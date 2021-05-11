package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BlockListener implements Listener {

    private Set<UUID> recents = new HashSet<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        if (townBlock != null && townBlock.hasTown()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getBlockData().getMaterial();
        if (material.equals(Material.POWERED_RAIL) || material.equals(Material.RAIL)) {
            if (player.getGameMode() != GameMode.SURVIVAL) return;
            if (!recents.contains(player.getUniqueId())) {
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD +
                        "** Reminder: Destroying public paths / railways is not allowed and is punishable. **");
                player.sendMessage("");

                block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(.5, 0, .5), 1,
                        new Particle.DustOptions(Color.RED, 10));
                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 5, 1);
                event.setCancelled(true);

                recents.add(player.getUniqueId());
                // remove after 10 minutes
                Bukkit.getScheduler().runTaskLater(TownyHelpersPlugin.get(), () -> {
                    recents.remove(player.getUniqueId());
                }, 20 * 60 * 10);
            }
        }
    }
}
