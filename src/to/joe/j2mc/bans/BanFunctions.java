package to.joe.j2mc.bans;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.LostSQLConnectionException;

public class BanFunctions {
	
    public ArrayList<Ban> bans;
    
	public void callAddBan(String adminName, String[] split, Location location) {
		//TODO: Co-op ban runners(mcbans/mcbouncer)
        String banReason = "";
        final long banTime = 0;
        // ^ Currently unused.
        banReason = this.combineSplit(1, split, " ");
        final String name = split[0];
        
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
        try {
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("INSERT INTO j2bans (name,reason,admin,unbantime,timeofban,x,y,z,pitch,yaw,world,server) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        	ps.setString(1, name);
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
        	J2MC_Manager.getCore().adminAndLog(ps.toString());
			J2MC_Manager.getMySQL().execute(ps);
            final Ban newban = new Ban(name.toLowerCase(), banReason, unBanTime, timeNow, timeNow, false);
            bans.add(newban);
		} catch (SQLException e) {
			J2MC_Manager.getLog().severe("Oh shit! SQL exception when adding a ban!", e);
		} catch (LostSQLConnectionException e) {
			J2MC_Manager.getLog().severe("Oh shit! Lost SQL connection when adding a ban!", e);
		} catch (ClassNotFoundException e) {
			J2MC_Manager.getLog().severe("Oh shit! Class not found when adding a ban!", e);
		}
        this.forceKick(name, "Banned: " + banReason);
        J2MC_Manager.getCore().adminAndLog(ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
    }

	public String combineSplit(int startIndex, String[] string, String seperator) {
        final StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
	}
	
	public void unban(String player){
        for (final Ban ban : bans) {
            if (ban.getName().equalsIgnoreCase(player)) {
                ban.unBan();
            }
        }
        try {
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE j2bans SET unbanned=1 WHERE name= ? ");
        	ps.setString(1, player);
        	J2MC_Manager.getMySQL().execute(ps);
        }catch (SQLException e) {
			J2MC_Manager.getLog().severe("Oh shit! SQL exception when unbanning!", e);
		} catch (LostSQLConnectionException e) {
			J2MC_Manager.getLog().severe("Oh shit! Lost SQL connection unbanning!", e);
		} catch (ClassNotFoundException e) {
			J2MC_Manager.getLog().severe("Oh shit! Class not found when unbanning!", e);
		}
	}
	
    public void forceKick(String name, String reason) {
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
