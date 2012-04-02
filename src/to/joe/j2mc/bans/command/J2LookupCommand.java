package to.joe.j2mc.bans.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;

public class J2LookupCommand extends MasterCommand {

    J2MC_Bans plugin;

    public J2LookupCommand(J2MC_Bans Bans) {
        super(Bans);
        this.plugin = Bans;
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "/j2lookup player [all]");
            return;
        }
        String target = args[0];
        plugin.getLogger().info(sender.getName() + " looked up " + target);
        boolean allbans = false;
        if ((args.length > 1) && args[1].equalsIgnoreCase("all")) {
            allbans = true;
        }
        try {
            PreparedStatement ps;
            if (allbans) {
                ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT name,reason,timeofban,unbantime,unbanned FROM j2bans WHERE name=?");
            } else {
                ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT name,reason,timeofban,unbantime,unbanned FROM j2bans WHERE name=? AND unbanned=0");
            }
            ps.setString(1, target);
            ResultSet rs = ps.executeQuery();
            final String g = ChatColor.GREEN.toString();
            boolean currentlyBanned = false;
            final ArrayList<String> messages = new ArrayList<String>();
            while (rs.next()) {
                String banned;
                if (rs.getBoolean("unbanned")) {
                    banned = "" + ChatColor.GREEN + "U";
                } else {
                    banned = "" + ChatColor.DARK_RED + "X";
                    currentlyBanned = true;
                }
                SimpleDateFormat shortdateformat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                String banTime = shortdateformat.format(new Date(rs.getLong("timeofban") * 1000));
                messages.add(g + "[" + banned + g + "] " + banTime + " " + ChatColor.GOLD + rs.getString("reason"));
            }
            String c = ChatColor.GREEN.toString();
            if (currentlyBanned) {
                c = ChatColor.RED.toString();
            }
            sender.sendMessage(ChatColor.AQUA + "Found " + ChatColor.GOLD + messages.size() + ChatColor.AQUA + " bans for " + c + target);
            for (String message : messages) {
                sender.sendMessage(message);
            }
        } catch (Exception e) {

        }
    }
}
