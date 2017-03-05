package no.porqpine.settlersgame.state;

public class FreeTile extends Tile {
    public FreeTile(int id, int x, int y) {
        super(id, x, y);
    }

    @Override
    public String getType() {
        return "FREE";
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
