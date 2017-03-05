package no.porqpine.settlersgame.state;

public abstract class OwnedTile extends Tile {
    public final Player owner;

    public OwnedTile(int x, int y, Player owner) {
        super(x, y);
        this.owner = owner;
    }
}
