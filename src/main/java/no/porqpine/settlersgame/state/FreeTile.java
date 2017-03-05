package no.porqpine.settlersgame.state;

public class FreeTile extends Tile {
    public FreeTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return "FREE";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return true;
    }

}
