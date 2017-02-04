package no.porqpine.settlersgame.state;

import java.util.Map;

public abstract class Structure extends GameObject {
    public final Player owner;
    public StructureType type;
    public Map<String, Long> cost;

    public Structure(int id, Player owner, StructureType type) {
        super(id);
        this.owner = owner;
        this.type = type;
    }

    public enum StructureType {
        HOUSE
    }

}
