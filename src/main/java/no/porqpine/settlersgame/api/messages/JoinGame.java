package no.porqpine.settlersgame.api.messages;

public class JoinGame  extends Message {

    public String name;
    public String color;

    public JoinGame() {}

    @Override
    public MessageType getType() {
        return MessageType.JOIN_GAME;
    }
}
