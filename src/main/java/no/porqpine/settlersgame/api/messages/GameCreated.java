package no.porqpine.settlersgame.api.messages;

public class GameCreated extends Message {

    public String gameId;

    public GameCreated(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public MessageType getType() {
        return MessageType.GAME_CREATED;
    }
}
