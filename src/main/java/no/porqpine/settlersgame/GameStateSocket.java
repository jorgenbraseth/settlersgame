package no.porqpine.settlersgame;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import static no.porqpine.settlersgame.GameLogic.GAME;

@WebSocket
public class GameStateSocket extends WebSocketAdapter {

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
}
