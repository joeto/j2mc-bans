package to.joe.j2mc.bans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class KickCommand extends MasterCommand {

    public KickCommand(J2MC_Bans Bans) {
        super(Bans);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /kick playername reason");
            return;
        }
        Player target = null;
        try {
            target = J2MC_Manager.getVisibility().getPlayer(args[0], null);
        } catch (final BadPlayerMatchException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return;
        }
        final String reason = J2MC_Core.combineSplit(1, args, " ");
        final String playerMsg;
        final String adminMsg;
        final String publicMsg;
        if (reason != "") {
            playerMsg = "Kicked: " + reason;
            adminMsg = ChatColor.RED + sender.getName() + " kicked " + target.getName() + "(" + reason + ")";
            publicMsg = ChatColor.RED + target.getName() + " kicked (" + reason + ")";
        } else {
            playerMsg = "Kicked.";
            adminMsg = ChatColor.RED + sender.getName() + " kicked " + target.getName();
            publicMsg = ChatColor.RED + target.getName() + " kicked";
        }
        target.kickPlayer(playerMsg);
        J2MC_Manager.getCore().adminAndLog(adminMsg);
        J2MC_Manager.getCore().messageNonAdmin(publicMsg);
        this.plugin.getServer().getPluginManager().callEvent(new MessageEvent(MessageEvent.compile("GAMEMSG"), ChatColor.stripColor(publicMsg)));
    }

}
