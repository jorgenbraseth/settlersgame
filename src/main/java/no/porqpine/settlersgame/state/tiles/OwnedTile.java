package no.porqpine.settlersgame.state.tiles;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Player;

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
        boolean hasOwnersPheromoneFromOtherSource = getPAmounts().entrySet().stream()
                .filter(e -> e.getKey().source != this)
                .filter(e -> e.getKey().player.isPresent() && e.getKey().player.get() == owner)
                .findFirst().isPresent();

        if ((hasOwnersPheromoneFromOtherSource || getType().equals("HOME")) && getHighestPheromonePlayer() == owner) {
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
