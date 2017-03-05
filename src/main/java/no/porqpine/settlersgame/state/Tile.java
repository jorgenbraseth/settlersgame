package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public abstract class Tile extends GameObject {

    public final int x;
    public final int y;

    @JsonIgnore
    public List<Tile> neighbours = new ArrayList<>();

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

    public void acceptQueuedPheromone() {
        pQueued.forEach((pheromoneType, queuedAmount) -> {
            Long currentAmount = pAmounts.getOrDefault(pheromoneType, 0L);
            pAmounts.put(pheromoneType, Math.max(0, currentAmount + queuedAmount));
        });

        pQueued.clear();
    }

    public void degrade() {
        pAmounts.keySet().forEach(this::degrade);
    }

    private void degrade(PheromoneType pheromoneType) {
        adjustPheromone(pheromoneType, (int) (-1 * Math.ceil(pAmounts.getOrDefault(pheromoneType, 0L) * pheromoneType.degradationRate)));
    }

    public void diffuse() {
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

    public Player getHighestPheromonePlayer() {
        List<PheromoneType> playerPheromonesPresent = pAmounts.keySet().stream()
                .filter(pheromoneType -> pheromoneType != PheromoneType.RESOURCE)
                .filter(pheromoneType -> pAmounts.get(pheromoneType) > 0)
                .collect(Collectors.toList());

        Optional<PheromoneType> playerWithMostPheromone = playerPheromonesPresent.stream()
                .max((o1, o2) -> pAmounts.get(o1).compareTo(pAmounts.get(o2)));

        return playerWithMostPheromone.map(pheromoneType -> pheromoneType.player.get()).orElse(null);

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

    /* Overridable by sub-classes. Called every render loop */
    public void tick(int i){};
}
