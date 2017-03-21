package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.state.Pheromone;
import no.porqpine.settlersgame.state.PheromoneType;

import java.util.Random;

public class ProducerTile extends Tile {

    private static final int TIME_ON = 30;
    private static final int TIME_OFF = 170;
    public static final int PRODUCTION = 1500;

    private boolean isOn = false;
    private int timeInCurrentState = 0;

    private final PheromoneType pheromone;

    public ProducerTile(int x, int y) {
        super(x, y);
        timeInCurrentState = new Random().nextInt(TIME_OFF);
        pheromone = PheromoneType.resource(this);
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

        Pheromone pheromone = new Pheromone(this, this.pheromone.type, PRODUCTION, this.pheromone.degradationRate, this.pheromone.diffusionRate, null);
        if (isOn) {
            queuePheromone(this, pheromone);
        }

    }

    @Override
    public String getType() {
        return "PRODUCER";
    }

}
