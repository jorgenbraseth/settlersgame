package no.porqpine.settlersgame.state;

public class BlockerTile extends Tile {
    public BlockerTile(int id, int x, int y) {
        super(id, x, y);
    }

    @Override
    public String getType() {
        return "BLOCKER";
    }

    @Override
    public boolean acceptsPheromone() {
        return false;
    }

    @Override
    public boolean spreadsPheromone() {
        return false;
    }
}
