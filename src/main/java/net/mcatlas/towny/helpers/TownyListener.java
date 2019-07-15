package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        sendUpkeepMessage(Bukkit.getPlayer(event.getTown().getMayor().getName()));
    }

    @EventHandler
    public void onNewNation(NewNationEvent event) {
        sendUpkeepMessage(Bukkit.getPlayer(event.getNation().getCapital().getMayor().getName()));
    }

    private void sendUpkeepMessage(Player player) {
        player.sendMessage(
                ChatColor.RED.toString() + ChatColor.BOLD
                        + "Make sure you deposit enough gold "
                        + ChatColor.WHITE + "(/t deposit <amt>)"
                        + ChatColor.RED.toString() + ChatColor.BOLD
                        + " to pay for your daily upkeep! "
                        + ChatColor.WHITE + "(/towny prices)");
    }

}
