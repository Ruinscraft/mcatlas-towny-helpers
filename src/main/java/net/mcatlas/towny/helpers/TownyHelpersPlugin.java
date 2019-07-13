package net.mcatlas.towny.helpers;

import org.bukkit.plugin.java.JavaPlugin;

public class TownyHelpersPlugin extends JavaPlugin {

	private static TownyHelpersPlugin townyHelpers;

	public static TownyHelpersPlugin getTownyHelpers() {
		return townyHelpers;
	}

	@Override
	public void onEnable() {
		townyHelpers = this;

		getServer().getPluginManager().registerEvents(new TownyListener(), this);
	}

	public void onDisable() {
		townyHelpers = null;
	}

}
