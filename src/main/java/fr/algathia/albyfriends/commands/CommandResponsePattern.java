package fr.algathia.albyfriends.commands;

import net.md_5.bungee.api.ChatColor;

/**
 * @author Vialonyx
 */

public enum CommandResponsePattern {

    RESPONSE_HELP(ChatColor.AQUA + "----" + ChatColor.RED + " AlbyFriends " + ChatColor.AQUA + "----",
            ChatColor.DARK_RED + "Info : Friend system is already in developement. (By vialonyx, btw).");

    private String[] responseLines;
    CommandResponsePattern(String... lines){
        this.responseLines = lines;
    }

    public String[] getContent(){
        return this.responseLines;
    }

}
