package no.porqpine.settlersgame.state;

public class PheromoneType {
    public static final PheromoneType RESOURCE = new PheromoneType("resource", 0.05, 0.5);

    public final String type;
    public final double degradationRate;
    public final double diffusionRate;

    PheromoneType(String type, double degradationRate, double diffusionRate) {
        this.type = type;
        this.degradationRate = degradationRate;
        this.diffusionRate = diffusionRate;
    }

    public PheromoneType playerPheromone(String playerName) {
        return new PheromoneType(playerName, 0.05, 0.5);
    }

    @Override
    public String toString() {
        return type;
    }
}