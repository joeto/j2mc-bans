package to.joe.j2mc.bans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.command.MasterCommand;

public class UnbanCommand extends MasterCommand {

	public UnbanCommand(J2MC_Bans Bans) {
		super(Bans);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, boolean isPlayer) {
		if (sender.hasPermission("j2mc.bans.unbanner")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /unban playername");
				return;
			}
			final String name = args[0];
			((J2MC_Bans) plugin).unban(name, sender.getName());
		}
	}
}
