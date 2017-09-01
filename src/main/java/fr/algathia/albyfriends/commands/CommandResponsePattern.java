package fr.algathia.albyfriends.commands;

import net.md_5.bungee.api.ChatColor;

/**
 * @author Vialonyx
 */

public enum CommandResponsePattern {

    RESPONSE_HELP_MAIN(ChatColor.AQUA + "----" + ChatColor.RED + " AlbyFriends " + ChatColor.AQUA + "----",
            ChatColor.DARK_RED + "Info : Friend system is already in developement. (By vialonyx, btw)."),

    RESPONSE_HELP_ADD(ChatColor.AQUA + "----" + ChatColor.RED + " AlbyFriends " + ChatColor.AQUA + "----",
            ChatColor.RED + "Utilisation : " + ChatColor.GOLD + "/friend " + ChatColor.YELLOW + "add " + ChatColor.GOLD + "<Joueur>"),

    RESPONSE_REQUEST_SEND_SUCCESS(ChatColor.GREEN + "Une demande d'ami a été envoyée a "),
    RESPONSE_REQUEST_NEW(ChatColor.GREEN + "Vous avez reçu une demande d'ami de "),
    RESPONSE_REQUEST_OFFLINE(ChatColor.RED + "Le joueur n'est pas en ligne"),

    RESPONSE_REQUEST_ACCEPTED_FROM(ChatColor.GREEN + " a accepté votre demande d'ami !"),
    RESPONSE_REQUEST_ACCEPTED_TARGET(ChatColor.GREEN + "Vous avez accepté la demande d'ami de "),

    RESPONSE_REQUEST_DECLINED_FROM(ChatColor.RED + " a refusé votre demande d'ami !"),
    RESPONSE_REQUEST_DECLINED_TARGET(ChatColor.RED + "Vous avez refusé la demande d'ami de ");

    private String[] responseLines;
    CommandResponsePattern(String... lines){
        this.responseLines = lines;
    }

    public String[] getContent(){
        return this.responseLines;
    }

}
