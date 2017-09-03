package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import fr.algathia.networkmanager.protocol.packet.SendMessagePacket;
import fr.algathia.networkmanager.utils.BungeeUUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Vialonyx
 */

public class FriendManager {

    private Map<String, UUID[]> requestIds;
    private SendMessagePacket messagePacket;

    public FriendManager(){
        this.requestIds = new HashMap<>();
        this.messagePacket = new SendMessagePacket(AlbyFriends.get().getNetworkManager());
    }

    // -- Core methods --

    public SendMessagePacket getMessagePacket(){
        return this.messagePacket;
    }

    public void sendFriendRequest(UUID fromUUID, String targetName){

        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(fromUUID);

        // Checking if player is online
        try {
            AlbyFriends.get().getPlayerCache().get(from.getUniqueId());
        } catch (ExecutionException e) {
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).map(TextComponent::new).forEach(from::sendMessage);
            return;
        }

        UUID targetUUID = BungeeUUIDFetcher.getUUID(targetName);

        String key = this.generateRequestID(from.getName(), targetName, fromUUID, targetUUID);

        // Sending request
        new FriendRequestPacket().send(
                fromUUID, targetUUID.toString(), key);

    }

    public void acceptRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);

        try {

            FriendPlayer from = AlbyFriends.get().getPlayerCache().get(contextIDs[0]);
            FriendPlayer target = AlbyFriends.get().getPlayerCache().get(contextIDs[1]);

            from.getFriends().add(contextIDs[1]);
            target.getFriends().add(contextIDs[0]);
            this.requestIds.remove(requestID);

            TextComponent fromComp = new TextComponent(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_FROM.getContent()[0]);
            TextComponent targetComp = new TextComponent(CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_TARGET.getContent()[0] + "" + ChatColor.GOLD + from.getPlayerName());

            // Sending messages
            from.sendMessage(fromComp);
            target.sendMessage(targetComp);

        } catch (ExecutionException e) {
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(
                    line -> this.messagePacket.send(contextIDs[1], BungeeCord.getInstance().gson.toJson(new TextComponent(line))));
            return;
        }

    }

    public void declineRequest(String requestID){

        if(!this.requestIds.containsKey(requestID)){
            return;
        }

        UUID[] contextIDs = this.requestIds.get(requestID);

        try {

            FriendPlayer from = AlbyFriends.get().getPlayerCache().get(contextIDs[0]);
            FriendPlayer target = AlbyFriends.get().getPlayerCache().get(contextIDs[1]);
            this.requestIds.remove(requestID);

            from.sendMessage(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_DECLINED_FROM.getContent()[0]);
            target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_DECLINED_TARGET.getContent()[0] + ChatColor.GOLD + from.getPlayerName());

        } catch (ExecutionException e) {
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(
                    line -> this.messagePacket.send(contextIDs[1], BungeeCord.getInstance().gson.toJson(new TextComponent(line)))
            );
            return;
        }

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
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GOLD + "Cliquez pour accepter !").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requestID));
        return message;
    }

    public TextComponent getFormattedDeclineMessage(String requestID){
        TextComponent message = new TextComponent("[REFUSER]");
        message.setColor(ChatColor.RED);
        message.setBold(true);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GOLD + "Cliquez pour refuser !").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + requestID));
        return message;
    }

}
