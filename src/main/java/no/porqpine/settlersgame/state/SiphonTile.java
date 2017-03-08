package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.GameLogic;

//TODO: make this drain neighbouring tiles instead?
public class SiphonTile extends OwnedTile {
    public int storedPheromone;

    public static final int COST = 300;
    public static final long MAX_SIPHON = 5;

    public SiphonTile(int x, int y, Player owner, GameLogic game) {
        super(x, y, owner, game);
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
        Long pheromoneOnTile = pAmounts.getOrDefault(PheromoneType.RESOURCE, 0L);
        long siphonedAmount = Math.min(pheromoneOnTile, MAX_SIPHON);
        owner.addResource("resource", siphonedAmount);
        pAmounts.put(PheromoneType.RESOURCE, pheromoneOnTile-siphonedAmount);
    }

    @Override
    public long cost() {
        return COST;
    }
}
