package fr.algathia.albyfriends.commands;

import fr.algathia.algathiaapi.tools.CharUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * @author Vialonyx
 */

public enum CommandResponsePattern {

    GLOBAL_SEPARATOR(ChatColor.stripColor("--------------------------------------------")),

    RESPONSE_MAINLIST(GLOBAL_SEPARATOR.getContent()[0],
            ChatColor.RED + "                    " + CharUtils.GAMING_CROSS.toString() + ChatColor.YELLOW + " Vos amis " + ChatColor.RED + CharUtils.GAMING_CROSS.toString(),
            ChatColor.AQUA + "- Utilisez" + ChatColor.GOLD + " /friend help " + ChatColor.AQUA + "pour lister les commandes disponibles !"),

    RESPONSE_COMMANDLIST(GLOBAL_SEPARATOR.getContent()[0],
            ChatColor.GOLD + "/friend " + ChatColor.RED + "| Voir vos amis",
            ChatColor.GOLD + "/friend " + ChatColor.YELLOW + "add " + ChatColor.GOLD + "<Joueur> " + ChatColor.RED + "| Envoyer une demande d'ami",
            ChatColor.GOLD + "/friend " + ChatColor.YELLOW + "remove " + ChatColor.GOLD + "<Joueur> " + ChatColor.RED + "| Supprimer un joueur de vos amis",
            GLOBAL_SEPARATOR.getContent()[0]),

    RESPONSE_HELP_ADD(ChatColor.RED + "Utilisation : " + ChatColor.GOLD + "/friend " + ChatColor.YELLOW + "add " + ChatColor.GOLD + "<Joueur>"),

    RESPONSE_HELP_REMOVE(ChatColor.RED + "Utilisation : " + ChatColor.GOLD + "/friend " + ChatColor.YELLOW + "remove " + ChatColor.GOLD + "<Joueur>"),

    RESPONSE_REQUEST_SEND_SUCCESS(ChatColor.GREEN + "Une demande d'ami a été envoyée a "),

    RESPONSE_REQUEST_NEW(ChatColor.GREEN + "Vous avez reçu une demande d'ami de "),

    RESPONSE_REQUEST_OFFLINE(ChatColor.RED + "Le joueur n'est pas en ligne"),

    RESPONSE_REQUEST_ACCEPTED_FROM(ChatColor.GREEN + " a accepté votre demande d'ami !"),
    RESPONSE_REQUEST_ACCEPTED_TARGET(ChatColor.GREEN + "Vous avez accepté la demande d'ami de "),

    RESPONSE_REQUEST_DECLINED_FROM(ChatColor.RED + " a refusé votre demande d'ami."),
    RESPONSE_REQUEST_DECLINED_TARGET(ChatColor.RED + "Vous avez refusé la demande d'ami de "),

    RESPONSE_FRIENDS_ALREADY(ChatColor.RED + "Vous êtes déjà amis avec ce joueur !"),

    RESPONSE_REQUEST_EXISTS(ChatColor.RED + "Vous avez déjà une demande d'ami en attente avec ce joueur"),

    RESPONSE_ADD_ITSELF(ChatColor.RED + "Vous ne pouvez pas vous ajouter vous-même en ami.");

    private String[] responseLines;
    CommandResponsePattern(String... lines){
        this.responseLines = lines;
    }

    public String[] getContent(){
        return this.responseLines;
    }

}
