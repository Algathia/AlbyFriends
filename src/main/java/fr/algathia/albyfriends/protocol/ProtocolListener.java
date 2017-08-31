package fr.algathia.albyfriends.protocol;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.algathiaapi.utils.ChatColor;
import fr.algathia.algathiaapi.utils.RedisConstant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import java.util.concurrent.TimeUnit;

/**
 * @author Vialonyx
 */

public class ProtocolListener extends JedisPubSub {

    public ProtocolListener(){

        AlbyFriends.get().getProxy().getScheduler().schedule(AlbyFriends.get(), () -> {

            try(Jedis jedis = AlbyFriends.get().getJedisUtils().getJedis(RedisConstant.DBID_COMM)){
                AlbyFriends.get().getLogger().info(ChatColor.GREEN + "Listening.");
                jedis.subscribe(this, RedisConstant.COMM_CHANNEL_FRIENDS);
            }

        }, 0, 50, TimeUnit.MILLISECONDS);

    }

    @Override
    public void onMessage(String channel, String message){
        String command = message.split(" ")[0];
        String[] args = message.replace(command + " ", "").split(" ");
        AlbyFriends.get().getProtocolManager().execute(command, args);
    }

}
