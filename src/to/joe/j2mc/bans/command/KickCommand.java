package to.joe.j2mc.bans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.BanFunctions;
import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class KickCommand extends MasterCommand{
	
	public KickCommand(J2MC_Bans Bans){
		super(Bans);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, boolean isPlayer) {
        if (!isPlayer || player.hasPermission("j2mc.bans.kicker")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kick playername reason");
                return;
            }
            BanFunctions methods = new BanFunctions();
            Player target = null;
            try{
            	target = J2MC_Manager.getVisibility().getPlayer(args[1], null);
            }catch(BadPlayerMatchException e){
            	player.sendMessage(ChatColor.RED + e.getMessage());
            	return;
            }
            String reason = methods.combineSplit(1, args, " ");
            if (reason != "") {
            	target.kickPlayer("Kicked: " + reason);
            	J2MC_Manager.getCore().adminAndLog(player.getName() + " kicked " + target.getName() + "(" + reason + ")");
            	J2MC_Manager.getCore().messageByPermission("receive.nomadin", ChatColor.RED + target.getName() + " kicked (" + reason + ")");
            	//TODO: IRC broadcast
            }
            else{
            	target.kickPlayer("Kicked.");
            	J2MC_Manager.getCore().adminAndLog(player.getName() + " kicked " + target.getName());
            	J2MC_Manager.getCore().messageByPermission("receive.nomadin", ChatColor.RED + target.getName() + " kicked");
            	//TODO: IRC broadcast
            }
        }
	}
	
}
