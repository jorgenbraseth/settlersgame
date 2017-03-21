package no.porqpine.settlersgame.state.tiles;

public class FreeTile extends Tile {
    public FreeTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return "FREE";
    }

}
