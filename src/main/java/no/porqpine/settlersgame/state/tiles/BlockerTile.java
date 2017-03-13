package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.PheromoneType;
import no.porqpine.settlersgame.state.Player;

import java.util.Optional;

public class BlockerTile extends OwnedTile {

    public static final int COST = 500;

    public BlockerTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game);
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
        Optional<PheromoneType> highestPlayerPheromone = getPAmounts().keySet().stream().max((p1, p2) -> getPAmounts().get(p1).compareTo(getPAmounts().get(p2)));
        if(highestPlayerPheromone.isPresent()){
            owner = highestPlayerPheromone.get().player.get();
        }
        getPAmounts().keySet().forEach(pheromoneType -> adjustPheromone(pheromoneType, -getPAmounts().get(pheromoneType)));
    }

    @Override
    public long cost() {
        return COST;
    }

    @Override
    public void tick(int i) {
    }
}
