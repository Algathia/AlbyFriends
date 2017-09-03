package fr.algathia.albyfriends.protocol.packet;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.FriendPlayer;
import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.Packet;
import fr.algathia.algathiaapi.utils.RedisConstant;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Vialonyx
 */

public class FriendRequestPacket implements Packet {

    @Override
    public void execute(String[] args) {

        try {
            FriendPlayer from = AlbyFriends.get().getPlayerCache().get(UUID.fromString(args[0]));
            FriendPlayer target = AlbyFriends.get().getPlayerCache().get(UUID.fromString(args[1]));

            TextComponent generalTarget = new TextComponent();
            generalTarget.addExtra(CommandResponsePattern.GLOBAL_SEPARATOR.getContent()[0] + "\n");
            generalTarget.addExtra(CommandResponsePattern.RESPONSE_REQUEST_NEW.getContent()[0] + ChatColor.GOLD + from.getPlayerName() + ChatColor.GREEN + " !" + "\n");
            generalTarget.addExtra(AlbyFriends.get().getFriendManager().getFormattedAcceptMessage(args[2]));
            generalTarget.addExtra(" ");
            generalTarget.addExtra(AlbyFriends.get().getFriendManager().getFormattedDeclineMessage(args[2]));
            generalTarget.addExtra("\n");
            generalTarget.addExtra(CommandResponsePattern.GLOBAL_SEPARATOR.getContent()[0]);

            TextComponent generalFrom = new TextComponent(
                    CommandResponsePattern.RESPONSE_REQUEST_SEND_SUCCESS.getContent()[0] + ChatColor.GOLD + target.getPlayerName());

            // Sending messages
            target.sendMessage(generalTarget);
            from.sendMessage(generalFrom);

        } catch (ExecutionException e){
            e.printStackTrace();
        }

    }

    @Override
    public String name() {
        return RedisConstant.COMM_FRIENDS_REQUESTS;
    }

    @Override
    public void send(UUID from, String... args) {
        AlbyFriends.get().getJedisUtils().publish(RedisConstant.COMM_CHANNEL_FRIENDS, name() + " " + from + " " + args[0] + " " + args[1]);
    }

}
