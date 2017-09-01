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

    private List<String> usedRequestsIDs;

    public FriendManager(){
        this.usedRequestsIDs = new ArrayList<>();
    }

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

    // -- Requests IDs management methods --

    private String generateRequestID(String fromName, String targetName){
        String key = this.getFirstCaracters(fromName) +"-"+ this.getFirstCaracters(targetName);
        this.usedRequestsIDs.add(key);
        AlbyFriends.get().getLogger().info("(DEBUG) Data added to Request IDs " + key);
        return key;
    }

    private String getFirstCaracters(String s){
        return s.substring(3, Math.min(s.length(), 3));
    }

    public boolean requestExists(String fromName, String targetName){
        if(this.usedRequestsIDs.contains(this.getFirstCaracters(fromName)+"-"+this.getFirstCaracters(targetName))){
            return true;
        }
        return false;
    }

    // -- Fromatted messages methods --

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
