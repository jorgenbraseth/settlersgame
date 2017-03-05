package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@SuppressWarnings("WeakerAccess")
public abstract class Tile extends GameObject {

    public static final double PHEROMONE_DEGRADATION = 0.01;
    public static final double DIFFUSION_RATE = 1;
    public final int x;
    public final int y;

    private List<Tile> neighbours = new ArrayList<>();

    public int pheromoneAmount = 0;
    private double queuedPheromone;

    public Tile(int id, int x, int y) {
        super(id);
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
        adjustPheromone((int) (-1 * pheromoneAmount * PHEROMONE_DEGRADATION));
    }

    private void diffuse() {
        if (acceptsPheromone()) {
            long acceptingNeighbours = neighbours.stream().filter(Tile::acceptsPheromone).count();
            int totalPheromone = pheromoneAmount + neighbours.stream()
                    .filter(Tile::spreadsPheromone)
                    .map(tile -> tile.pheromoneAmount)
                    .reduce((integer, integer2) -> integer + integer2)
                    .orElse(0);

            long avgAmount = totalPheromone / (acceptingNeighbours+1);
            adjustPheromone((long) ((avgAmount - pheromoneAmount) * DIFFUSION_RATE));
        }
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
}
