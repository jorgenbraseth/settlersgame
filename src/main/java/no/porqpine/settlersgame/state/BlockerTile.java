package no.porqpine.settlersgame.state;

public class BlockerTile extends OwnedTile {
    public BlockerTile(int x, int y, Player owner) {
        super(x, y, owner);
    }

    @Override
    public String getType() {
        return "BLOCKER";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return false;
    }

    @Override
    public void diffuse() {
    }

    @Override
    public void degrade() {
        pAmounts.keySet().forEach(pheromoneType -> adjustPheromone(pheromoneType, -pAmounts.get(pheromoneType)));
    }
}
