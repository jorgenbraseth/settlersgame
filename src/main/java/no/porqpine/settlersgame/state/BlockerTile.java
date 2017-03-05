package no.porqpine.settlersgame.state;

public class BlockerTile extends Tile {
    public BlockerTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return "BLOCKER";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return false;
    }
}
