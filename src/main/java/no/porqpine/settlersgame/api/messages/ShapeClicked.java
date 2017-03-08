package no.porqpine.settlersgame.api.messages;

public class ShapeClicked extends Message {

    public int id;
    public int[] coords;
    public String playerName;

    @Override
    public MessageType getType() {
        return MessageType.SHAPE_CLICKED;
    }
}
