package no.porqpine.settlersgame.state.tiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Player;

import java.util.Optional;

public abstract class OwnedTile extends Tile {
    public Player owner;
    private final Game game;

    @JsonIgnore
    public int distanceToHome;

    @JsonProperty
    public final long MAX_HEALTH;
    public long health;
    public OwnedTile pheromoneSupplier;


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

        if (getHighestPheromonePlayer() == owner && distanceToHome < Integer.MAX_VALUE) {
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

    @Override
    public void calculateNewPheromoneAmounts() {
        Optional<OwnedTile> pheromoneSupplier = pQueued.stream()
                .filter(pheromone -> pheromone.owner == owner)
                .map(pheromone -> (OwnedTile) pheromone.source)
                .filter(source -> source.distanceToHome < distanceToHome)
                .min((o1, o2) -> Integer.compare(o1.distanceToHome, o2.distanceToHome));
        this.pheromoneSupplier = pheromoneSupplier.orElse(null);

        distanceToHome = pheromoneSupplier.map(ownedTile -> ownedTile.distanceToHome + 1).orElse(Integer.MAX_VALUE);

        super.calculateNewPheromoneAmounts();

    }

    @JsonProperty
    public Integer getDistanceToHome() {
        return distanceToHome < Integer.MAX_VALUE ? distanceToHome : null;
    }
}
