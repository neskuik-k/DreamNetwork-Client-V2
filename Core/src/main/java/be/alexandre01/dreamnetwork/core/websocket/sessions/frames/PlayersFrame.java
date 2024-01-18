package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;

import java.util.function.Consumer;

public class PlayersFrame extends FrameAbstraction {

    Consumer<Player> playerJoin = new Consumer<Player>() {
        @Override
        public void accept(Player player) {
            System.out.println("Player join : " + player.getName());
            getSession().send(
                    new WebMessage().put("player", player.getName())
                            .put("id", player.getId())
                            .put("event", "join")
                            .put("time", player.getTimePlayed())
                            .put("server", player.getServer().getService().getFullName())
            );
        }
    };

    Consumer<Player> playerQuit = new Consumer<Player>() {
        @Override
        public void accept(Player player) {
            System.out.println("Player quit : " + player.getName());
            getSession().send(
                    new WebMessage().put("id", player.getId())
                    .put("event", "quit")
            );
        }
    };



    public PlayersFrame(WebSession session) {
        super(session, "players");
    }

    @Override
    public void handle(WebMessage webMessage) {

    }

    @Override
    public void onEnter() {
        System.out.println("Enter PlayersFrame");
        for (Player player : Core.getInstance().getServicePlayersManager().getPlayersMap().values()) {
            getSession().send(
                    new WebMessage().put("player", player.getName())
                            .put("id", player.getId())
                            .put("event", "join")
                            .put("time", player.getTimePlayed())
                            .put("server", player.getServer().getService().getFullName())
            );
        }
        Core.getInstance().getServicePlayersManager().addPlayerUpdateListener(playerJoin);
        Core.getInstance().getServicePlayersManager().addPlayerQuitListener(playerQuit);
    }

    @Override
    public void onLeave() {
        System.out.println("Leave PlayersFrame");
        Core.getInstance().getServicePlayersManager().removePlayerUpdateListener(playerJoin);
        Core.getInstance().getServicePlayersManager().removePlayerQuitListener(playerQuit);
    }
}
