package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.state.tiles.Tile;

import java.util.Optional;

public class PheromoneType {
    public static final String RESOURCE_TYPE = "resource";
//    public static final PheromoneType RESOURCE = new PheromoneType("resource", 0.001, 1.0);

    public final String type;
    public final double degradationRate;
    public final double diffusionRate;
    public final Optional<Player> player;
    public final Tile source;

    PheromoneType(String type, double degradationRate, double diffusionRate, Tile source) {
        this(type, degradationRate, diffusionRate, source, null);
    }

    PheromoneType(String type, double degradationRate, double diffusionRate, Tile source, Player player) {
        this.type = type;
        this.degradationRate = degradationRate;
        this.diffusionRate = diffusionRate;
        this.source = source;
        this.player = Optional.ofNullable(player);
    }

    public static PheromoneType resource(Tile source) {
        return new PheromoneType("resource", 0.001, 1.0, source, null);
    }

    public static PheromoneType playerPheromone(Tile source, Player player) {
        return new PheromoneType(player.name, 0.05, 0.5, source, player);
    }

    @Override
    public String toString() {
        return type;
    }
}