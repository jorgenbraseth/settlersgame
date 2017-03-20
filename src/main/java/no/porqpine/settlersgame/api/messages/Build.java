package no.porqpine.settlersgame.api.messages;

public class Build extends GameMessage {

    public String tileToBuild;
    public int buildOnTileId;
    public String playerName;

    @Override
    public MessageType getType() {
        return MessageType.BUILD;
    }
}
