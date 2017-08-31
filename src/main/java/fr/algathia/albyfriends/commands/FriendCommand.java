package fr.algathia.albyfriends.commands;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.RequestResponseValue;
import fr.algathia.albyfriends.protocol.packet.ResponseRequestPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

/**
 * @author Vialonyx
 */

public class FriendCommand extends Command {

    /**
     * FRIEND COMMANDS
     * friend add <Pseudo> (ONLINE)
     * friend remove <Pseudo> (EXISTS)
     */

    public FriendCommand(){
        super("friend");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if(!(commandSender instanceof ProxiedPlayer)){
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        String[] mainMsg = {
                ChatColor.BLUE + "---- " + ChatColor.RED + " AlbyFriends " + ChatColor.BLUE + " ----",
                ChatColor.RED + "Info : Friend system is already in developement. (By vialonyx, btw).",
        };

        if(args.length < 2){
            Arrays.stream(mainMsg).forEach(msg -> player.sendMessage(msg));
            return;
        }

    }

}
