package no.porqpine.settlersgame.api.messages;

import no.porqpine.settlersgame.Game;
import no.porqpine.settlersgame.state.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static no.porqpine.settlersgame.api.messages.MessageType.GAME_LIST;


public class GameList extends Message {

    public List<SimpleGameInfo> games;

    public GameList(Collection<Game> games) {
        this.games = games.stream().map(SimpleGameInfo::new).collect(Collectors.toList());
    }

    @Override
    public MessageType getType() {
        return GAME_LIST;
    }

    public static class SimpleGameInfo {

        public final List<Player> players;
        public final String gameId;

        public SimpleGameInfo(Game game) {
            players = game.state.players;
            gameId = game.gameId;
        }
    }
}
