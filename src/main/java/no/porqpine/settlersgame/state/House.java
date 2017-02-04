package no.porqpine.settlersgame.state;

import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class House extends Structure {

    public House(int id, Player owner) {
        super(id, owner, StructureType.HOUSE);

        Map<String, Long> costMap = new HashMap<>();
        costMap.put(Tile.TileType.FOREST.name(), 5L);
        costMap.put(Tile.TileType.PASTURE.name(), 5L);

        cost = Collections.unmodifiableMap(costMap);
    }

    @Override
    public void click(ShapeClicked event) {

    }

}
