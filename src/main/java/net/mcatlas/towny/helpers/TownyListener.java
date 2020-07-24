package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

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
    public void onTownAddResident(TownAddResidentEvent event) {
        Resident resident = event.getResident();
        List<String> ranks = new ArrayList<>(resident.getNationRanks());
        for (String rank : resident.getTownRanks()) {
            ranks.add(rank);
        }
        for (String rank : ranks) {
            TownyHelpersPlugin.get().getLogger().info("Removing rank " + rank + " from " + resident.getName());
            try {
                resident.removeNationRank(rank);
                resident.removeTownRank(rank);
            } catch (NotRegisteredException e) { }
        }
    }

    @EventHandler
    public void onNationAddTown(NationAddTownEvent event) {
        Town town = event.getTown();
        for (Resident resident : town.getResidents()) {
            List<String> ranks = new ArrayList<>(resident.getNationRanks());
            for (String rank : ranks) {
                TownyHelpersPlugin.get().getLogger().info("Removing rank " + rank + " from " + resident.getName());
                try {
                    resident.removeNationRank(rank);
                } catch (NotRegisteredException e) { }
            }
        }
    }

    @EventHandler
    public void onTownySpawn(SpawnEvent event) {
        if (event.getFrom().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You must use a portal to return to the Earth world.");
        }
    }

}
