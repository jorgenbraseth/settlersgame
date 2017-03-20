package no.porqpine.settlersgame.api;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.api.messages.*;
import no.porqpine.settlersgame.state.Player;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static no.porqpine.settlersgame.Game.OBJECT_MAPPER;
import static no.porqpine.settlersgame.GameHolder.GAME_LIST;

@WebSocket
public class GameApi extends WebSocketAdapter {

    private static Logger log = LoggerFactory.getLogger(GameApi.class);

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
            log.info("Got message: {}", message);
            Game game;
            switch (metadata.type) {
                case CREATE_GAME:
                    handleCreateGame();
                    break;
                case JOIN_GAME:
                    game = GAME_LIST.getOrCreateGame(metadata.gameId);
                    handleJoinGame(message, game);
                    break;
                case CHAT:
                    game = GAME_LIST.getGame(metadata.gameId);
                    handleChatMessage(message, game);
                    break;
                case LIST_GAMES:
                    handleListGames();
                    break;
                case BUILD:
                    game = GAME_LIST.getGame(metadata.gameId);
                    handleBuild(message, game);
                    break;
                default:
                    log.error("Unhandled message type received: {}", metadata.type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleBuild(String message, Game game) throws IOException {
        Build build = OBJECT_MAPPER.readValue(message, Build.class);
        game.build(build);
    }

    private void handleListGames() throws IOException {
        getSession().getRemote().sendString(OBJECT_MAPPER.writeValueAsString(new GameList(GAME_LIST.games())));
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
