package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private static int NEXT_PLAYER_ID = 0;

    @JsonBackReference
    public Session session;

    public final String color;
    public final String name;
    public final int id;
    public Map<String, Long> resources = new HashMap<>();
    public PheromoneType pheromone;

    public Player(String name, String color, Session connection) {
        this.session = connection;
        this.color = color;
        this.name = name;
        this.id = NEXT_PLAYER_ID++;
    }

    public void addResource(String resource, long amount) {
        resources.putIfAbsent(resource, 0L);
        resources.computeIfPresent(resource, (s, currentValue) -> currentValue + amount);
    }

    public void payCost(Map<String, Long> cost) {
        for (String resource : cost.keySet()) {
            resources.computeIfPresent(resource, (s, currentValue) -> currentValue - cost.get(resource));
        }
    }

    public void setPheromone(PheromoneType pheromone) {
        this.pheromone = pheromone;
    }
}
