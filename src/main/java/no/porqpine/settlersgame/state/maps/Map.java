package no.porqpine.settlersgame.state.maps;

import no.porqpine.settlersgame.state.tiles.FreeTile;
import no.porqpine.settlersgame.state.tiles.ProducerTile;
import no.porqpine.settlersgame.state.tiles.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Map {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];

    private List<Coord> startTilesPositions = new ArrayList<>();
    private List<Coord> producerPositions = new ArrayList<>();
    private List<Coord> wallPositions = new ArrayList<>();

    public Tile[][] getTiles() {
        return tiles;
    }

    public Map() {
        startTilesPositions.addAll(Arrays.asList(
                new Coord(3,3),
                new Coord(26,26),
                new Coord(3,26),
                new Coord(26,3)
        ));

        producerPositions.addAll(Arrays.asList(
                new Coord(5,5),
                new Coord(24,24),
                new Coord(5,25),
                new Coord(24,5),

                new Coord(14,14),
                new Coord(15,14),
                new Coord(13,15),
                new Coord(15,15),
                new Coord(14,16),
                new Coord(15,16)
        ));

        wallPositions.addAll(Arrays.asList(
                new Coord(14,15)
        ));

        makeTiles();
        calculateNeighbours();
    }

    private void makeTiles() {
        //Create tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                int finalX = x;
                int finalY = y;
                Tile newTile;
                if(producerPositions.stream().anyMatch(coord -> coord.isAt(finalX, finalY))) {
                    newTile = new ProducerTile(x, y);
                }else{
                    newTile = new FreeTile(x, y);
                }
                tiles[x][y] = newTile;
            }
        }
    }

    public List<Coord> getStartTilesPositions() {
        return startTilesPositions;
    }

    private void calculateNeighbours() {
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

    public static class Coord {
        public final int x;
        public final int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isAt(int x, int y){
            return x == this.x && y == this.y;
        }
    }
}
