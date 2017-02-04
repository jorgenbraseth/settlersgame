package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.GameLogic;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Crossing extends GameObject {

    @JsonBackReference
    public final Optional<Tile> NW;
    @JsonBackReference
    public final Optional<Tile> NE;
    @JsonBackReference
    public final Optional<Tile> SW;
    @JsonBackReference
    public final Optional<Tile> SE;
    public String owner;

    public Structure structure;

    public Crossing(int id, Tile se, Tile sw, Tile ne, Tile nw) {
        super(id);
        NW = Optional.ofNullable(nw);
        NE = Optional.ofNullable(ne);
        SW = Optional.ofNullable(sw);
        SE = Optional.ofNullable(se);

        NW.ifPresent(tile -> tile.setSE(this));
        NE.ifPresent(tile -> tile.setSW(this));
        SW.ifPresent(tile -> tile.setNE(this));
        SE.ifPresent(tile -> tile.setNW(this));
    }

    public Integer getNW() {
        return NW.map(tile -> tile.id).orElse(null);
    }

    public Integer getNE() {
        return NE.map(tile -> tile.id).orElse(null);
    }

    public Integer getSW() {
        return SW.map(tile -> tile.id).orElse(null);
    }

    public Integer getSE() {
        return SE.map(tile -> tile.id).orElse(null);
    }

    public Integer getX() {
        return SE.get().x;
    }

    public Integer getY() {
        return SE.get().y;
    }

    @Override
    public void click(ShapeClicked event) {
        Player player = GameLogic.GAME.findPlayer(event.playerName);
        build(new House(0, player));
    }

    public void build(Structure structure) {
        if(owner == null && structure.owner.canAfford(structure)){
            structure.owner.payCost(structure.cost);
            this.structure = structure;
        }
    }
}
