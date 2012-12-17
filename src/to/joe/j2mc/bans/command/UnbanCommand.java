package to.joe.j2mc.bans.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.bans.J2MC_Bans;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;

public class UnbanCommand extends MasterCommand<J2MC_Bans> {

    public UnbanCommand(J2MC_Bans Bans) {
        super(Bans);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban playername");
            return;
        }
        final String name = args[0];
        this.plugin.unban(name, sender.getName());
        ResultSet rs = null;
        String result = "";
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT IP FROM `alias` WHERE name=? order by Time desc limit 1");
            ps.setString(1, name);
            rs = ps.executeQuery();
        } catch (final SQLException e) {
            this.plugin.getLogger().warning(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
            this.plugin.getLogger().log(Level.SEVERE, "SQL Exception:", e);
        }
        try {
            if (rs.next()) {
                result = rs.getString("IP");
            }
        } catch (final SQLException ex) {
            this.plugin.getLogger().warning(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
            this.plugin.getLogger().log(Level.SEVERE, "SQL Exception:", ex);
        }
        if(!result.isEmpty()){
            if(plugin.getServer().getIPBans().contains(result)){
                sender.sendMessage(ChatColor.RED + "That user's ip is banned. If necessary use /unbanip <playername> to remove his ip ban");
            }
        }
    }
}
