package no.porqpine.settlersgame.state;

import java.util.Optional;

public class BlockerTile extends OwnedTile {

    public static final int COST = 3000;

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
        //Nothing spreads from this
    }

    @Override
    public void degrade() {
        Optional<PheromoneType> highestPlayerPheromone = pAmounts.keySet().stream().max((p1, p2) -> pAmounts.get(p1).compareTo(pAmounts.get(p2)));
        if(highestPlayerPheromone.isPresent()){
            owner = highestPlayerPheromone.get().player.get();
        }
        pAmounts.keySet().forEach(pheromoneType -> adjustPheromone(pheromoneType, -pAmounts.get(pheromoneType)));
    }

    @Override
    public long cost() {
        return COST;
    }
}
