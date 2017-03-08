package no.porqpine.settlersgame.api.messages;

public class CreateGame extends Message {

    public CreateGame() {}

    @Override
    public MessageType getType() {
        return MessageType.CREATE_GAME;
    }
}
