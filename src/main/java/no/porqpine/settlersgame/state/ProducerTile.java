package no.porqpine.settlersgame.state;

public class ProducerTile extends Tile {

    private static final int PRODUCE_EVERY_N_TICK = 30;
    public static final int PRODUCTION = 800;
    private int timeSinceLastProduction;

    public ProducerTile(int x, int y) {
        super(x, y);
        timeSinceLastProduction = (int) (Math.random() * PRODUCE_EVERY_N_TICK);
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        timeSinceLastProduction += ticks;
        if(timeSinceLastProduction >= PRODUCE_EVERY_N_TICK){
            timeSinceLastProduction = 0;
            adjustPheromone(PheromoneType.RESOURCE, PRODUCTION);
        }

    }

    @Override
    public String getType() {
        return "PRODUCER";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return true;
    }

}
