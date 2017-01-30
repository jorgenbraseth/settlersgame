package no.porqpine.settlersgame;

import java.util.Random;

public class Tile {

    public final int id;
    public final int x;
    public final int y;
    public final TileType type;
    private Tile tileToTheNorth;
    private Tile tileToTheSouth;
    private Tile tileToTheWest;
    private Tile tilToTheEast;
    public int timer;
    private double tickChance = 0.25;

    public Tile(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        type = TileType.sample();
        this.timer = 10;
    }

    public void tick(int ticks){
        if(timer == 0 || Math.random() < tickChance){
            timer -= ticks;
        }
        if(timer < 0){
            timer = 10;
        }
    }

    public void setNorth(Tile tileToTheNorth){
        this.tileToTheNorth = tileToTheNorth;
    }
    public void setSouth(Tile tileToTheSouth){
        this.tileToTheSouth = tileToTheSouth;
    }
    public void setWest(Tile tileToTheWest){
        this.tileToTheWest = tileToTheWest;
    }
    public void setEast(Tile tilToTheEast){
        this.tilToTheEast = tilToTheEast;
    }



    public enum TileType {
        FOREST,PASTURE,MOUNTAIN,WATER,DESERT;

        private static Random rnd = new Random();
        public static TileType sample(){
            return TileType.values()[rnd.nextInt(TileType.values().length)];
        }
    }
}
