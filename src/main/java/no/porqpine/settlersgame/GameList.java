package no.porqpine.settlersgame;

import java.util.HashMap;
import java.util.Map;

public class GameList {

    private static Map<String, Game> games = new HashMap<>();

    public static Game createGame(String gameId) {
        Game newGame = new Game(gameId);
        games.put(newGame.gameId, newGame);
        return newGame;
    }

    public static Game getGame(String gameId) {
        return games.get(gameId);
    }
}
