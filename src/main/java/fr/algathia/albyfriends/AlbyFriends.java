package fr.algathia.albyfriends;

import fr.algathia.algathiaapi.api.AlgathiaAPI;
import fr.algathia.algathiaapi.utils.JedisUtils;
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

    @Override
    public void onEnable(){

        instance = this;
        getLogger().info("Test");

        // Initialization
         initConfig(this.getDataFolder());
         initConnetions();

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

}
