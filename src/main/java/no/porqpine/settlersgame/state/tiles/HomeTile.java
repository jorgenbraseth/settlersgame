package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Pheromone;
import no.porqpine.settlersgame.state.PheromoneType;
import no.porqpine.settlersgame.state.Player;

public class HomeTile extends OwnedTile {

    private static final int PHEROMONE_EVERY_N_TICK = 1;
    private static final int RESOURCE_EVERY_N_TICK = 50;
    public static final long PRODUCTION = 700L;
    public static final int COST = 0;
    public static final long RESOURCE_PRODUCED = 500L;
    private final PheromoneType pheromone;
    private int timeSinceLastPheromone;
    private int timeSinceLastResource;

    public HomeTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game, 1000);
        pheromone = PheromoneType.playerPheromone(this, owner);
        setPheromone(pheromone.type, PRODUCTION);
        distanceToHome = 0;
    }

    @Override
    public String getType() {
        return "HOME";
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        timeSinceLastPheromone += ticks;
        if (timeSinceLastPheromone >= PHEROMONE_EVERY_N_TICK) {
            timeSinceLastPheromone = 0;
            Pheromone pheromone = new Pheromone(this, this.pheromone.type, PRODUCTION, this.pheromone.degradationRate, this.pheromone.diffusionRate, owner);
            queuePheromone(this, pheromone);
        }

        timeSinceLastResource += ticks;
        if (timeSinceLastResource >= RESOURCE_EVERY_N_TICK) {
            timeSinceLastResource = 0;
            owner.addResource("resource", RESOURCE_PRODUCED);
        }

    }

    @Override
    public void calculateNewPheromoneAmounts() {
        super.calculateNewPheromoneAmounts();
        distanceToHome = 0;
    }

    @Override
    public long cost() {
        return COST;
    }
}
