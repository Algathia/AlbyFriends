package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Vialonyx
 */

public class FriendManager {

    public void sendFriendRequest(UUID fromUUID, String targetName){

        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(fromUUID);

        boolean online = AlbyFriends.get().getProxy().getPlayers().stream()
                .filter(player -> player.getName().equals(targetName))
                .findAny().isPresent();

        if(!online){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        new FriendRequestPacket().send(fromUUID, AlbyFriends.get().getProxy().getPlayer(targetName).getUniqueId().toString());

    }

}
