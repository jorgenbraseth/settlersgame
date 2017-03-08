package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.porqpine.settlersgame.GameLogic;

public abstract class OwnedTile extends Tile {
    public Player owner;
    private final GameLogic game;

    @JsonProperty
    public final long MAX_HEALTH = 1000;
    public long health = MAX_HEALTH;


    public OwnedTile(int x, int y, Player owner, GameLogic game) {
        super(x, y);
        this.owner = owner;
        this.game = game;
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

    @Override
    public void tick(int i) {
        if(getHighestPheromonePlayer() != owner){
            health -= 10;
        }
        if(health <= 0){
            die();
        }
    }

    private void die() {
        game.destroyTile(this);
    }


}
