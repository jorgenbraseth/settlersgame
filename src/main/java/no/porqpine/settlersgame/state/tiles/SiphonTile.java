package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Player;

//TODO: make this drain neighbouring tiles instead?
public class SiphonTile extends OwnedTile {
    public int storedPheromone;

    public static final int COST = 300;
    public static final long MAX_SIPHON = 200;

    public SiphonTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game, 300);
    }

    @Override
    public String getType() {
        return "SIPHON";
    }

    @Override
    public void calculateNewPheromoneAmounts() {
        super.calculateNewPheromoneAmounts();
        getResourcePheromonesList().stream()
                .forEach(pheromone -> {
                    double siphonedAmount = Math.min(pheromone.amount, MAX_SIPHON);
                    owner.addResource("resource", (long)(siphonedAmount/10));
                    queuePheromone(this, pheromone.copyWithAmount(-siphonedAmount));
                });
    }

    @Override
    public long cost() {
        return COST;
    }
}
