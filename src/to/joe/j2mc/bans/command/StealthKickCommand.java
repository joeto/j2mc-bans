package to.joe.j2mc.bans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class StealthKickCommand extends MasterCommand{

	public StealthKickCommand(J2MC_Bans Bans){
		super(Bans);
	}
	
	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, boolean isPlayer) {
		if (sender.hasPermission("j2mc.bans.kicker")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED
						+ "Usage: /kick playername reason");
				return;
			}
			Player target = null;
			try {
				target = J2MC_Manager.getVisibility().getPlayer(args[0], null);
			} catch (BadPlayerMatchException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return;
			}
			String reason = J2MC_Core.combineSplit(1, args, " ");
			if (reason != "") {
				target.kickPlayer("Kicked: " + reason);
				J2MC_Manager.getCore().adminAndLog(
						sender.getName() + " kicked " + target.getName() + "("
								+ reason + ")");
			} else {
				target.kickPlayer("Kicked.");
				J2MC_Manager.getCore().adminAndLog(
						sender.getName() + " kicked " + target.getName());
			}
		}
	}
}
