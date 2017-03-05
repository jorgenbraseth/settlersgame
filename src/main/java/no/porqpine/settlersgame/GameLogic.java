package no.porqpine.settlersgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.porqpine.settlersgame.api.ShapeClicked;
import no.porqpine.settlersgame.state.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.io.IOException;
import java.util.Optional;

public class GameLogic implements Runnable {

    public static final GameLogic GAME = new GameLogic();
    public boolean running = true;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GameState state;


    private GameLogic() {
        init();
    }

    private void init() {
        this.state = new GameState();
    }


    public void addPlayer(Session connection, String name, String color) {
        Optional<Player> playerWithSameName = state.players.stream()
                .filter(p -> p.name.equals(name))
                .findFirst();

        if(playerWithSameName.isPresent()){
            playerWithSameName.get().session = connection;
        }else{
            Player player = new Player(name, color, connection);
            state.players.add(player);
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
        state.getTiles().forEach(tile -> tile.tick(1));
        state.getTiles().forEach(tile -> tile.acceptQueuedPheromone());
    }

    private void publishGameState() {
        try {
            sendToAllPlayers(OBJECT_MAPPER.writeValueAsString(state));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllPlayers(String text) {
        state.players.stream().map(p -> p.session).filter(Session::isOpen)
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(text);
                    } catch (IOException | WebSocketException e) {
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
        state.players.forEach(player -> player.session.close(0, "Server shutting down."));

        while (state.players.stream().filter(p -> p.session.isOpen()).findAny().isPresent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

    public void shapeClicked(ShapeClicked event) {
        Tile clickedShape = (Tile)state.find(event.id);
        int x = event.coords[0];
        int y = event.coords[1];
        Player player = findPlayer(event.playerName);
        switch (clickedShape.getType()){
            case "FREE":
                state.build(new BlockerTile(x,y));
                break;
            case "BLOCKER":
                state.build(new ProducerTile(x,y, player));
                break;
            case "PRODUCER":
                state.build(new ConsumerTile(x,y, player));
                break;
            case "CONSUMER":
                state.build(new FreeTile(x,y));
                break;
        }

    }

    public Player findPlayer(String playerName) {
        return state.players.stream().filter(p -> p.name.equals(playerName)).findFirst().orElse(null);
    }
}
