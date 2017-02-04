package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.GameLogic;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.Optional;
import java.util.Random;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Tile extends GameObject {

    private static Random rnd = new Random();

    public static final int PAYOUT = 1;
    public final int x;
    public final int y;
    public final TileType type;
    public final Integer resourceOn;

    private Edge N;
    private Edge S;
    private Edge W;
    private Edge E;

    private Optional<Crossing> NW = Optional.empty();
    private Optional<Crossing> NE = Optional.empty();
    private Optional<Crossing> SW = Optional.empty();
    private Optional<Crossing> SE = Optional.empty();

    public Tile(int id, int x, int y) {
        this(id, x, y, TileType.sample());
    }

    public Tile(int id, int x, int y, TileType type) {
        super(id);
        this.x = x;
        this.y = y;
        this.type = type;
        if(type != TileType.WATER){
            this.resourceOn = 1+rnd.nextInt(11);
        }else{
            this.resourceOn = null;
        }
    }

    public void tick(int ticks) {
        if (resourceOn != null && GameLogic.GAME.state.currentRoll == resourceOn) {
            giveResources();
        }
    }

    private void giveResources() {
        NW.ifPresent(this::payout);
        NE.ifPresent(this::payout);
        SW.ifPresent(this::payout);
        SE.ifPresent(this::payout);
    }

    private void payout(Crossing crossing) {
        Structure structure = crossing.structure;
        if(structure != null){
            structure.owner.addResource(this.type.name(), PAYOUT);
        }
    }

    public Double getProduction() {
        if (type == TileType.WATER) {
            return null;
        }
        return GameLogic.GAME.state.currentRoll == resourceOn ? 1d : 0d;
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

    public void setNW(Crossing crossing) {
        this.NW = Optional.ofNullable(crossing);
    }

    public void setNE(Crossing crossing) {
        this.NE = Optional.ofNullable(crossing);
    }

    public void setSW(Crossing crossing) {
        this.SW = Optional.ofNullable(crossing);
    }

    public void setSE(Crossing crossing) {
        this.SE = Optional.ofNullable(crossing);
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

    public Optional<Crossing> getNW() {
        return NW;
    }

    public Optional<Crossing> getNE() {
        return NE;
    }

    public Optional<Crossing> getSW() {
        return SW;
    }

    public Optional<Crossing> getSE() {
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
