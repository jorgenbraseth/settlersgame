package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.PheromoneType;
import no.porqpine.settlersgame.state.Player;

public class HomeTile extends OwnedTile {

    private static final int PRODUCE_EVERY_N_TICK = 1;
    public static final long PRODUCTION = 600L;
    public static final int COST = 0;
    private int timeSinceLastProduction;

    public HomeTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game);
        pAmounts.put(owner.pheromone, PRODUCTION);
    }

    @Override
    public String getType() {
        return "HOME";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return true;
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        timeSinceLastProduction += ticks;
        if (timeSinceLastProduction >= PRODUCE_EVERY_N_TICK) {
            timeSinceLastProduction = 0;
            adjustPheromone(owner.pheromone, PRODUCTION);
        }

        owner.addResource("resource", 1L);
    }

    @Override
    public long cost() {
        return COST;
    }
}
