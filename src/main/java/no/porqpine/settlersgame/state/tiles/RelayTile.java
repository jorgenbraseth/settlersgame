package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.PheromoneType;
import no.porqpine.settlersgame.state.Player;

public class RelayTile extends OwnedTile {

    private static final int PRODUCE_EVERY_N_TICK = 1;
    public static final int PRODUCTION = 600;
    public static final int COST = 1000;
    private final PheromoneType pheromone;
    private int timeSinceLastProduction;

    public RelayTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game);
        pheromone = PheromoneType.playerPheromone(this, owner);
    }

    @Override
    public String getType() {
        return "OWNERSHIP_SPREADER";
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

            adjustPheromone(pheromone, PRODUCTION);
        }
    }

    @Override
    public long cost() {
        return COST;
    }
}
