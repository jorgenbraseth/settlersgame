package no.porqpine.settlersgame.api;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.api.messages.*;
import no.porqpine.settlersgame.state.Player;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

import static no.porqpine.settlersgame.Game.OBJECT_MAPPER;
import static no.porqpine.settlersgame.GameList.GAME_LIST;

@WebSocket
public class GameApi extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketText(String message) {

        try {
            MessageMetadata metadata = Game.OBJECT_MAPPER.readValue(message, MessageMetadata.class);
            Game game = GAME_LIST.getOrCreateGame(metadata.gameId);
            System.out.println("Got message: "+message);
            switch (metadata.type){
                case SHAPE_CLICKED:
                    ShapeClicked shapeClicked = Game.OBJECT_MAPPER.readValue(message, ShapeClicked.class);
                    game.shapeClicked(shapeClicked);
                    break;
                case SHAPE_RIGHT_CLICKED:
                    ShapeRightClicked shapeRightClicked = Game.OBJECT_MAPPER.readValue(message, ShapeRightClicked.class);
                    game.shapeRightClicked(shapeRightClicked);
                    break;
                case CREATE_GAME:
                    getSession().getRemote().sendString(Game.OBJECT_MAPPER.writeValueAsString(new GameCreated("game_name")));
                    break;
                case JOIN_GAME:
                    JoinGame joinGame = Game.OBJECT_MAPPER.readValue(message, JoinGame.class);
                    game.addPlayer(getSession(),joinGame.name,joinGame.color);
                    break;
                case CHAT:
                    Chat chat = Game.OBJECT_MAPPER.readValue(message, Chat.class);
                    Player player = game.findPlayer(chat.playerName);
                    chat.player = player;
                    game.sendToAllPlayers(OBJECT_MAPPER.writeValueAsString(chat));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class MessageMetadata {
        public MessageType type;
        public String gameId;

        public MessageMetadata() {}
    }


}
