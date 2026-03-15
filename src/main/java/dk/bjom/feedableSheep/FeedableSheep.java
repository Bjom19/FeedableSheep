package dk.bjom.feedableSheep;

import org.bukkit.plugin.java.JavaPlugin;

public final class FeedableSheep extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        saveDefaultConfig();

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
