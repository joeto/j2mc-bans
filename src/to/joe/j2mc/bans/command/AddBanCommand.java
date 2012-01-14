package to.joe.j2mc.bans.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.BanFunctions;
import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.command.MasterCommand;

public class AddBanCommand extends MasterCommand{

	public AddBanCommand(J2MC_Bans Bans) {
		super(Bans);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, boolean isPlayer) {
		if(!isPlayer || player.hasPermission("j2mc.bans.banner")){
			BanFunctions methods = new BanFunctions();
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
            methods.callAddBan(player.getName(), args, loc);
		}
	}

}
