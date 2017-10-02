package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.FriendCommand;
import fr.algathia.albyfriends.protocol.ProtocolListener;
import fr.algathia.albyfriends.protocol.ProtocolManager;
import fr.algathia.algathiaapi.api.AlgathiaAPI;
import fr.algathia.commons.JedisUtils;
import fr.algathia.networkmanager.NetworkManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Level;

public class AlbyFriends extends Plugin implements Listener {

    private static AlbyFriends instance;
    private Configuration configuration;
    private JedisUtils jedisUtils;
    private ProtocolManager protocolManager;
    private FriendManager friendManager;

    @Override
    public void onEnable(){

        instance = this;

        // Initialization
         initConfig(this.getDataFolder());
         initConnetions();

        // Protocol
        this.protocolManager = new ProtocolManager();
        new ProtocolListener();

        // Listener(s)
        this.getProxy().getPluginManager().registerListener(this, this);

        // Manager(s)
        this.friendManager = new FriendManager();

        // Command(s)
        getProxy().getPluginManager().registerCommand(this, new FriendCommand());

        getLogger().log(Level.INFO, () -> ChatColor.GREEN + "Friend system succesfully loaded.");

    }

    @Override
    public void onDisable(){
        this.jedisUtils.close();
        super.onDisable();
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event){
        this.internalPlayerSave(event.getPlayer().getUniqueId());
    }

    private void initConnetions() {
        this.jedisUtils = new JedisUtils(configuration.getString("redis.ip"), configuration.getString("redis.password"), AlgathiaAPI.getInstance().getServerName() + "-friends");
    }

    private void initConfig(File file) {
        File configFile = new File(file, "config.yml");
        if(!configFile.getParentFile().exists())
            configFile.getParentFile().mkdirs();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(configFile.getName())) {
            if (!configFile.exists()) {
                Files.copy(in, configFile.toPath());
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
            System.exit(0);
        }
    }

    private void internalPlayerSave(UUID playerID){

        try {
            FriendPlayer player = new FriendPlayer(playerID);
            player.saveFriends();
        } catch (IllegalAccessError e){
            this.getLogger().info("Error during friend saving for player " + playerID);
            return;
        }

    }

    public static AlbyFriends get(){
        return instance;
    }

    public NetworkManager getNetworkManager(){
        return (NetworkManager) this.getProxy().getPluginManager().getPlugin("NetworkManager");
    }

    public ProtocolManager getProtocolManager(){
        return this.protocolManager;
    }

    public FriendManager getFriendManager(){
        return this.friendManager;
    }

    public JedisUtils getJedisUtils(){
        return this.jedisUtils;
    }

}
