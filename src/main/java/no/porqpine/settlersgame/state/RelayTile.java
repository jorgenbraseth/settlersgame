package no.porqpine.settlersgame.state;

public class RelayTile extends OwnedTile {

    private static final int PRODUCE_EVERY_N_TICK = 1;
    public static final int PRODUCTION = 600;
    private int timeSinceLastProduction;

    public RelayTile(int x, int y, Player owner) {
        super(x, y, owner);
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

            adjustPheromone(owner.pheromone, PRODUCTION);
        }
    }
}
