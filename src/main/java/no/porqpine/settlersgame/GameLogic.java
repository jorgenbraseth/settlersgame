package no.porqpine.settlersgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.porqpine.settlersgame.api.ShapeClicked;
import no.porqpine.settlersgame.state.GameObject;
import no.porqpine.settlersgame.state.GameState;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameLogic implements Runnable {

    public static final GameLogic GAME = new GameLogic();
    public boolean running = true;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
    }
    private GameState state;


    private GameLogic() {
        init();

    }

    private void init() {
        this.state = new GameState();
    }

    List<Session> listeningConnections = new ArrayList<>();

    public void addPlayer(Session player) {
        listeningConnections.add(player);
    }

    public void run() {
        while (running) {
            clearDeadConnections();
            tick();
            publishGameState();

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        state.getTiles().forEach(tile -> tile.tick(1));
    }

    private void publishGameState() {
        try {
            sendToAllPlayers(OBJECT_MAPPER.writeValueAsString(state));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllPlayers(String text) {
        listeningConnections.stream().filter(Session::isOpen)
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(text);
                    } catch (IOException | WebSocketException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void clearDeadConnections() {
        ArrayList<Session> remainingConnections = new ArrayList<>();
        remainingConnections.addAll(
                listeningConnections.stream().filter(Session::isOpen).collect(Collectors.toList())
        );
        listeningConnections = remainingConnections;
    }

    public void stop() {
        listeningConnections.forEach(session -> session.close(0, "Server shutting down."));

        while (listeningConnections.stream().filter(session -> session.isOpen()).findAny().isPresent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

    public void shapeClicked(ShapeClicked event) {
        state.find(event.id).click(event);
    }
}
