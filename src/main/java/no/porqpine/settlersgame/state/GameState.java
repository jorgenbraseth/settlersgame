package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.GameLogic;
import no.porqpine.settlersgame.api.MessageType;
import no.porqpine.settlersgame.exceptions.InvalidObjectID;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.porqpine.settlersgame.api.MessageType.GAME_STATE;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class GameState {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;

    private static Random rnd = new Random();
    private final GameLogic game;

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    public List<Player> players = new ArrayList<>();
    public MessageType type = GAME_STATE;
    public int currentRoll;

    public GameState(GameLogic game) {
        this.game = game;
        createMap();
    }

    private void createMap() {
        //Create tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                Tile newTile = new FreeTile(x, y);
                if (Math.random() < 0.02) {
                    newTile = new ProducerTile(x, y);
                }
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

        //TODO: wrap around?

    }

    public List<Tile> getTiles() {
        return Stream.of(tiles).flatMap(row -> Stream.of(row)).collect(Collectors.toList());
    }

    public GameObject find(long id) throws InvalidObjectID {
        List<GameObject> allObjects = new ArrayList<>();
        allObjects.addAll(getTiles());
        return allObjects.stream().filter(tile -> tile.id == id).findFirst().orElseThrow(() -> new InvalidObjectID(id));
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
        if(newTile instanceof OwnedTile){
            OwnedTile ownedTile = (OwnedTile) newTile;
            Player owner = ownedTile.owner;
            long costOfTile = ownedTile.cost();
            if (owner.canAfford(costOfTile)) {
                owner.addResource("resource",-costOfTile);
            } else {
                return;
            }
        }
        replaceTile(tiles[newTile.x][newTile.y], newTile);
    }

    public Tile tileAt(int x, int y) {
        return tiles[x][y];
    }

    public void addPlayer(Player player) {
        players.add(player);
        Tile tile = tiles[(int) (Math.random() * WIDTH)][(int) (Math.random() * HEIGHT)];
        while (tile instanceof OwnedTile) {
            tile = tiles[(int) (Math.random() * WIDTH)][(int) (Math.random() * HEIGHT)];
        }

        replaceTile(tile, new HomeTile(tile.x, tile.y, player, game));
    }
}
