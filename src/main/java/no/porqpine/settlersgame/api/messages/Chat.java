package no.porqpine.settlersgame.api.messages;

import no.porqpine.settlersgame.state.Player;

public class Chat extends GameMessage {
    public String playerName;
    public String message;
    public Player player;

    @Override
    public MessageType getType() {
        return MessageType.CHAT;
    }
}
