package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.SpawnEvent;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    @EventHandler
    public void onTownyClaim(TownClaimEvent event) {
        TownBlock townBlock = event.getTownBlock();
        if (townBlock == null) return;

        WorldCoord coord = townBlock.getWorldCoord();
        Location location = new Location(coord.getBukkitWorld(), (coord.getX() * 16) + 8, 64, (coord.getZ() * 16) + 8);
        Block block = location.getBlock();
        if (block == null) return;
        if (block.getBiome() != null && block.getBiome().name().contains("OCEAN")) {
            Resident resident = event.getResident();
            if (resident == null) return;
            Player player = resident.getPlayer();
            if (player == null || !player.isOnline()) return;

            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Warning: Building directly in the ocean " +
                    "is (usually) not allowed. Please read our rules on building in the ocean. Your build may be " +
                    "deleted if it does not follow the rules.");
            player.sendMessage(ChatColor.GRAY + "https://mcatlas.net/rules");
        }
    }

}
