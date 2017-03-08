package no.porqpine.settlersgame.state;

//TODO: make this drain neighbouring tiles instead?
public class SiphonTile extends OwnedTile {
    public int storedPheromone;

    public SiphonTile(int x, int y, Player owner) {
        super(x, y, owner);
    }

    @Override
    public String getType() {
        return "SIPHON";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return true;
    }

    @Override
    public void acceptQueuedPheromone() {
        super.acceptQueuedPheromone();
        storedPheromone += pAmounts.getOrDefault(PheromoneType.RESOURCE, 0L);
        pAmounts.put(PheromoneType.RESOURCE, 0L);
    }
}
