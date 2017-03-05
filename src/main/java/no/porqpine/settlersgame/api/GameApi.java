package no.porqpine.settlersgame.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
//        GAME.addPlayer(getSession(), name, color);
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
            MessageWithOnlyType typeMessage = GameLogic.OBJECT_MAPPER.readValue(message, MessageWithOnlyType.class);
            System.out.println("Got message: "+message);
            switch (typeMessage.type){
                case SHAPE_CLICKED:
                    ShapeClicked shapeClicked = GameLogic.OBJECT_MAPPER.readValue(message, ShapeClicked.class);
                    GAME.shapeClicked(shapeClicked);
                    break;
                case SHAPE_RIGHT_CLICKED:
//                    ShapeClicked shapeClicked = GameLogic.OBJECT_MAPPER.readValue(message, ShapeClicked.class);
//                    GAME.shapeClicked(shapeClicked);
                    break;
                case JOIN_GAME:
                    JoinGame joinGame = GameLogic.OBJECT_MAPPER.readValue(message, JoinGame.class);
                    GAME.addPlayer(getSession(),joinGame.name,joinGame.color);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class MessageWithOnlyType {
        public MessageType type;

        public MessageWithOnlyType() {}
    }
}
