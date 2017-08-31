package fr.algathia.albyfriends.protocol.packet;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.protocol.Packet;
import fr.algathia.algathiaapi.utils.RedisConstant;

import java.util.UUID;

/**
 * @author Vialonyx
 */

public class FriendRequestPacket implements Packet {

    @Override
    public void execute(String[] args) {
        // TEMPORARY DEBUG
        //ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(args[0]);
        //ProxiedPlayer to = AlbyFriends.get().getProxy().getPlayer(args[1]);
        AlbyFriends.get().getProxy().broadcast("[Debug] New friend request / " + args[0] + " to " + args[1]);
    }

    @Override
    public String name() {
        return RedisConstant.COMM_FRIENDS_REQUESTS;
    }

    @Override
    public void send(UUID from, String... args) {
        AlbyFriends.get().getJedisUtils().publish(RedisConstant.COMM_CHANNEL_FRIENDS, name() + " " + from + " " + args[0]);
    }

}
