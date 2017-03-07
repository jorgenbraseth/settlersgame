package no.porqpine.settlersgame.state;

import java.util.Optional;

public class PheromoneType {
    public static final PheromoneType RESOURCE = new PheromoneType("resource", 0.001, 1.0);

    public final String type;
    public final double degradationRate;
    public final double diffusionRate;
    public final Optional<Player> player;

    PheromoneType(String type, double degradationRate, double diffusionRate) {
        this(type, degradationRate, diffusionRate, null);
    }

    PheromoneType(String type, double degradationRate, double diffusionRate, Player player) {
        this.type = type;
        this.degradationRate = degradationRate;
        this.diffusionRate = diffusionRate;
        this.player = Optional.ofNullable(player);
    }

    public static PheromoneType playerPheromone(Player player) {
        return new PheromoneType(player.name, 0.05, 0.5, player);
    }

    @Override
    public String toString() {
        return type;
    }
}