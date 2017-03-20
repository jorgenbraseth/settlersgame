package no.porqpine.settlersgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.porqpine.settlersgame.api.messages.Build;
import no.porqpine.settlersgame.state.GameState;
import no.porqpine.settlersgame.state.Player;
import no.porqpine.settlersgame.state.tiles.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Game {
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public final String gameId;

    //TODO: move to own class
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

            state.addPlayer(player);
        }
    }

    public void tick() {
        state.getTiles().forEach(Tile::diffuse);
        state.getTiles().forEach(Tile::degrade);
        state.getTiles().forEach(Tile::calculateNewPheromoneAmounts);
        state.getTiles().forEach(tile -> tile.tick(1));
    }

    public void publishGameState() {

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

                        log.info("Player disconnected: [{}]", player.name);
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
        sessions.forEach(player -> {
            player.close(0, "Server shutting down.");
            try {
                player.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        while (sessions.stream().filter(Session::isOpen).findAny().isPresent()) {
            log.error("Not all client sessions stopped");
            try {
                Thread.sleep(330);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("Game [{}] stopped.", gameId);

    }

    public void build(Build buildCommand) {
        Tile clickedTile = (Tile) state.find(buildCommand.buildOnTileId);
        Player player = findPlayer(buildCommand.playerName);
        boolean playerHasHighestPheromoneOnTile = clickedTile.getHighestPheromonePlayer() == player;
        boolean isClickedTileFree = Objects.equals(clickedTile.getType(), "FREE");

        if (playerHasHighestPheromoneOnTile && isClickedTileFree) {
            switch (buildCommand.tileToBuild) {
                case "SIPHON":
                    state.build(new SiphonTile(clickedTile.x, clickedTile.y, player, this));
                    break;
                case "RELAY":
                    state.build(new RelayTile(clickedTile.x, clickedTile.y, player, this));
                    break;
                case "WALL":
                    state.build(new BlockerTile(clickedTile.x, clickedTile.y, player, this));
                    break;
            }
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
