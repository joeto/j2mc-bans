package to.joe.j2mc.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.bans.command.AddBanCommand;
import to.joe.j2mc.bans.command.BanCommand;
import to.joe.j2mc.bans.command.J2LookupCommand;
import to.joe.j2mc.bans.command.KickCommand;
import to.joe.j2mc.bans.command.UnbanCommand;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;

public class J2MC_Bans extends JavaPlugin implements Listener {

    private ArrayList<Ban> bans;
    private final Object bansSync = new Object();
    private static final String joinError = ChatColor.AQUA + "Error. Rejoin in 10 seconds.";

    /**
     * 
     * @param user
     *            Name of the user being banned
     * @param admin
     *            Name of the admin performing the ban
     * @param reason
     *            Reason for the ban
     * @param location
     *            Location of ban, provide with null if there isn't a location (e.g console/irc)
     * @param announce
     *            Announce the ban in chat?
     */
    public void ban(String user, String admin, String reason, Location location, boolean announce) {
        double x = 0, y = 0, z = 0;
        float pitch = 0, yaw = 0;
        String world = "";
        if (location != null) {
            x = location.getX();
            y = location.getY();
            z = location.getZ();
            pitch = location.getPitch();
            yaw = location.getYaw();
            world = location.getWorld().getName();
        }
        final Date curTime = new Date();
        final long timeNow = curTime.getTime() / 1000;
        final long unBanTime = 0; // If we ever add tempban handling /o\
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("INSERT INTO j2bans (name,reason,admin,unbantime,timeofban,x,y,z,pitch,yaw,world,server) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, user);
            ps.setString(2, reason);
            ps.setString(3, admin);
            ps.setLong(4, unBanTime);
            ps.setLong(5, timeNow);
            ps.setDouble(6, x);
            ps.setDouble(7, y);
            ps.setDouble(8, z);
            ps.setFloat(9, pitch);
            ps.setFloat(10, yaw);
            ps.setString(11, world);
            ps.setInt(12, J2MC_Manager.getServerID());
            ps.execute();
            final Ban newban = new Ban(user, reason, unBanTime, timeNow, timeNow, false);
            synchronized (this.bansSync) {
                this.bans.add(newban);
            }
        } catch (final SQLException e) {
            this.getLogger().log(Level.SEVERE, "Oh shit! SQL exception when adding a ban!", e);
        }
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if ((p != null) && p.getName().equalsIgnoreCase(user)) {
                p.kickPlayer("Banned: " + reason);
                if (announce) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    this.getServer().getPluginManager().callEvent(new MessageEvent(MessageEvent.compile("GAMEMSG"), p.getName() + " banned (" + reason + ")"));
                }
                J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Knocked " + user + " out of the server");
                break;
            }
        }
        if (announce) {
            J2MC_Manager.getCore().messageByNoPermission(ChatColor.RED + user + " banned (" + reason + ")", "j2mc.core.admin");
        }
        J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Banning " + user + " by " + admin + ": " + reason);
    }

    /**
     * Get user's ban reason or null if not banned
     * 
     * @param player
     * @return
     */
    public String getBanReason(String player) {
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT reason WHERE unbanned=0 and name= ?");
            ps.setString(1, player);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("reason");
            } else {
                return null;
            }
        } catch (final SQLException e) {

        }
        return null;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Bans module disabled");
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Bans module enabled");

        this.getCommand("ban").setExecutor(new BanCommand(this));
        this.getCommand("addban").setExecutor(new AddBanCommand(this));
        this.getCommand("kick").setExecutor(new KickCommand(this));
        this.getCommand("unban").setExecutor(new UnbanCommand(this));
        this.getCommand("j2lookup").setExecutor(new J2LookupCommand(this));

        this.bans = new ArrayList<Ban>();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new BanListener(this), this);

        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new HeartbeatTask(this), 100, 100);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        final String name = event.getName();
        final Date curTime = new Date();
        final long timeNow = curTime.getTime() / 1000;
        String reason = null;
        synchronized (this.bansSync) {
            Iterator<Ban> i = this.bans.iterator();
            while(i.hasNext()) {
                Ban ban = i.next();
                /*if (ban.isBanned() && ban.isTemp() && (ban.getTimeOfUnban() < timeNow)) {
                    // unban(user);
                    // tempbans
                }*/
                if ((ban.getTimeLoaded() > (timeNow - 60)) && ban.getName().equalsIgnoreCase(name) && ban.isBanned()) {
                    reason = "Banned: " + ban.getReason();
                }
                if (ban.getTimeLoaded() < (timeNow - 60)) {
                    i.remove();
                }
            }
        }
        if (reason == null) {
            ResultSet rs = null;
            try {
                final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT name,reason,unbantime,timeofban FROM j2bans WHERE unbanned=0 and name= ?");
                ps.setString(1, name);
                rs = ps.executeQuery();
                if (rs.next()) {
                    reason = rs.getString("reason");
                    reason = "Banned: " + reason;
                }
            } catch (final Exception e) {
                e.printStackTrace();
                reason = J2MC_Bans.joinError;
            }
        }
        if (reason != null) {
            if (!reason.equals(J2MC_Bans.joinError)) {
                reason = "Visit http://www.joe.to/unban/ for unban";
            }
            event.setKickMessage(reason);
            event.disallow(Result.KICK_BANNED, reason);
        }
    }

    /**
     * Unban a player
     * 
     * @param player
     *            to be unbanned
     * @param Name
     *            of admin who is unbanning
     */
    public void unban(String player, String AdminName) {
        synchronized (this.bansSync) {
            for (final Ban ban : this.bans) {
                if (ban.getName().equalsIgnoreCase(player)) {
                    ban.unBan();
                }
            }
        }
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE j2bans SET unbanned=1 WHERE name= ? ");
            ps.setString(1, player);
            ps.execute();
        } catch (final SQLException e) {
            this.getLogger().log(Level.SEVERE, "Oh shit! SQL exception when unbanning!", e);
        }
        J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Unbanning " + player + " by " + AdminName);
    }
}
