package no.porqpine.settlersgame.state;

public class ProducerTile extends Tile {

    private static final int PRODUCE_EVERY_N_TICK = 15;
    public static final int PRODUCTION = 300;
    private int timeSinceLastProduction;

    public ProducerTile(int id, int x, int y) {
        super(id, x, y);
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        timeSinceLastProduction += ticks;
        if(timeSinceLastProduction >= PRODUCE_EVERY_N_TICK){
            timeSinceLastProduction = 0;
            adjustPheromone(PRODUCTION);
        }

    }

    @Override
    public String getType() {
        return "PRODUCER";
    }

    @Override
    public boolean acceptsPheromone() {
        return true;
    }

    @Override
    public boolean spreadsPheromone() {
        return true;
    }
}
