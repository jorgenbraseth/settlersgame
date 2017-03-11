package no.porqpine.settlersgame.api.messages;

public class ShapeRightClicked extends GameMessage {

    public int id;

    public int[] coords;
    public String playerName;

    @Override
    public MessageType getType() {
        return MessageType.SHAPE_RIGHT_CLICKED;
    }
}
