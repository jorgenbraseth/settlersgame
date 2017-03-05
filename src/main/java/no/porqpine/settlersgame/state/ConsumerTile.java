package no.porqpine.settlersgame.state;

public class ConsumerTile extends OwnedTile {
    public int storedPheromone;

    public ConsumerTile(int x, int y, Player owner) {
        super(x, y, owner);
    }

    @Override
    public String getType() {
        return "CONSUMER";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return true;
    }

    @Override
    public void acceptQueuedPheromone() {
        super.acceptQueuedPheromone();
        storedPheromone += pAmounts.get(PheromoneType.RESOURCE);
        pAmounts.put(PheromoneType.RESOURCE, 0L);
    }
}
