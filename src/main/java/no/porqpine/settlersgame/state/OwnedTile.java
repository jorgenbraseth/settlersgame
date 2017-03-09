package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.porqpine.settlersgame.Game;

public abstract class OwnedTile extends Tile {
    public Player owner;
    private final Game game;

    @JsonProperty
    public final long MAX_HEALTH = 1000;
    public long health = MAX_HEALTH;


    public OwnedTile(int x, int y, Player owner, Game game) {
        super(x, y);
        this.owner = owner;
        this.game = game;
    }

    public long getHealth(){
        return health;
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
        }else{
            health = Math.min(MAX_HEALTH, health + 5);
        }
        if(health <= 0){
            die();
        }
    }

    private void die() {
        game.destroyTile(this);
    }


}
