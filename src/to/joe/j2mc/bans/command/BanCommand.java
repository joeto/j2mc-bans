package to.joe.j2mc.bans.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class BanCommand extends MasterCommand {

    public BanCommand(J2MC_Bans Bans) {
        super(Bans);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban playername reason");
            sender.sendMessage(ChatColor.RED + "       reason can have spaces in it");
            return;
        }
        Player target = null;
        try {
            target = J2MC_Manager.getVisibility().getPlayer(args[0], null);
        } catch (final BadPlayerMatchException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return;
        }
        Location loc;
        args[0] = target.getName();
        if (!isPlayer) {
            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        } else {
            loc = player.getLocation();
        }
        ((J2MC_Bans) this.plugin).callAddBan(sender.getName(), args, loc, true);
    }

}
