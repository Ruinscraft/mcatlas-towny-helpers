package net.mcatlas.towny.helpers;

import org.bukkit.plugin.java.JavaPlugin;

public class TownyHelpersPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new TownyListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
    }

    public void onDisable() {
        instance = null;
    }

    private static TownyHelpersPlugin instance;

    public static TownyHelpersPlugin get() {
        return instance;
    }

}
