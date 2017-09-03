package fr.algathia.albyfriends.protocol.packet;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.commands.CommandResponsePattern;
import fr.algathia.albyfriends.protocol.Packet;
import fr.algathia.algathiaapi.utils.RedisConstant;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.UUID;

/**
 * @author Vialonyx
 */

public class FriendRequestPacket implements Packet {

    @Override
    public void execute(String[] args) {

        ProxiedPlayer from = AlbyFriends.get().getProxy().getPlayer(UUID.fromString(args[0]));
        ProxiedPlayer target = AlbyFriends.get().getProxy().getPlayer(UUID.fromString(args[1]));

        TextComponent choiseComp = new TextComponent();
        choiseComp.addExtra(AlbyFriends.get().getFriendManager().getFormattedAcceptMessage(args[2]));
        choiseComp.addExtra(" ");
        choiseComp.addExtra(AlbyFriends.get().getFriendManager().getFormattedDeclineMessage(args[2]));

        // Message
        target.sendMessage(CommandResponsePattern.GLOBAL_SEPARATOR.getContent()[0]);
        target.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_NEW.getContent()[0] + ChatColor.GOLD + from.getName() + ChatColor.GREEN + " !");
        target.sendMessage(choiseComp);
        target.sendMessage(CommandResponsePattern.GLOBAL_SEPARATOR.getContent()[0]);

        from.sendMessage(CommandResponsePattern.RESPONSE_REQUEST_SEND_SUCCESS.getContent()[0] + ChatColor.GOLD + target.getDisplayName());

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
