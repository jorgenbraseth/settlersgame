package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.state.PheromoneType;

import java.util.Random;

public class ProducerTile extends Tile {

    private static final int TIME_ON = 15;
    private static final int TIME_OFF = 40;
    public static final int PRODUCTION = 100;

    private boolean isOn = false;
    private int timeInCurrentState = 0;

    public ProducerTile(int x, int y) {
        super(x, y);
        timeInCurrentState = new Random().nextInt(TIME_OFF);
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