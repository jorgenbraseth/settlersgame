package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.ShapeClicked;

public abstract class GameObject {
    private static int nextObjectId = 0;
    public final int id;

    public GameObject() {
        this.id = nextObjectId++;
    }

}
