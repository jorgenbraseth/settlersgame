package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.porqpine.settlersgame.api.MessageType.GAME_STATE;
import static no.porqpine.settlersgame.state.Edge.Orientation.HORIZONTAL;
import static no.porqpine.settlersgame.state.Edge.Orientation.VERTICAL;

public class GameState {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    public List<Player> players = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();
    public List<Crossing> crossings = new ArrayList<>();
    public MessageType type = GAME_STATE;

    public GameState() {
        createMap();
    }

    private void createMap() {
        int id = 0;

        //Create tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if(x == 0 || y == 0 || x == WIDTH-1 || y == HEIGHT -1){
                    tiles[x][y] = new Tile(id++, x, y, Tile.TileType.WATER);
                }else{
                    tiles[x][y] = new Tile(id++, x, y);
                }
            }
        }

        //Add Crossings and Edges

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Tile currentTile = tiles[x][y];
                Tile east = null;
                Tile south = null;
                Tile southEast = null;
                if (x < WIDTH - 1) {
                    east = tiles[x + 1][y];
                    if(!(east.type == Tile.TileType.WATER && currentTile.type == Tile.TileType.WATER)){
                        Edge edge = new Edge(id++, currentTile, east, VERTICAL);
                        edges.add(edge);
                        currentTile.setE(edge);
                        east.setW(edge);
                    }
                }

                if (y > 0) {
                    Tile north = tiles[x][y - 1];
                    if(!(north.type == Tile.TileType.WATER && currentTile.type == Tile.TileType.WATER)){
                        Edge edge = new Edge(id++, north, currentTile, HORIZONTAL);
                        edges.add(edge);
                        currentTile.setN(edge);
                        north.setS(edge);
                    }
                }

                if (x < WIDTH - 1 && y < HEIGHT - 1) {
                    southEast = tiles[x + 1][y + 1];
                    south = tiles[x][y + 1];
                    Crossing crossing = new Crossing(id++, southEast, south, east, currentTile);
                    crossings.add(crossing);
                }

            }
        }
    }

    public List<Tile> getTiles() {
        return Stream.of(tiles).flatMap(row -> Stream.of(row)).collect(Collectors.toList());
    }

    public GameObject find(int id) {
        List<GameObject> allObjects = new ArrayList<>();
        allObjects.addAll(getTiles());
        allObjects.addAll(edges);
        allObjects.addAll(crossings);
        return allObjects.stream().filter(tile -> tile.id == id).findFirst().orElseThrow(() -> new RuntimeException("No gameObject exists with id: "+id));
    }
}
