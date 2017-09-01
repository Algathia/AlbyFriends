package fr.algathia.albyfriends;

import fr.algathia.albyfriends.commands.FriendCommand;
import fr.algathia.albyfriends.protocol.ProtocolListener;
import fr.algathia.albyfriends.protocol.ProtocolManager;
import fr.algathia.algathiaapi.api.AlgathiaAPI;
import fr.algathia.algathiaapi.utils.JedisUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class AlbyFriends extends Plugin {

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

        // Manager(s)
        this.friendManager = new FriendManager();

        // Commands
        getProxy().getPluginManager().registerCommand(this, new FriendCommand());

        getLogger().info(ChatColor.GREEN + "Friends system succesfully loaded.");

    }

    @Override
    public void onDisable(){
        this.jedisUtils.close();
        super.onDisable();
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

    public static AlbyFriends get(){
        return instance;
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
