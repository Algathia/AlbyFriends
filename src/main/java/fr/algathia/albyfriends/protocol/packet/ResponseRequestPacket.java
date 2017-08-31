package fr.algathia.albyfriends.protocol.packet;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.RequestResponseValue;
import fr.algathia.albyfriends.protocol.Packet;
import fr.algathia.algathiaapi.utils.RedisConstant;

import java.util.UUID;

/**
 * @author Vialonyx
 */

public class ResponseRequestPacket implements Packet {

    RequestResponseValue responseValue;

    @Override
    public void execute(String[] args) {
        // TEMPORARY
        //ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(args[0]);
        //ProxiedPlayer to = AlbyFriends.get().getProxy().getPlayer(args[1]);

        switch (args[2]){
            case "y" :
                this.responseValue = RequestResponseValue.ACCEPTED;
                break;
            case "n":
            default:
                this.responseValue = RequestResponseValue.REFUSED;
            break;
        }

    }

    @Override
    public String name() {
        return RedisConstant.COMM_FRIENDS_RESPONSES;
    }

    @Override
    public void send(UUID from, String... args) {
        AlbyFriends.get().getJedisUtils().publish(RedisConstant.COMM_CHANNEL_FRIENDS, name() + " " + from + " " + args[0] + " " + args[1]);
    }

    public RequestResponseValue getResponse(){
        return this.responseValue;
    }

}
