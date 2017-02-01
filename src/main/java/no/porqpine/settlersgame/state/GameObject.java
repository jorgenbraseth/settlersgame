package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.ShapeClicked;

public abstract class GameObject {

    public final int id;

    public GameObject(int id) {
        this.id = id;
    }

    public abstract void click(ShapeClicked event);
}
