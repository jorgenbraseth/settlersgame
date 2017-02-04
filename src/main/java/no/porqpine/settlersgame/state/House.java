package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.ShapeClicked;

public class House extends Structure {

    public House(int id, Player owner) {
        super(id, owner, StructureType.HOUSE);
    }

    @Override
    public void click(ShapeClicked event) {

    }

}
