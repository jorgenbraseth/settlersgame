package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@SuppressWarnings("WeakerAccess")
public abstract class Tile extends GameObject {

    public static final double PHEROMONE_DEGRADATION = 0.05;
    public static final double DIFFUSION_RATE = .5;
    public final int x;
    public final int y;

    @JsonIgnore
    public List<Tile> neighbours = new ArrayList<>();

    public long pheromoneAmount = 0;
    private double queuedPheromone;

    public Map<PheromoneType, Long> pAmounts = new HashMap<>();
    public Map<PheromoneType, Long> pQueued = new HashMap<>();


    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public void addNeighbour(Tile t) {
        neighbours.add(t);
    }

    public void tick(int ticks) {
        diffuseAll();
        degradeAll();
    }

    public void acceptQueuedPheromone() {
        pQueued.forEach((pheromoneType, queuedAmount) -> {
            Long currentAmount = pAmounts.getOrDefault(pheromoneType, 0L);
            pAmounts.put(pheromoneType, Math.max(0, currentAmount + queuedAmount));
        });

        pQueued.clear();
    }

    private void degradeAll() {
        pAmounts.keySet().forEach(this::degrade);
    }

    private void degrade(PheromoneType pheromoneType) {
        adjustPheromone(pheromoneType, (int) (-1 * Math.ceil(pAmounts.getOrDefault(pheromoneType, 0L) * pheromoneType.degradationRate)));
    }

    private void diffuseAll() {
        pAmounts.keySet().forEach(this::diffuse);
    }

    private void diffuse(PheromoneType pheromoneType) {
        Long pheromoneAmount = pAmounts.getOrDefault(pheromoneType, 0L);
        long pheromoneToSpread = (long) (pheromoneAmount * pheromoneType.diffusionRate);
        long acceptingNeighbours = neighbours.stream()
                .filter(tile -> tile.acceptsPheromone(pheromoneType))
                .count();
        neighbours.stream()
                .filter((tile) -> tile.acceptsPheromone(pheromoneType))
                .forEach(neighbour ->
                        neighbour.adjustPheromone(pheromoneType, pheromoneToSpread / acceptingNeighbours)
                );

        adjustPheromone(pheromoneType, -pheromoneToSpread);
    }

    void adjustPheromone(PheromoneType type, long amount) {
        Long currentQueued = pQueued.getOrDefault(type, 0L);
        pQueued.put(type, currentQueued + amount);
    }

    @Override
    public void click(ShapeClicked event) {

    }

    public abstract String getType();

    public abstract boolean acceptsPheromone(PheromoneType pheromoneType);

    public void replaceNeighbour(Tile oldTile, Tile newTile) {
        removeNeighbour(oldTile);
        addNeighbour(newTile);
    }

    private void removeNeighbour(Tile oldTile) {
        neighbours.remove(oldTile);
    }
}
