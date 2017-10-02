package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import fr.algathia.commons.RedisConstant;
import fr.algathia.networkmanager.utils.BungeeUUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import redis.clients.jedis.Jedis;
import java.util.*;

/**
 * @author Vialonyx
 */

public class FriendManager {

    Jedis redis;

    public FriendManager(){

        /**
         * KEY(id)      KEY(value)
         * request-id   json(uuid-from/uuid-target)
         */

        this.redis = AlbyFriends.get().getJedisUtils().getJedis(RedisConstant.DBID_COMM);

    }

    // -- Core methods --

    public void sendFriendRequest(UUID fromUUID, String targetName){

        FriendPlayer from = new FriendPlayer(fromUUID);
        FriendPlayer target = null;

        // Check if the player is online
        try {
            target = new FriendPlayer(BungeeUUIDFetcher.getUUID(targetName));
        } catch (IllegalAccessError | IllegalArgumentException e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Check if the player is trying to add itself
        if(from.getUUID().equals(target.getUUID())){
            Arrays.stream(CommandResponsePattern.RESPONSE_ADD_ITSELF.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Check if player is not already friends
        if(this.isFriends(from, target)){
            Arrays.stream(CommandResponsePattern.RESPONSE_FRIENDS_ALREADY.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Check if the sender has not a pending request with the target
        Map<String, String> requestKeys = this.redis.hgetAll(RedisConstant.COMM_FRIENDS_REQUESTS_IDS);
        if(requestKeys.containsKey(this.generateRequestID(from.getPlayerName(), targetName, fromUUID, target.getUUID()))){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_EXISTS.getContent()).forEach(line -> from.sendMessage(line));
            return;
        }

        // Generate & sending the request key
        String key = this.generateRequestID(from.getPlayerName(), targetName, fromUUID, target.getUUID());
        this.redis.hset(RedisConstant.COMM_FRIENDS_REQUESTS_IDS, key, BungeeCord.getInstance().gson.toJson(fromUUID + ":" + target.getUUID()));

        // Sending request
        new FriendRequestPacket().send(fromUUID, target.getUUID().toString(), key);

    }

    public void acceptRequest(String requestID){

        if(!this.isValidKey(requestID)){
            return;
        }

        Map<String, String> requestKeys = this.redis.hgetAll(RedisConstant.COMM_FRIENDS_REQUESTS_IDS);
        String[] uuids = requestKeys.get(requestID).replace("\"", "").split(":");

        FriendPlayer from = null;
        FriendPlayer target = new FriendPlayer(UUID.fromString(uuids[1]));

        try {
            from = new FriendPlayer(UUID.fromString(uuids[0]));
        } catch (IllegalAccessError | IllegalArgumentException e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> target.sendMessage(line));
            return;
        }

        target.getFriends().add(UUID.fromString(uuids[0]));
        this.redis.hdel(RedisConstant.COMM_FRIENDS_REQUESTS_IDS, requestID);   // NOT SURE, NEED TO BE TESTED /!\

        TextComponent fromComp = new TextComponent(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_FROM.getContent()[0]);
        TextComponent targetComp = new TextComponent(CommandResponsePattern.RESPONSE_REQUEST_ACCEPTED_TARGET.getContent()[0] + "" + ChatColor.GOLD + from.getPlayerName() + ChatColor.GREEN + " !");

        // Sending messages
        from.sendMessage(fromComp);
        target.sendMessage(targetComp);

    }

    public void declineRequest(String requestID){

        if(!this.isValidKey(requestID)){
            return;
        }

        Map<String, String> requestKeys = this.redis.hgetAll(RedisConstant.COMM_FRIENDS_REQUESTS_IDS);
        String[] uuids = requestKeys.get(requestID).replace("\"", "").split(":");

        FriendPlayer from = null;
        FriendPlayer target = new FriendPlayer(UUID.fromString(uuids[1]));

        try {
            from = new FriendPlayer(UUID.fromString(uuids[0]));
        } catch (IllegalAccessError | IllegalArgumentException e){
            Arrays.stream(CommandResponsePattern.RESPONSE_REQUEST_OFFLINE.getContent()).forEach(line -> target.sendMessage(line));
            return;
        }

        from.sendMessage(ChatColor.GOLD + target.getPlayerName() + CommandResponsePattern.RESPONSE_REQUEST_DECLINED_FROM.getContent()[0]);
        target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_DECLINED_TARGET.getContent()[0] + ChatColor.GOLD + from.getPlayerName() + ChatColor.RED + ".");

        this.redis.hdel(RedisConstant.COMM_FRIENDS_REQUESTS_IDS, requestID);   // NOT SURE, NEED TO BE TESTED /!\

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
        return key;

    }

    private boolean isValidKey(String requestKey){

        if(this.redis.hgetAll(RedisConstant.COMM_FRIENDS_REQUESTS_IDS).containsKey(requestKey)){
            return true;
        }

        return false;

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
