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

    public Player(String name, String color, Session connection) {
        this.session = connection;
        this.color = color;
        this.name = name;
        this.id = NEXT_PLAYER_ID++;

        addResource(Tile.TileType.FOREST.name(),5);
        addResource(Tile.TileType.PASTURE.name(),5);
        addResource(Tile.TileType.MOUNTAIN.name(),2);
    }

    public void addResource(String resource, long amount) {
        resources.putIfAbsent(resource, 0L);
        resources.computeIfPresent(resource, (s, currentValue) -> currentValue + amount);
    }

    public boolean canAfford(Structure structure) {
        Map<String, Long> cost = structure.cost;
        for (String resource : cost.keySet()) {
            if(resources.get(resource) < cost.get(resource)){
                return false;
            }
        }
        return true;
    }

    public void payCost(Map<String, Long> cost) {
        for (String resource : cost.keySet()) {
            resources.computeIfPresent(resource, (s, currentValue) -> currentValue - cost.get(resource));
        }
    }
}
