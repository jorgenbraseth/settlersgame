package no.porqpine.settlersgame.state;

public abstract class GameObject {
    private static int nextObjectId = 0;
    public final int id;

    public GameObject() {
        this.id = nextObjectId++;
    }

}
