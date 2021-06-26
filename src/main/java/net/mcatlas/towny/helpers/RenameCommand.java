package net.mcatlas.towny.helpers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
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
            town.setName("Renamed-" + id);
            town.setBoard("Town has been renamed for breaking a rule regarding Nation naming. Doing this again may lead to Town deletion or punishment. /rules");
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
            nation.setName("Renamed-" + id);
            nation.setBoard("Nation has been renamed for breaking a rule regarding Nation naming. Doing this again may lead to Nation deletion or punishment. /rules");
            commandSender.sendMessage(ChatColor.GREEN + "Nation renamed!");
            return true;
        }
        return false;
    }

}
