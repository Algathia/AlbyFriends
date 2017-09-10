package fr.algathia.albyfriends;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fr.algathia.albyfriends.commands.FriendCommand;
import fr.algathia.albyfriends.protocol.ProtocolListener;
import fr.algathia.albyfriends.protocol.ProtocolManager;
import fr.algathia.algathiaapi.api.AlgathiaAPI;
import fr.algathia.algathiaapi.utils.JedisUtils;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AlbyFriends extends Plugin implements Listener {

    private static AlbyFriends instance;
    private Configuration configuration;
    private JedisUtils jedisUtils;
    private ProtocolManager protocolManager;
    private FriendManager friendManager;
    private LoadingCache<UUID, FriendPlayer> playerCache;

    @Override
    public void onEnable(){

        instance = this;

        // Initialization
         initConfig(this.getDataFolder());
         initConnetions();
         this.playerCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<UUID, FriendPlayer>() {
             @Override
             public FriendPlayer load(UUID uuid) throws Exception {
                 return new FriendPlayer(uuid);
             }
         });

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
            FriendPlayer player = this.getPlayerCache().get(playerID);
            player.saveFriends();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        }

        this.getPlayerCache().invalidate(playerID);

    }

    public static AlbyFriends get(){
        return instance;
    }

    public NetworkManager getNetworkManager(){
        return (NetworkManager) this.getProxy().getPluginManager().getPlugin("NetworkManager");
    }

    public LoadingCache<UUID, FriendPlayer> getPlayerCache(){
        return this.playerCache;
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
