package no.porqpine.settlersgame.state.tiles;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Pheromone;
import no.porqpine.settlersgame.state.Player;

public class BlockerTile extends OwnedTile {

    public static final int COST = 500;
    public static final String TYPE_BLOCKER = "BLOCKER";

    public BlockerTile(int x, int y, Player owner, Game game) {
        super(x, y, owner, game, 3000);
    }

    @Override
    public String getType() {
        return TYPE_BLOCKER;
    }

    @Override
    public void diffuse() {
        //Doesn't spread pheromone.
    }

    @Override
    public void calculateNewPheromoneAmounts() {
        pAmounts.clear();
        super.calculateNewPheromoneAmounts();
    }

    @Override
    public double queuePheromone(Tile source, Pheromone pheromone) {
        if (pheromone.amount > 0) {
            source.queuePheromone(source, pheromone);
        }
        super.queuePheromone(source, pheromone);
        return 0;
    }

    @Override
    public long cost() {
        return COST;
    }
}
