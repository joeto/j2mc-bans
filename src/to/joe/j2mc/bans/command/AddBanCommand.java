package to.joe.j2mc.bans.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.command.MasterCommand;

public class AddBanCommand extends MasterCommand {

    public AddBanCommand(J2MC_Bans Bans) {
        super(Bans);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /addban playername reason");
            sender.sendMessage(ChatColor.RED + " reason can have spaces in it");
            return;
        }
        Location loc;
        if (!isPlayer) {
            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        } else {
            loc = player.getLocation();
        }
        String target = args[0];
        String reason = J2MC_Core.combineSplit(1, args, " ");
        ((J2MC_Bans) this.plugin).ban(target, sender.getName(), reason, loc, false);
    }

}
