package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

    public TextComponent getFormattedAcceptMessage(String requestID){
        TextComponent message = new TextComponent("[ACCEPTER]");
        message.setColor(ChatColor.GREEN);
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requestID));
        return message;
    }

    public TextComponent getFormattedRefuseMessage(String requestID){
        TextComponent message = new TextComponent("[REFUSER]");
        message.setColor(ChatColor.RED);
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend refuse " + requestID));
        return message;
    }

}
