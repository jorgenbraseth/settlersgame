package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.Random;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Tile extends GameObject{

    public final int x;
    public final int y;
    public final TileType type;

    private Edge N;
    private Edge S;
    private Edge W;
    private Edge E;

    private Crossing NW;
    private Crossing NE;
    private Crossing SW;
    private Crossing SE;
    public int timer;
    private double tickChance = 1;
    private int maxTime;

    public Tile(int id, int x, int y) {
        this(id,x,y,TileType.sample());
    }

    public Tile(int id, int x, int y, TileType type) {
        super(id);
        this.x = x;
        this.y = y;
        this.type = type;

        this.maxTime = (int) (Math.random() * 50) + 50;
        this.timer = maxTime;
    }

    public void tick(int ticks) {
        if (timer == 0 || Math.random() < tickChance) {
            timer -= ticks;
        }
        if (timer < 0) {
            timer = maxTime;
        }
    }

    public double getProduction() {
        if(type == TileType.WATER){
            return 1;
        }
        return 1.0 - (timer / (double) maxTime);
    }

    public void setN(Edge n) {
        N = n;
    }

    public void setS(Edge s) {
        S = s;
    }

    public void setW(Edge w) {
        W = w;
    }

    public void setE(Edge e) {
        E = e;
    }

    public void setNW(Crossing NW) {
        this.NW = NW;
    }

    public void setNE(Crossing NE) {
        this.NE = NE;
    }

    public void setSW(Crossing SW) {
        this.SW = SW;
    }

    public void setSE(Crossing SE) {
        this.SE = SE;
    }

    public Edge getN() {
        return N;
    }

    public Edge getS() {
        return S;
    }

    public Edge getW() {
        return W;
    }

    public Edge getE() {
        return E;
    }

    public Crossing getNW() {
        return NW;
    }

    public Crossing getNE() {
        return NE;
    }

    public Crossing getSW() {
        return SW;
    }

    public Crossing getSE() {
        return SE;
    }

    @Override
    public void click(ShapeClicked event) {

    }

    public enum TileType {
        FOREST, PASTURE, MOUNTAIN, WATER, DESERT;

        private static Random rnd = new Random();

        public static TileType sample() {
            return TileType.values()[rnd.nextInt(TileType.values().length)];
        }
    }
}
