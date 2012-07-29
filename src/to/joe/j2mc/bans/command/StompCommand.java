package to.joe.j2mc.bans.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;

public class StompCommand extends MasterCommand {

    public StompCommand(J2MC_Bans Bans) {
        super(Bans);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /stomp <playername> <reason>");
            sender.sendMessage(ChatColor.RED + "       reason can have spaces in it");
            return;
        }
        String target = args[0];
        String reason = J2MC_Core.combineSplit(1, args, " ");
        Location loc;
        if (!isPlayer) {
            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        } else {
            loc = player.getLocation();
        }

        ResultSet rs = null;
        String result = null;
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT IP FROM `alias` WHERE name=? order by Time desc limit 1");
            ps.setString(1, args[0]);
            rs = ps.executeQuery();
        } catch (final SQLException e) {
            this.plugin.getLogger().warning(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
            this.plugin.getLogger().log(Level.SEVERE, "SQL Exception:", e);
        } catch (final ClassNotFoundException e) {
        }

        try {
            if (rs.next()) {
                result = rs.getString("IP");
            }
        } catch (final SQLException ex) {
            this.plugin.getLogger().warning(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
            this.plugin.getLogger().log(Level.SEVERE, "SQL Exception:", ex);
        }
        if (result == null || result.length() < 8) {
            sender.sendMessage(ChatColor.RED + "No IP matches on that username, could not stomp.");
            return;
        } else {
            plugin.getServer().banIP(result);
            ((J2MC_Bans) this.plugin).ban(target, sender.getName(), reason, loc, false);
        }

    }

}
