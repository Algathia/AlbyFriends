package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import fr.algathia.networkmanager.utils.BungeeUUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

        FriendPlayer from = new FriendPlayer(fromUUID);
        FriendPlayer target = null;

        try {
           target = new FriendPlayer(BungeeUUIDFetcher.getUUID(targetName));
        } catch (IllegalAccessError e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Checking if player is not already friends
        if(this.isFriends(from, target)){
            Arrays.stream(CommandResponsePattern.RESPONSE_FRIENDS_ALREADY.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Generate the request key
        String key = this.generateRequestID(from.getPlayerName(), targetName, fromUUID, target.getUUID());

        // Sending request
        new FriendRequestPacket().send(fromUUID, target.getUUID().toString(), key);

    }

    public void acceptRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);

        FriendPlayer from = null;
        FriendPlayer target = new FriendPlayer(contextIDs[1]);

        try {
            from = new FriendPlayer(contextIDs[0]);
        } catch (IllegalAccessError e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> target.sendMessage(line));
            return;
        }

        target.getFriends().add(contextIDs[0]);
        this.requestIds.remove(requestID);

        TextComponent fromComp = new TextComponent(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_FROM.getContent()[0]);
        TextComponent targetComp = new TextComponent(CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_TARGET.getContent()[0] + "" + ChatColor.GOLD + from.getPlayerName() + ChatColor.GREEN + " !");

        // Sending messages
        from.sendMessage(fromComp);
        target.sendMessage(targetComp);

    }

    public void declineRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);

        FriendPlayer from = null;
        FriendPlayer target = new FriendPlayer(contextIDs[1]);

        try {
            from = new FriendPlayer(contextIDs[0]);
        } catch (IllegalAccessError e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> target.sendMessage(line));
            return;
        }

        this.requestIds.remove(requestID);
        from.sendMessage(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_DECLINED_FROM.getContent()[0]);
        target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_DECLINED_TARGET.getContent()[0] + ChatColor.GOLD + from.getPlayerName() + ChatColor.RED + ".");

    }

    public boolean isFriends(FriendPlayer playerA, FriendPlayer playerB){

        if(playerA.getFriends().contains(playerB.getUUID()) && playerB.getFriends().contains(playerA.getUUID())){
            return true;
        }

        return false;

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
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "[:)] Cliquez pour accepter !").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requestID));
        return message;
    }

    public TextComponent getFormattedDeclineMessage(String requestID){
        TextComponent message = new TextComponent("[REFUSER]");
        message.setColor(ChatColor.RED);
        message.setBold(true);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "[:(] Cliquez pour refuser !").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + requestID));
        return message;
    }

}
