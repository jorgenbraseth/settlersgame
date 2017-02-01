package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import java.util.Optional;

import static no.porqpine.settlersgame.state.Edge.Orientation.VERTICAL;

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

    }
}
