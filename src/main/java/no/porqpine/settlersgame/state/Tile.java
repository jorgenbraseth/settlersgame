package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.ArrayList;
import java.util.List;

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

    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public void addNeighbour(Tile t) {
        neighbours.add(t);
    }

    public void tick(int ticks) {
        diffuse();
        degrade();
    }

    public void acceptQueuedPheromone() {
        pheromoneAmount += queuedPheromone;
        queuedPheromone = 0;

        pheromoneAmount = Math.max(0, pheromoneAmount);
    }

    private void degrade() {
        adjustPheromone((int) (-1 * Math.ceil(pheromoneAmount * PHEROMONE_DEGRADATION)));
    }

    private void diffuse() {
        long pheromoneToSpread = (long) (pheromoneAmount * DIFFUSION_RATE);
        long acceptingNeighbours = neighbours.stream().filter(Tile::acceptsPheromone).count();
        neighbours.stream()
                .filter(Tile::acceptsPheromone)
                .forEach(neighbour ->
                        neighbour.adjustPheromone(pheromoneToSpread / acceptingNeighbours)
                );

        adjustPheromone(-pheromoneToSpread);
    }

    void adjustPheromone(long amount) {
        this.queuedPheromone += amount;

    }

    @Override
    public void click(ShapeClicked event) {

    }

    public abstract String getType();

    public abstract boolean acceptsPheromone();

    public abstract boolean spreadsPheromone();

    public void replaceNeighbour(Tile oldTile, Tile newTile) {
        removeNeighbour(oldTile);
        addNeighbour(newTile);
    }

    private void removeNeighbour(Tile oldTile) {
        neighbours.remove(oldTile);
    }
}
