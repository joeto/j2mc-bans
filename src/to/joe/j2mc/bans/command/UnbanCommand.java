package to.joe.j2mc.bans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.BanFunctions;
import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.command.MasterCommand;

public class UnbanCommand extends MasterCommand{

	public UnbanCommand(J2MC_Bans Bans){
		super(Bans);
	}
	
	BanFunctions methods = new BanFunctions();
	
	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, boolean isPlayer) {
		if(!isPlayer || player.hasPermission("j2mc.bans.unbanner")){
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /unban playername");
                return;
            }
            final String name = args[0];
            methods.unban(name);
		}
	}
}
