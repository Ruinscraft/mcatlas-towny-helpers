package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.event.TownBlockSettingsChangedEvent;
import com.palmergames.bukkit.towny.event.TownSpawnEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownTogglePVPEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownPVPToggleTimer implements Listener {

    private static final int PVP_TIMER_LENGTH_SECONDS = 20;

    private Map<Player, Town> teleportConfirms;
    private Map<Town, Long> recentToggles;

    public TownPVPToggleTimer() {
        teleportConfirms = new ConcurrentHashMap<>();
        recentToggles = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onTeleportWarnIfAboutToEnterPVPZone(TownSpawnEvent event) {
        final Town town = event.getToTown();
        final Player player = event.getPlayer();

        // Residents don't have to confirm a teleport
        if (town.hasResident(player.getName())) {
            return;
        }

        // Player has confirmed a teleport
        if (teleportConfirms.containsKey(player)) {
            Town confirmTown = teleportConfirms.get(player);
            if (confirmTown.equals(town)) {
                teleportConfirms.remove(player);
            }
            return;
        }

        boolean aboutToEnablePVP = recentToggles.containsKey(town);

        if (aboutToEnablePVP) {
            event.setCancelled(true);
            event.setCancelMessage("The town you tried to teleport to is about to enable PVP. Run this command again if you're sure you want to teleport.");
        }

        if (town.isPVP()) {
            event.setCancelled(true);
            event.setCancelMessage("The town you tried to teleport to has PVP enabled. Run this command again if you're sure you want to teleport.");
        }

        if (event.isCancelled()) {
            teleportConfirms.put(player, town);
            // Remove from confirms after 30s
            TownyHelpersPlugin.get().getServer().getScheduler().runTaskLater(TownyHelpersPlugin.get(), () -> {
                teleportConfirms.remove(player);
            }, 30 * 20L);
        }
    }

    @EventHandler
    public void onTownTogglePVP(TownTogglePVPEvent event) {
        final Town town = event.getTown();

        // Town recently toggled pvp
        if (recentToggles.containsKey(town)) {
            event.setCancelled(true);
            event.setCancellationMsg("Wait to use this again.");
            return;
        }

        final long toggledAt = System.currentTimeMillis();
        final boolean newPVPState = event.getFutureState();
        String newPVPStateString = newPVPState ? "enabled" : "disabled";

        recentToggles.put(town, toggledAt);

        // Cancel the event
        event.setCancelled(true);
        event.setCancellationMsg("PVP will be " + newPVPStateString + " after the countdown.");

        BukkitRunnable pvpTimerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (town == null || !town.hasMayor()) {
                    // Deleted town or something?
                    cancel();
                    recentToggles.remove(town);
                    return;
                }

                if (!recentToggles.containsKey(town)) {
                    cancel();
                    return;
                }

                long secondsSinceToggle = (System.currentTimeMillis() - toggledAt) / 1000;
                Set<Player> playersInTown = getPlayersCurrentlyInTown(town);

                if (secondsSinceToggle > PVP_TIMER_LENGTH_SECONDS) {
                    // Update town pvp status and save
                    town.setPVP(newPVPState);
                    saveTownPVPSetting(town);
                    // Cancel timer task
                    cancel();
                    // Remove town from recent toggles
                    recentToggles.remove(town);
                    // Alert players
                    List<String> playerNamesInTown = new ArrayList<>();
                    for (Player player : playersInTown) {
                        playerNamesInTown.add(player.getName());
                        player.sendTitle(ChatColor.YELLOW + "PVP is now " + newPVPStateString + "!", "", 0, 20 * 5, 0);
                    }
                    // Log players currently in the town
                    TownyHelpersPlugin.get().getLogger().info(town.getName() + " has " + newPVPStateString + " PVP. Players in town [" + String.join(", ", playerNamesInTown) + "]");
                } else {
                    for (Player player : playersInTown) {
                        player.sendTitle(ChatColor.YELLOW + "PVP " + newPVPStateString + " in " + (PVP_TIMER_LENGTH_SECONDS - secondsSinceToggle) + "s!", "The town you are in has toggled PVP", 0, 20 * 2, 0);
                    }
                }
            }
        };

        // Run the pvp timer every second
        pvpTimerRunnable.runTaskTimer(TownyHelpersPlugin.get(), 0L, 20L);
    }

    // From https://github.com/TownyAdvanced/Towny/blob/8f7f75f2904891ed4071279132d1500d4bb459aa/src/com/palmergames/bukkit/towny/command/TownCommand.java#L1605
    private static void saveTownPVPSetting(Town town) {
        for (TownBlock townBlock : town.getTownBlocks()) {
            if (!townBlock.hasResident() && !townBlock.isChanged()) {
                townBlock.setType(townBlock.getType());
                townBlock.save();
            }
        }
        //Change settings event
        Bukkit.getServer().getPluginManager().callEvent(new TownBlockSettingsChangedEvent(town));
        // Save the Town.
        town.save();
    }

    private static Set<Player> getPlayersCurrentlyInTown(Town town) {
        Set<Player> players = new HashSet<>();
        // maybe not the most efficient way to calculate this but itll work
        for (TownBlock townBlock : town.getTownBlocks()) {
            Chunk chunk = town.getWorld().getChunkAt(townBlock.getX(), townBlock.getZ());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().equals(town.getWorld())) {
                    continue;
                }
                if (player.getLocation().getChunk().equals(chunk)) {
                    players.add(player);
                }
            }
        }
        return players;
    }

}
