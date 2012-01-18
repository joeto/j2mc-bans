package to.joe.j2mc.bans;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.LostSQLConnectionException;

public class BanManager {
	
	/**
	 * Adds a ban for the user
	 * 
	 * @param adminName 
	 * 		Name of banning admin
	 * @param WhoToBan 
	 * 		String containing who to ban
	 * @param banReason 
	 * 		String containing ban reason
	 * @param location 
	 * 		Location of banning admin (set to 0,0,0,0,0 in cases where cordinates aren't applicable)
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws LostSQLConnectionException 
	 */
	public static void AddBan(String adminName, String WhoToBan, String banReason,
			Location location) throws SQLException, ClassNotFoundException, LostSQLConnectionException {
		// TODO: Co-op ban runners(mcbans/mcbouncer)
		final long banTime = 0;
		// ^ Currently unused.
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
		long unBanTime;
		if (banTime == 0) {
			unBanTime = 0;
		} else {
			unBanTime = timeNow + (60 * banTime);
		}
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("INSERT INTO j2bans (name,reason,admin,unbantime,timeofban,x,y,z,pitch,yaw,world,server) VALUES(?,?,?,?,?,?,?,?,?,?,?,?");
        	ps.setString(1, WhoToBan);
        	ps.setString(2, banReason);
        	ps.setString(3, adminName);
        	ps.setLong(4, unBanTime);
        	ps.setLong(5, timeNow);
        	ps.setDouble(6, x);
        	ps.setDouble(7, y);
        	ps.setDouble(8, z);
        	ps.setFloat(9, pitch);
        	ps.setFloat(10, yaw);
        	ps.setString(11, world);
        	ps.setInt(12, J2MC_Manager.getServerID());
        	J2MC_Manager.getMySQL().executeQuery(ps);
		forceKick(WhoToBan, "Banned: " + banReason);
		J2MC_Manager.getCore().adminAndLog(
				ChatColor.RED + "Banning " + WhoToBan + " by " + adminName + ": "
						+ banReason);
	}
	
	public static void unban(String player, String AdminName) throws SQLException, ClassNotFoundException, LostSQLConnectionException{
		BanFunctions methods = new BanFunctions();
        for (final Ban ban : methods.bans) {
            if (ban.getName().equalsIgnoreCase(player)) {
                ban.unBan();
            }
        }
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE j2bans SET unbanned=1 WHERE name= ? ");
        	ps.setString(0, player);
        	J2MC_Manager.getMySQL().execute(ps);
        	J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Unbanning " + player + " by " + AdminName);
	}

    public static void forceKick(String name, String reason) {
        //boolean msged = false;
    	J2MC_Manager.getCore().messageByPermission("receive.nomadin", ChatColor.RED + name + " banned (" + reason + ")");
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if ((p != null) && p.getName().equalsIgnoreCase(name)) {
                p.getWorld().strikeLightningEffect(p.getLocation());
                p.kickPlayer(reason);
                /*
                if (!msged) {
                    if (reason != "") {
                        this.j2.irc.messageRelay(name + " kicked");
                    } else {
                        this.j2.irc.messageRelay(name + " kicked (" + reason + ")");
                    }
                */
                    J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Knocked " + name + " out of the server");
                /*
                    msged = !msged;
                */
                }
            }
    }
	
}
