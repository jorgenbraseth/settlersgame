package no.porqpine.settlersgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.porqpine.settlersgame.api.messages.ShapeClicked;
import no.porqpine.settlersgame.api.messages.ShapeRightClicked;
import no.porqpine.settlersgame.exceptions.InvalidObjectID;
import no.porqpine.settlersgame.state.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game implements Runnable {

//    public static final Game GAME = new Game("gameId");
    public final String gameId;
    public boolean running = true;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GameState state;
    private long lastPublishTime;
    private List<Session> sessions = new ArrayList<>();


    public Game(String gameId) {
        this.gameId = gameId;
        init();
    }

    private void init() {
        this.state = new GameState(this);
    }


    public void addPlayer(Session connection, String name, String color) {
        Optional<Player> playerWithSameName = state.players.stream()
                .filter(p -> p.name.equals(name))
                .findFirst();

        if (playerWithSameName.isPresent()) {
            playerWithSameName.get().session = connection;
        } else {
            Player player = new Player(name, color, connection);
            player.setPheromone(PheromoneType.playerPheromone(player));

            state.addPlayer(player);
        }
    }

    public void run() {
        while (running) {
            clearDeadConnections();
            tick();
            publishGameState();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        state.roll();
        state.getTiles().forEach(tile -> tile.diffuse());
        state.getTiles().forEach(tile -> tile.degrade());
        state.getTiles().forEach(tile -> tile.acceptQueuedPheromone());
        state.getTiles().forEach(tile -> tile.tick(1));
    }

    private void publishGameState() {

        long timeSinceLastPublish = System.currentTimeMillis() - lastPublishTime;
        if (timeSinceLastPublish > 100) {
            lastPublishTime = System.currentTimeMillis();
            try {
                sendToAllPlayers(OBJECT_MAPPER.writeValueAsString(state));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendToAllPlayers(String text) {
        state.players.stream().filter(p -> p.session.isOpen())
                .forEach(player -> {
                    try {
                        player.session.getRemote().sendString(text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WebSocketException e) {
                        System.out.println(String.format("Player disconnected: [%s]", player.name));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void clearDeadConnections() {

//        ArrayList<Session> remainingConnections = new ArrayList<>();
//        remainingConnections.addAll(
//                state.players.stream().map(p -> p.session).filter(Session::isOpen).collect(Collectors.toList())
//        );
//        players = remainingConnections;
    }

    public void stop() {
        running = false;
        sessions.forEach(player -> {
            player.close(0, "Server shutting down.");
            try {
                player.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        while (state.players.stream().filter(p -> p.session.isOpen()).findAny().isPresent()) {
        while (sessions.stream().filter(p -> p.isOpen()).findAny().isPresent()) {
            System.out.println("Not all client sessions stopped");
            try {
                Thread.sleep(330);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game loop stopped.");

    }

    public void shapeRightClicked(ShapeRightClicked event) {
        Tile clickedTile = (Tile) state.find(event.id);
        int x = event.coords[0];
        int y = event.coords[1];
        Player player = findPlayer(event.playerName);

        Player highestPheromonePlayer = clickedTile.getHighestPheromonePlayer();
        if (highestPheromonePlayer == player || clickedTile instanceof OwnedTile && (((OwnedTile) clickedTile).owner == player)) {
            switch (clickedTile.getType()) {
                case "BLOCKER":
                    state.build(new FreeTile(x, y));
                    break;
                default:
                    state.build(new BlockerTile(x, y, player, this));
            }
        }
    }

    public void shapeClicked(ShapeClicked event) {
        try {
            Tile clickedTile = (Tile) state.find(event.id);
            System.out.println(clickedTile);
            int x = event.coords[0];
            int y = event.coords[1];
            Player player = findPlayer(event.playerName);

            Player highestPheromonePlayer = clickedTile.getHighestPheromonePlayer();
            if (highestPheromonePlayer == player) {
                switch (clickedTile.getType()) {
                    case "FREE":
                        state.build(new SiphonTile(x, y, player, this));
                        break;
                    case "SIPHON":
                        state.build(new RelayTile(x, y, player, this));
                        break;
                    case "OWNERSHIP_SPREADER":
                        state.build(new FreeTile(x, y));
                        break;
                }
            }
        } catch (InvalidObjectID e) {
            System.out.println("Invalid id, probably old state on client side.");
        }
    }

    public Player findPlayer(String playerName) {
        return state.players.stream().filter(p -> p.name.equals(playerName)).findFirst().orElse(null);
    }

    public void destroyTile(Tile ownedTile) {
        state.build(new FreeTile(ownedTile.x, ownedTile.y));
    }

    public void addSession(Session sess) {
        sessions.add(sess);
    }
}