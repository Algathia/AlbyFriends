package fr.algathia.albyfriends.protocol.packet;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.protocol.Packet;
import fr.algathia.algathiaapi.utils.RedisConstant;
import java.util.UUID;

/**
 * @author Vialonyx
 */

public class ResponseRequestPacket implements Packet {

    @Override
    public void execute(String[] args) {

        switch (args[2]){
            case "y":
                AlbyFriends.get().getFriendManager().acceptRequest(args[1]);
                break;
            case "n":
                AlbyFriends.get().getFriendManager().declineRequest(args[1]);
                break;
            default:
            break;
        }

    }

    @Override
    public String name() {
        return RedisConstant.COMM_FRIENDS_RESPONSES;
    }

    @Override
    public void send(UUID from, String... args) {
        // 0: reqId, 1: y|n
        AlbyFriends.get().getJedisUtils().publish(RedisConstant.COMM_CHANNEL_FRIENDS, name() + " " + from + " " + args[0] + " " + args[1]);
    }

}
