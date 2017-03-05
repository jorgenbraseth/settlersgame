package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.porqpine.settlersgame.api.MessageType.GAME_STATE;

public class GameState {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private static Random rnd = new Random();

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    public List<Player> players = new ArrayList<>();
    public MessageType type = GAME_STATE;
    public int currentRoll;
    private int nextTileId;

    public GameState() {
        createMap();
    }

    private void createMap() {
        nextTileId = 0;

        //Create tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                Tile newTile = new FreeTile(x, y);
                tiles[x][y] = newTile;
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Tile currentTile = tiles[x][y];
                if (x > 0) {
                    currentTile.addNeighbour(tiles[x - 1][y]);
                }
                if (x < WIDTH - 1) {
                    currentTile.addNeighbour(tiles[x + 1][y]);
                }

                if (y > 0) {
                    currentTile.addNeighbour(tiles[x][y - 1]);
                }
                if (y < HEIGHT - 1) {
                    currentTile.addNeighbour(tiles[x][y + 1]);
                }

                if (y % 2 == 0 && x > 0) { //Partallsrader, samme og -1
                    if (y > 0) {
                        currentTile.addNeighbour(tiles[x - 1][y - 1]);
                    }
                    if (y < HEIGHT - 1) {
                        currentTile.addNeighbour(tiles[x - 1][y + 1]);
                    }
                }
                if (y % 2 == 1 && x < WIDTH - 1) {  //Oddetallsrader, samme og +1
                    if (y > 0) {
                        currentTile.addNeighbour(tiles[x + 1][y - 1]);
                    }
                    if (y < HEIGHT - 1) {
                        currentTile.addNeighbour(tiles[x + 1][y + 1]);
                    }
                }

            }
        }


    }

    public List<Tile> getTiles() {
        return Stream.of(tiles).flatMap(row -> Stream.of(row)).collect(Collectors.toList());
    }

    public Player getPlayer(String playerName) {
        return players.stream().filter(p -> p.name.equals(playerName)).findFirst().orElse(null);
    }

    public GameObject find(int id) {
        List<GameObject> allObjects = new ArrayList<>();
        allObjects.addAll(getTiles());
        return allObjects.stream().filter(tile -> tile.id == id).findFirst().orElseThrow(() -> new RuntimeException("No gameObject exists with id: " + id));
    }

    public void roll() {
        this.currentRoll = rnd.nextInt(6) + rnd.nextInt(6);
    }

    private void replaceTile(Tile tile, Tile newTile) {
        Tile oldTile = tile;
        oldTile.neighbours.forEach(n -> {
            newTile.addNeighbour(n);
            n.replaceNeighbour(oldTile, newTile);
        });
        tiles[newTile.x][newTile.y] = newTile;
    }

    public void build(Tile newTile) {
        replaceTile(tiles[newTile.x][newTile.y], newTile);
    }
}
