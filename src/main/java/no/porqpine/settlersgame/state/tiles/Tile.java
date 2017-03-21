package no.porqpine.settlersgame.state.tiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.state.GameObject;
import no.porqpine.settlersgame.state.Pheromone;
import no.porqpine.settlersgame.state.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Tile extends GameObject {

    public final int x;
    public final int y;

    @JsonIgnore
    public List<Tile> neighbours = new ArrayList<>();

    @JsonIgnore
    public List<Pheromone> pAmounts = new ArrayList<>();

    @JsonIgnore
    public List<Pheromone> pQueued = new ArrayList<>();


    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public void setPheromone(String typeToSet, long amount) {
        pAmounts = pAmounts.stream().map(pheromone -> {
            if (typeToSet.equals(pheromone.type)) {
                return pheromone.copyWithAmount(amount);
            } else {
                return pheromone;
            }
        }).collect(Collectors.toList());
    }

    @JsonIgnore
    public Map<Player, Long> getPlayerPheromones() {
        Map<Player, Long> map = new HashMap<>();
        pAmounts.stream()
                .filter(pheromone -> pheromone.owner != null)
                .forEach(ph -> map.merge(ph.owner, (long)ph.amount, (aLong, aLong2) -> aLong + aLong2));
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Long> getResourcePheromones() {
        Map<String, Long> map = new HashMap<>();
        pAmounts.stream()
                .filter(p -> p.owner == null)
                .forEach(ph -> map.put(ph.type, (long)ph.amount));
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public List<Pheromone> getResourcePheromonesList() {

        return pAmounts.stream()
                .filter(p -> p.owner == null)
                .filter(p -> p.amount > 0L)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Pheromone> getPAmounts() {
        return pAmounts.stream().filter(pheromone -> pheromone.amount > 0L).collect(Collectors.toList());
    }

    public void addNeighbour(Tile t) {
        neighbours.add(t);
    }

    public void calculateNewPheromoneAmounts() {
        Map<Tile, Pheromone> newAmounts = new HashMap<>();
        pQueued.stream().forEach(pheromone -> newAmounts.merge(pheromone.source, pheromone, Pheromone::add));

        pAmounts = newAmounts.values().stream().filter(pheromone -> pheromone.amount > 0).collect(Collectors.toList());

        Player highestPheromonePlayer = getHighestPheromonePlayer();
        Long leadOfHighestPheromone = getPheromoneLeadOfHighestPheromone();
        pQueued.clear();
    }

    public void degrade() {
        pAmounts.forEach((pheromone) -> queuePheromone(this, pheromone.degraded()));
    }

    public void diffuse() {
        pAmounts.forEach(pheromone -> pheromone.diffuse(this, neighbours));
    }

    public double queuePheromone(Tile source, Pheromone pheromone) {
        if(pheromone.amount > 0){
            pQueued.add(pheromone);
        }
        return pheromone.amount;
    }

    public Player getHighestPheromonePlayer() {
        Map<Player, Long> playerPheromones = getPlayerPheromones();
        if (playerPheromones.isEmpty()) {
            return null;
        }
        return getPlayerPheromones().entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .get().getKey();
    }

    public Long getPheromoneLeadOfHighestPheromone() {
        if (getHighestPheromonePlayer() == null) {
            return null;
        }
        if (getPlayerPheromones().size() == 1) {
            return getPlayerPheromones().get(getHighestPheromonePlayer());
        } else {

            Long secondPlace = getPlayerPheromones().entrySet().stream()
                    .filter(entry -> entry.getKey() != getHighestPheromonePlayer())
                    .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .get().getValue();

            long lead = getPlayerPheromones().getOrDefault(getHighestPheromonePlayer(), 0L) - secondPlace;
            return lead;

        }
    }


    public abstract String getType();

    public void replaceNeighbour(Tile oldTile, Tile newTile) {
        removeNeighbour(oldTile);
        addNeighbour(newTile);
    }

    private void removeNeighbour(Tile oldTile) {
        neighbours.remove(oldTile);
    }

    /* Overridable by sub-classes. Called every render loop */
    public void tick(int i) {
    }

    @Override
    public String toString() {
        return "Tile{" +
                "type=" + getType() +
                ", x=" + x +
                ", y=" + y +
                ", id=" + id +
                '}';
    }
}
