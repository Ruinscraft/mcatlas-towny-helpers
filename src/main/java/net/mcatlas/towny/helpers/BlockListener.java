package net.mcatlas.towny.helpers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import net.md_5.bungee.api.ChatColor;

public class BlockListener implements Listener {

	private Set<UUID> recents = new HashSet<>();

	public void onBlockPlace(BlockPlaceEvent event) {
		Location location = event.getBlock().getLocation();
		TownBlock townBlock = TownyUniverse.getTownBlock(location);
		if (townBlock != null && townBlock.hasTown()) return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material material = block.getBlockData().getMaterial();
		if (material.equals(Material.POWERED_RAIL) || material.equals(Material.RAIL)) {
			if (player.getGameMode() != GameMode.SURVIVAL) return; 
			if (!recents.contains(player.getUniqueId())) {
				player.sendMessage("");
				player.sendMessage(ChatColor.RED + 
						"** Reminder: Destroying public paths / railways is not allowed and is punishable. **");
				player.sendMessage("");

				block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation(), 1);
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
