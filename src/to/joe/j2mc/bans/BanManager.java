package to.joe.j2mc.bans;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;

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
	 * @param Location 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws LostSQLConnectionException 
	 */
	public static void AddBan(String adminName, String WhoToBan, String banReason,
			Location location) throws SQLException, ClassNotFoundException, LostSQLConnectionException {
		BanFunctions methods = new BanFunctions();
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
        	ps.setString(0, WhoToBan);
        	ps.setString(1, banReason);
        	ps.setString(2, adminName);
        	ps.setLong(3, unBanTime);
        	ps.setLong(4, timeNow);
        	ps.setDouble(5, x);
        	ps.setDouble(6, y);
        	ps.setDouble(7, z);
        	ps.setFloat(8, pitch);
        	ps.setFloat(9, yaw);
        	ps.setString(10, world);
        	ps.setInt(11, J2MC_Manager.getServerID());
        	J2MC_Manager.getMySQL().executeQuery(ps);
		methods.forceKick(WhoToBan, "Banned: " + banReason);
		J2MC_Manager.getCore().adminAndLog(
				ChatColor.RED + "Banning " + WhoToBan + " by " + adminName + ": "
						+ banReason);
	}
	
	public static void unban(String player) throws SQLException, ClassNotFoundException, LostSQLConnectionException{
		BanFunctions methods = new BanFunctions();
        for (final Ban ban : methods.bans) {
            if (ban.getName().equalsIgnoreCase(player)) {
                ban.unBan();
            }
        }
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE j2bans SET unbanned=1 WHERE name= ? ");
        	ps.setString(0, player);
        	J2MC_Manager.getMySQL().execute(ps);
	}

}
