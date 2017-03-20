package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.api.messages.MessageType;
import no.porqpine.settlersgame.exceptions.InvalidObjectID;
import no.porqpine.settlersgame.state.maps.Map;
import no.porqpine.settlersgame.state.tiles.HomeTile;
import no.porqpine.settlersgame.state.tiles.OwnedTile;
import no.porqpine.settlersgame.state.tiles.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.porqpine.settlersgame.api.messages.MessageType.GAME_STATE;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class GameState {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;

    private static Random rnd = new Random();
    private final Game game;
    private final Map initialMap;

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    public List<Player> players = new ArrayList<>();
    public MessageType type = GAME_STATE;

    public GameState(Game game) {
        this.game = game;
        initialMap = new Map();
        tiles = initialMap.getTiles();
    }

    public Tile getFreeStartTile() {
        return initialMap.getStartTilesPositions().stream()
                .map(coord -> tiles[coord.x][coord.y])
                .filter(tile -> tile.getType().equals("FREE"))
                .findAny().orElse(null);
    }

    public List<Tile> getTiles() {
        return Stream.of(tiles).flatMap(row -> Stream.of(row)).collect(Collectors.toList());
    }

    public GameObject find(long id) throws InvalidObjectID {
        List<GameObject> allObjects = new ArrayList<>();
        allObjects.addAll(getTiles());
        return allObjects.stream().filter(tile -> tile.id == id).findFirst().orElseThrow(() -> new InvalidObjectID(id));
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
        if (newTile instanceof OwnedTile) {
            OwnedTile ownedTile = (OwnedTile) newTile;
            Player owner = ownedTile.owner;
            long costOfTile = ownedTile.cost();
            if (owner.canAfford(costOfTile)) {
                owner.addResource("resource", -costOfTile);
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
        Tile startTile = getFreeStartTile();
        if(startTile != null){
            players.add(player);
            replaceTile(startTile, new HomeTile(startTile.x, startTile.y, player, game));
        }
    }
}
