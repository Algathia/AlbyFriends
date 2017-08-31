package fr.algathia.albyfriends.commands;

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

        if(args.length < 2){
            Arrays.stream(CommandResponsePattern.RESPONSE_HELP.getContent()).forEach(line -> player.sendMessage(line));
            return;
        }

    }

}
