package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.state.tiles.Tile;

import java.util.List;
import java.util.Objects;

public class Pheromone {

    public final String type;
    public final double amount;
    public final double degradationRate;
    public final double diffusionRate;
    public final Player owner;
    public final Tile source;

    public Pheromone(Tile source, String type, double amount, double degradationRate, double diffusionRate, Player owner) {
        this.source = source;
        this.type = type;
        this.amount = amount;
        this.owner = owner;
        this.degradationRate = degradationRate;
        this.diffusionRate = diffusionRate;
    }

    public void diffuse(Tile source, List<Tile> targets) {
        if(amount > 1){


        double pheromoneToDiffuse = amount * diffusionRate;
        final double amountForEachNeighbour = pheromoneToDiffuse / targets.size();

        targets.stream().map(tile -> {
                    Pheromone sentPheromone = copyWithAmount(amountForEachNeighbour);
                    return tile.queuePheromone(source, sentPheromone);
                }).mapToDouble(Double::doubleValue).sum();
        }
    }

    public Pheromone add(Pheromone toAdd) {
        if (!Objects.equals(toAdd.type, type)) {
            throw new RuntimeException("Tried to add two different types of pheromone!");
        }

        return copyWithAmount(toAdd.amount + amount);
    }

    public Pheromone diffused() {
        return copyWithAmount((long) (amount * (1 - diffusionRate)));
    }

    public Pheromone copyWithAmount(double amount) {
        return new Pheromone(source, type, amount, degradationRate, diffusionRate, owner);
    }

    public Pheromone degraded() {
        return copyWithAmount((int) (-1 * Math.ceil(amount * degradationRate)));
    }
}
