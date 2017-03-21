package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Pheromone;
import no.porqpine.settlersgame.state.PheromoneType;
import no.porqpine.settlersgame.state.Player;

public class RelayTile extends OwnedTile {

    private static final int PRODUCE_EVERY_N_TICK = 1;
    public static final int PRODUCTION = 400;
    public static final int COST = 1000;
    private final PheromoneType pheromone;
    private int timeSinceLastProduction;

    public RelayTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game, 500);
        pheromone = PheromoneType.playerPheromone(this, owner);
    }

    @Override
    public String getType() {
        return "OWNERSHIP_SPREADER";
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        timeSinceLastProduction += ticks;
        if (timeSinceLastProduction >= PRODUCE_EVERY_N_TICK) {
            timeSinceLastProduction = 0;
            Pheromone pheromone = new Pheromone(this.pheromone.type, PRODUCTION, this.pheromone.degradationRate, this.pheromone.diffusionRate, owner);
            queuePheromone(this, pheromone);
        }
    }

    @Override
    public long cost() {
        return COST;
    }
}
