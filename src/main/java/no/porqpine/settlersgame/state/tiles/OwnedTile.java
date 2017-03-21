package no.porqpine.settlersgame.state.tiles;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Player;

public abstract class OwnedTile extends Tile {
    public Player owner;
    private final Game game;

    @JsonProperty
    public final long MAX_HEALTH;
    public long health;


    public OwnedTile(int x, int y, Player owner, Game game, long maxHealth) {
        super(x, y);
        this.owner = owner;
        this.game = game;
        health = maxHealth;
        MAX_HEALTH = maxHealth;
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

        if (getHighestPheromonePlayer() == owner) {
            health = Math.min(MAX_HEALTH, health + 5);
        }else{
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
