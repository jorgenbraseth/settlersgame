package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OwnedTile extends Tile {
    public Player owner;

    @JsonProperty
    public final long MAX_HEALTH = 0;


    public OwnedTile(int x, int y, Player owner) {
        super(x, y);
        this.owner = owner;
    }

    public long getHealth(){
        return MAX_HEALTH;
    }

    @Override
    public String toString() {
        return "OwnedTile{" +
                "owner=" + owner.name +
                ", MAX_HEALTH=" + MAX_HEALTH +
                "} " + super.toString();
    }

    public abstract long cost();
}
