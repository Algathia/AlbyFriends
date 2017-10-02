package fr.algathia.albyfriends.commands;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.FriendPlayer;
import fr.algathia.albyfriends.protocol.packet.ResponseRequestPacket;
import fr.algathia.networkmanager.utils.BungeeUUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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

        FriendPlayer player = new FriendPlayer(BungeeUUIDFetcher.getUUID(commandSender.getName()));

        if(args.length < 1){
            Arrays.stream(CommandResponsePattern.RESPONSE_MAINLIST.getContent()).forEach(line -> player.sendMessage(line));

            TextComponent formattedFriendList = new TextComponent();
            player.getFriends().forEach(friend -> formattedFriendList.addExtra(ChatColor.GREEN + player.getPlayerName() + ", "));
            player.sendMessage(formattedFriendList);

            player.sendMessage(CommandResponsePattern.GLOBAL_SEPARATOR.getContent()[0]);
            return;
        }

        switch (args[0].toLowerCase()) {

            case "help":
                Arrays.stream(CommandResponsePattern.RESPONSE_COMMANDLIST.getContent()).forEach(line -> player.sendMessage(line));
                break;
            case "add":
                if(args.length < 2){
                    Arrays.stream(CommandResponsePattern.RESPONSE_HELP_ADD.getContent()).forEach(line -> player.sendMessage(line));
                    break;
                }
                AlbyFriends.get().getFriendManager().sendFriendRequest(player.getUUID(), args[1]);
                break;
            case "accept": // ** NO-VISIBLE COMMAND **
                if(args.length < 2){
                    break;
                }

                new ResponseRequestPacket().send(player.getUUID(), args[1], "y");
                break;
            case "decline": // ** NO-VISIBLE COMMAND **
                if(args.length < 2){
                    break;
                }

                new ResponseRequestPacket().send(player.getUUID(), args[1], "n");
                break;
            default:
                Arrays.stream(CommandResponsePattern.RESPONSE_COMMANDLIST.getContent()).forEach(line -> player.sendMessage(line));
                break;

        }

    }

}
