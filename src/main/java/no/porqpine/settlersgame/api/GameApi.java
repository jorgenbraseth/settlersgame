package no.porqpine.settlersgame.api;

import no.porqpine.settlersgame.GameLogic;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

import static no.porqpine.settlersgame.GameLogic.GAME;

@WebSocket
public class GameApi extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        GAME.addPlayer(getSession());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        GAME.clearDeadConnections();
        GAME.sendToAllPlayers("Player Left");
    }

    @Override
    public void onWebSocketText(String message) {

        try {
            ShapeClicked event = GameLogic.OBJECT_MAPPER.readValue(message, ShapeClicked.class);
            GAME.shapeClicked(event);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
