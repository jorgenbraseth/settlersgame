package no.porqpine.settlersgame.state;

public class ProducerTile extends OwnedTile {

    private static final int PRODUCE_EVERY_N_TICK = 30;
    public static final int PRODUCTION = 800;
    private int timeSinceLastProduction;

    public ProducerTile(int x, int y, Player owner) {
        super(x, y, owner);
        timeSinceLastProduction = (int) (Math.random() * PRODUCE_EVERY_N_TICK);
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
