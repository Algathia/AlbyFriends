package fr.algathia.albyfriends;

import net.md_5.bungee.api.plugin.Plugin;

public class AlbyFriends extends Plugin {

    private static AlbyFriends instance;

    @Override
    public void onEnable(){

        instance = this;
        getLogger().info("Test");

    }

    @Override
    public void onDisable(){

    }

    public static AlbyFriends get(){
        return instance;
    }

}
