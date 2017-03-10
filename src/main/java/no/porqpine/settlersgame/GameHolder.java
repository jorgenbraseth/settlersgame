package no.porqpine.settlersgame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameHolder implements Runnable {

    public static final GameHolder GAME_LIST = new GameHolder();

    private static Map<String, Game> games = new HashMap<>();
    private boolean running;

    private GameHolder() {
        running = true;
    }

    public Game createGame(String gameId) {
        Game newGame = new Game(gameId);
        games.put(newGame.gameId, newGame);
        return newGame;
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public void run() {
        while (running) {
            games.values().forEach(game -> {
                game.clearDeadConnections();
                game.tick();
                game.publishGameState();
            });

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        games.values().forEach(Game::stop);
    }

    public Game getOrCreateGame(String gameId) {
        return games.computeIfAbsent(gameId, this::createGame);
    }

    public Collection<Game> games() {
        return games.values();
    }
}
