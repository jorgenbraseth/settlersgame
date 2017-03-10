package no.porqpine.settlersgame.api;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.api.messages.*;
import no.porqpine.settlersgame.state.Player;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

import static no.porqpine.settlersgame.Game.OBJECT_MAPPER;
import static no.porqpine.settlersgame.GameHolder.GAME_LIST;

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
            MessageMetadata metadata = OBJECT_MAPPER.readValue(message, MessageMetadata.class);
            Game game = GAME_LIST.getOrCreateGame(metadata.gameId);
            System.out.println("Got message: " + message);
            switch (metadata.type) {
                case SHAPE_CLICKED:
                    handleLeftClick(message, game);
                    break;
                case SHAPE_RIGHT_CLICKED:
                    handleRightClick(message, game);
                    break;
                case CREATE_GAME:
                    handleCreateGame();
                    break;
                case JOIN_GAME:
                    handleJoinGame(message, game);
                    break;
                case CHAT:
                    handleChatMessage(message, game);
                    break;
                case LIST_GAMES:
                    handleListGames();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleListGames() throws IOException {
        getSession().getRemote().sendString(OBJECT_MAPPER.writeValueAsString(new GameList(GAME_LIST.games())));
    }

    private void handleLeftClick(String message, Game game) throws IOException {
        ShapeClicked shapeClicked = OBJECT_MAPPER.readValue(message, ShapeClicked.class);
        game.shapeClicked(shapeClicked);
    }

    private void handleRightClick(String message, Game game) throws IOException {
        ShapeRightClicked shapeRightClicked = OBJECT_MAPPER.readValue(message, ShapeRightClicked.class);
        game.shapeRightClicked(shapeRightClicked);
    }

    private void handleCreateGame() throws IOException {
        getSession().getRemote().sendString(OBJECT_MAPPER.writeValueAsString(new GameCreated("game_name")));
    }

    private void handleJoinGame(String message, Game game) throws IOException {
        JoinGame joinGame = OBJECT_MAPPER.readValue(message, JoinGame.class);
        game.addPlayer(getSession(), joinGame.name, joinGame.color);
    }

    private void handleChatMessage(String message, Game game) throws IOException {
        Chat chat = OBJECT_MAPPER.readValue(message, Chat.class);
        Player player = game.findPlayer(chat.playerName);
        chat.player = player;
        game.sendToAllPlayers(OBJECT_MAPPER.writeValueAsString(chat));
    }

    public static class MessageMetadata {
        public MessageType type;
        public String gameId;

        public MessageMetadata() {
        }
    }


}
