package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.*;

/**
 * @author Vialonyx
 */

public class FriendManager {

    private Map<String, UUID[]> requestIds;

    public FriendManager(){
        this.requestIds = new HashMap<>();
    }

    // -- Core methods --

    public void sendFriendRequest(UUID fromUUID, String targetName){

        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(fromUUID);

        boolean online = AlbyFriends.get().getProxy().getPlayers().stream()
                .filter(player -> player.getName().equals(targetName))
                .findAny().isPresent();

        if(!online){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        new FriendRequestPacket().send(
                fromUUID, AlbyFriends.get().getProxy().getPlayer(targetName).getUniqueId().toString(),
                this.generateRequestID(from.getName(), targetName, fromUUID, AlbyFriends.get().getProxy().getPlayer(targetName).getUniqueId()));

    }

    public void acceptRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);
        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(contextIDs[0]);
        ProxiedPlayer target = AlbyFriends.get().getProxy().getPlayer(contextIDs[1]);

        // TODO Use playerData system to save new friend relation.

        this.requestIds.remove(requestID);
        from.sendMessage(ChatColor.GOLD + target.getName() + CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_FROM);
        target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_TARGET + "" + ChatColor.GOLD + from.getName());

    }

    public void declineRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);
        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(contextIDs[0]);
        ProxiedPlayer target = AlbyFriends.get().getProxy().getPlayer(contextIDs[1]);

        this.requestIds.remove(requestID);
        from.sendMessage(ChatColor.GOLD + target.getName() + CommandResponsePattern.RESPONSE_REQUEST_DECLINED_FROM);
        target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_DECLINED_TARGET + "" + ChatColor.GOLD + from.getName());

    }

    // -- Requests IDs management methods --

    private String generateRequestID(String fromName, String targetName, UUID fromUUID, UUID targetUUID){
        String key = fromName +"-"+ targetName;
        this.requestIds.put(key, new UUID[] {fromUUID, targetUUID});
        return key;
    }

    // -- Fromatted messages methods --

    public TextComponent getFormattedAcceptMessage(String requestID){
        TextComponent message = new TextComponent("[ACCEPTER]");
        message.setColor(ChatColor.GREEN);
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requestID));
        return message;
    }

    public TextComponent getFormattedDeclineMessage(String requestID){
        TextComponent message = new TextComponent("[REFUSER]");
        message.setColor(ChatColor.RED);
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + requestID));
        return message;
    }

}
