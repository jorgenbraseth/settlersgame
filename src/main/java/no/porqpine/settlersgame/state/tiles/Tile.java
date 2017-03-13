package no.porqpine.settlersgame.state.tiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.state.GameObject;
import no.porqpine.settlersgame.state.PheromoneType;
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

    private Map<PheromoneType, Long> pAmounts = new HashMap<>();

    @JsonIgnore
    public Map<PheromoneType, Long> pQueued = new HashMap<>();


    public Tile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public void setPheromone(PheromoneType pheromone, long amount) {
        pAmounts.put(pheromone, amount);
    }


    @JsonIgnore
    public Map<Player, Long> getPlayerPheromones() {
        Map<Player, Long> map = new HashMap<>();
        pAmounts.keySet().stream()
                .filter(type -> type.player.isPresent())
                .forEach(ph -> map.merge(ph.player.get(), pAmounts.get(ph), (aLong, aLong2) -> aLong + aLong2));
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Long> getResourcePheromones() {
        Map<String, Long> map = new HashMap<>();
        pAmounts.keySet().stream()
                .filter(type -> !type.player.isPresent())
                .forEach(ph -> map.merge(ph.type, pAmounts.get(ph), (aLong, aLong2) -> aLong + aLong2));
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public Map<PheromoneType, Long> getPAmounts() {
        return pAmounts.entrySet().stream().filter(entry -> entry.getValue() > 0L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addNeighbour(Tile t) {
        neighbours.add(t);
    }

    public void calculateNewPheromoneAmounts() {
        Map<PheromoneType, Long> newAmounts = new HashMap<>();

        pQueued.forEach((pheromoneType, queuedAmount) -> {
            Long currentAmount = pAmounts.getOrDefault(pheromoneType, 0L);
            long newAmount = Math.max(0, currentAmount + queuedAmount);
            if(newAmount > 0) {
                newAmounts.put(pheromoneType, newAmount);
            }
        });
        pAmounts = newAmounts;

        pQueued.clear();
    }

    public void degrade() {
        pAmounts.keySet().forEach(this::degrade);
    }

    private void degrade(PheromoneType pheromoneType) {
        adjustPheromone(pheromoneType, (int) (-1 * Math.ceil(pAmounts.getOrDefault(pheromoneType, 0L) * pheromoneType.degradationRate)));
    }

    public void diffuse() {
        pAmounts.keySet().forEach(this::diffuse);
    }

    private void diffuse(PheromoneType pheromoneType) {
        Long pheromoneAmount = pAmounts.getOrDefault(pheromoneType, 0L);
        long pheromoneToSpread = (long) (pheromoneAmount * pheromoneType.diffusionRate);
        long acceptingNeighbours = neighbours.stream()
                .filter(tile -> tile.acceptsPheromone(pheromoneType))
                .count();
        neighbours.stream()
                .filter((tile) -> tile.acceptsPheromone(pheromoneType))
                .forEach(neighbour ->
                        neighbour.adjustPheromone(pheromoneType, pheromoneToSpread / acceptingNeighbours)
                );

        adjustPheromone(pheromoneType, -pheromoneToSpread);
    }

    void adjustPheromone(PheromoneType type, long amount) {
        Long currentQueued = pQueued.getOrDefault(type, 0L);
        pQueued.put(type, currentQueued + amount);
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

    public abstract boolean acceptsPheromone(PheromoneType pheromoneType);

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
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
