package no.porqpine.settlersgame.state;

public abstract class Structure extends GameObject{
    public final Player owner;
    public StructureType type;

    public Structure(int id, Player owner, StructureType type) {
        super(id);
        this.owner = owner;
        this.type = type;
    }

    public enum StructureType {
        HOUSE
    }

}
