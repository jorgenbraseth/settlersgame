package no.porqpine.settlersgame.state;

public class ConsumerTile extends Tile {
    public int storedPheromone;

    public ConsumerTile(int id, int x, int y) {
        super(id, x, y);
    }

    @Override
    public String getType() {
        return "CONSUMER";
    }

    @Override
    public boolean acceptsPheromone() {
        return true;
    }

    @Override
    public void acceptQueuedPheromone() {
        super.acceptQueuedPheromone();
        storedPheromone += pheromoneAmount;
        pheromoneAmount = 0;
    }

    @Override
    public boolean spreadsPheromone() {
        return false;
    }
}
