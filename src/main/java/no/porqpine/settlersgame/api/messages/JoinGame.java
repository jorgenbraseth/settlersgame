package no.porqpine.settlersgame.api.messages;

public class JoinGame  extends GameMessage {

    public String name;
    public String color;

    public JoinGame() {}

    @Override
    public MessageType getType() {
        return MessageType.JOIN_GAME;
    }
}
