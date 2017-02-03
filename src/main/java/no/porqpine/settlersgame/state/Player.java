package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.eclipse.jetty.websocket.api.Session;

public class Player {
    private static int NEXT_PLAYER_ID = 0;

    @JsonBackReference
    public Session session;

    public final String color;
    public final String name;
    public final int id;

    public Player(String name, String color, Session connection) {
        this.session = connection;
        this.color = color;
        this.name = name;
        this.id = NEXT_PLAYER_ID++;
    }
}
