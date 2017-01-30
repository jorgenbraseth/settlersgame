package no.porqpine.settlersgame;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import static no.porqpine.settlersgame.GameState.GAME_STATE;

@WebSocket
public class GameStateSocket extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        GAME_STATE.addPlayer(getSession());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        GAME_STATE.clearDeadConnections();
        GAME_STATE.sendToAllPlayers("Player Left");
    }
}
