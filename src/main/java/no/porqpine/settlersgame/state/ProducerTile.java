package no.porqpine.settlersgame.state;

public class ProducerTile extends Tile {

    private static final int TIME_ON = 10;
    private static final int TIME_OFF = 40;
    public static final int PRODUCTION = 200;

    private boolean isOn = false;
    private int timeInCurrentState = 0;

    public ProducerTile(int x, int y) {
        super(x, y);
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);

        if (isOn && timeInCurrentState > TIME_ON) {
            isOn = !isOn;
            timeInCurrentState = 0;
        } else if (!isOn && timeInCurrentState > TIME_OFF) {
            isOn = !isOn;
            timeInCurrentState = 0;
        }
        timeInCurrentState++;

        if (isOn) {
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
