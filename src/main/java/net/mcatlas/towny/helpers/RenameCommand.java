package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NationPreRenameEvent;
import com.palmergames.bukkit.towny.event.TownPreRenameEvent;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class RenameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "/rename <town/nation> <town or nation name>");
            return false;
        }
        if (!args[0].equalsIgnoreCase("town") && !args[0].equalsIgnoreCase("nation")) {
            commandSender.sendMessage(ChatColor.RED + "/rename <town/nation> <town or nation name>");
            return false;
        }
        if (args[0].equalsIgnoreCase("town")) {
            String townName = args[1];
            Town town = TownyAPI.getInstance().getTown(townName);
            if (town == null) {
                commandSender.sendMessage(ChatColor.RED + "Town not found");
                return false;
            }
            String id = UUID.randomUUID().toString().substring(0, 5);
            String newName = "Renamed-" + id;
            String board = "Town has been renamed for breaking a rule regarding Nation naming. Doing this again may lead to Town deletion or punishment. /rules";
            try {
                TownPreRenameEvent event = new TownPreRenameEvent(town, newName);
                Bukkit.getServer().getPluginManager().callEvent(event);
                TownyAPI.getInstance().getDataSource().renameTown(town, newName);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            town.setBoard(board);
            commandSender.sendMessage(ChatColor.GREEN + "Town renamed!");
            return true;
        } else if (args[0].equalsIgnoreCase("nation")) {
            String nationName = args[1];
            Nation nation = TownyAPI.getInstance().getNation(nationName);
            if (nation == null) {
                commandSender.sendMessage(ChatColor.RED + "Nation not found");
                return false;
            }
            String id = UUID.randomUUID().toString().substring(0, 5);
            String newName = "Renamed-" + id;
            String board = "Nation has been renamed for breaking a rule regarding Nation naming. Doing this again may lead to Nation deletion or punishment. /rules";
            nation.setBoard(board);
            try {
                NationPreRenameEvent event = new NationPreRenameEvent(nation, newName);
                Bukkit.getServer().getPluginManager().callEvent(event);
                TownyAPI.getInstance().getDataSource().renameNation(nation, newName);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            commandSender.sendMessage(ChatColor.GREEN + "Nation renamed!");
            return true;
        }
        return false;
    }

}
