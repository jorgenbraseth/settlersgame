package no.porqpine.settlersgame.state;

public class OwnerShipSpreaderTile extends OwnedTile {

    public OwnerShipSpreaderTile(int x, int y, Player owner) {
        super(x, y, owner);
    }

    @Override
    public String getType() {
        return "OWNERSHIP_SPREADER";
    }

    @Override
    public boolean acceptsPheromone(PheromoneType pheromoneType) {
        return false;
    }
}
