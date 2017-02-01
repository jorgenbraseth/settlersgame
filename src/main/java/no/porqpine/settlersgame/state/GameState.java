package no.porqpine.settlersgame.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.porqpine.settlersgame.state.Edge.Orientation.VERTICAL;

public class GameState {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    public List<Player> players = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();
    public List<Crossing> crossings = new ArrayList<>();

    public GameState() {
        int id = 0;

        //Create tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(id++, x, y);
            }
        }

        //Add Crossings and Edges

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Tile tile = tiles[x][y];
                if (x > 0) {
                    Tile west = tiles[x - 1][y];
                    Edge edge = new Edge(id++, west, tile, VERTICAL);
                    tile.setW(edge);
                    west.setE(edge);
                }
                if (x < WIDTH-1) {
                    Tile east = tiles[x + 1][y];
                    Edge edge = new Edge(id++, tile, east, VERTICAL);
                    tile.setE(edge);
                    east.setW(edge);
                }
            }
        }


        IntStream.range(0, 81).boxed()
                .forEach(i -> crossings.add(new Crossing(i, i % 9, i / 9)));


    }

    public List<Tile> getTiles() {
        return Stream.of(tiles).flatMap(row -> Stream.of(row)).collect(Collectors.toList());
    }
}
