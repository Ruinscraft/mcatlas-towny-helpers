package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.SpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
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

    @EventHandler
    public void onTownySpawn(SpawnEvent event) {
        if (event.getPlayer().hasPermission("mcatlas.townyhelpers.spawnfromanywhere")) {
            return;
        }

        if (event.getFrom().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You must use a portal to return to the Earth world.");
        }
    }

}
