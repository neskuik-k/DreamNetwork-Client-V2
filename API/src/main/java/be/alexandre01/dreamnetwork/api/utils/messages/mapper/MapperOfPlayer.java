package be.alexandre01.dreamnetwork.api.utils.messages.mapper;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.utils.messages.ObjectConverterMapper;

import java.util.Date;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:02
*/
public class MapperOfPlayer extends ObjectConverterMapper<Player, Integer> {

    @Override
    public Integer convert(Player player) {
        return player.getId();
    }

    @Override
    public Player read(Integer id) {
        return DNCoreAPI.getInstance().getServicePlayersManager().getPlayer(id);
    }
}
