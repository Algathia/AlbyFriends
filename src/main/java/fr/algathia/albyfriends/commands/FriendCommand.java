package fr.algathia.albyfriends.commands;

import fr.algathia.albyfriends.AlbyFriends;
import fr.algathia.albyfriends.protocol.packet.ResponseRequestPacket;
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

        if(args.length < 1){
            Arrays.stream(CommandResponsePattern.RESPONSE_HELP_MAIN.getContent()).forEach(line -> player.sendMessage(line));
            return;
        }

        switch (args[0].toLowerCase()){

            case "add":
                if(args.length < 2){
                    Arrays.stream(CommandResponsePattern.RESPONSE_HELP_ADD.getContent()).forEach(line -> player.sendMessage(line));
                    break;
                }
                AlbyFriends.get().getFriendManager().sendFriendRequest(player.getUniqueId(), args[1]);
                break;
            case "accept": // ** NO-VISIBLE COMMAND **
                if(args.length < 2){
                    break;
                }

                new ResponseRequestPacket().send(player.getUniqueId(), args[1], "y");
                break;
            case "decline": // ** NO-VISIBLE COMMAND **
                if(args.length < 2){
                    break;
                }

                new ResponseRequestPacket().send(player.getUniqueId(), args[1], "n");
                break;
            default:
                Arrays.stream(CommandResponsePattern.RESPONSE_HELP_MAIN.getContent()).forEach(line -> player.sendMessage(line));
                break;

        }

    }

}
