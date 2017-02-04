package no.porqpine.settlersgame.state;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.porqpine.settlersgame.api.ShapeClicked;

import static no.porqpine.settlersgame.state.Edge.Orientation.HORIZONTAL;
import static no.porqpine.settlersgame.state.Edge.Orientation.VERTICAL;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Edge extends GameObject{

    @JsonBackReference
    public final Tile t1;
    @JsonBackReference
    public final Tile t2;
    public final Orientation orientation;
    public String owner;

    public Edge(int id, Tile t1, Tile t2, Orientation orientation) {
        super(id);
        this.t1 = t1;
        this.t2 = t2;
        this.orientation = orientation;
    }

    public Integer getX() {
        if(VERTICAL == orientation){
            return t2.x;
        }else{
            return t1.x;
        }
    }
    public Integer getY() {
        if(VERTICAL == orientation){
            return t1.y;
        }else{
            return t2.y;
        }
    }

    public Integer getW() {
        if(VERTICAL == orientation){
            return t1.id;
        }else{
            return null;
        }
    }
    public Integer getE() {
        if(VERTICAL == orientation){
            return t2.id;
        }else{
            return null;
        }
    }
    public Integer getN() {
        if(HORIZONTAL == orientation){
            return t1.id;
        }else{
            return null;
        }
    }
    public Integer getS() {
        if(HORIZONTAL == orientation){
            return t1.id;
        }else{
            return null;
        }
    }

    @Override
    public void click(ShapeClicked event) {
        this.owner = event.playerName;
    }

    enum Orientation {
        VERTICAL,HORIZONTAL
    }

}
