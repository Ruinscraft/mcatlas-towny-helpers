package net.mcatlas.townyhelpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;

import net.md_5.bungee.api.ChatColor;

public class TownyListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onNewTown(NewTownEvent event) {
		sendUpkeepMessage(Bukkit.getPlayer(event.getTown().getMayor().getName()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onNewNation(NewNationEvent event) {
		sendUpkeepMessage(Bukkit.getPlayer(event.getNation().getCapital().getMayor().getName()));
	}

	private void sendUpkeepMessage(Player player) {
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD 
				+ "!!! Make sure you deposit enough gold to pay for your daily upkeep! " 
				+ ChatColor.WHITE + "/towny prices");
	}

}
