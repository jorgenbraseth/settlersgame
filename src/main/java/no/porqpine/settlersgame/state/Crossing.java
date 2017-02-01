package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Crossing {

    @JsonBackReference
    public final Optional<Tile> NW;
    @JsonBackReference
    public final Optional<Tile> NE;
    @JsonBackReference
    public final Optional<Tile> SW;
    @JsonBackReference
    public final Optional<Tile> SE;
    public final int id;

    public Crossing(int id, Tile se, Tile sw, Tile ne, Tile nw) {
        NW = Optional.ofNullable(nw);
        NE = Optional.ofNullable(ne);
        SW = Optional.ofNullable(sw);
        SE = Optional.ofNullable(se);

        NW.ifPresent(tile -> tile.setSE(this));
        NE.ifPresent(tile -> tile.setSW(this));
        SW.ifPresent(tile -> tile.setNE(this));
        SE.ifPresent(tile -> tile.setNW(this));

        this.id = id;
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
}
