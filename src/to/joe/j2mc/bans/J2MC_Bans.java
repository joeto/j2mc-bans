package to.joe.j2mc.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.bans.command.BanCommand;
import to.joe.j2mc.bans.command.UnbanCommand;
import to.joe.j2mc.bans.command.KickCommand;
import to.joe.j2mc.bans.command.AddBanCommand;
import to.joe.j2mc.core.J2MC_Manager;

public class J2MC_Bans extends JavaPlugin{
	
	public BanFunctions methods = new BanFunctions();

	@Override
	public void onDisable() {
		J2MC_Manager.getLog().info("Bans module disabled");
	}

	@Override
	public void onEnable() {
		J2MC_Manager.getLog().info("Bans module enabled");
		
		this.getCommand("ban").setExecutor(new BanCommand(this));
		this.getCommand("addban").setExecutor(new AddBanCommand(this));
		this.getCommand("kick").setExecutor(new KickCommand(this));
		this.getCommand("unban").setExecutor(new UnbanCommand(this));
		
		methods.bans = new ArrayList<Ban>();
		J2MC_Manager.getLog().info("ArrayList initalized");
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_PRELOGIN, new JoinListener(), Priority.Normal, this);
	}
	
	class JoinListener extends PlayerListener{
			public void onPlayerPreLogin(PlayerPreLoginEvent event) {
				final String name = event.getName();
		        final Date curTime = new Date();
		        final long timeNow = curTime.getTime() / 1000;
				String reason = null;
		        for (final Ban ban : methods.bans) {
		            if (ban.isBanned() && ban.isTemp() && (ban.getTimeOfUnban() < timeNow)) {
		                // unban(user);
		                // tempbans
		            }
		            if ((ban.getTimeLoaded() > (timeNow - 60)) && ban.getName().equalsIgnoreCase(name) && ban.isBanned()) {
		                reason = "Banned: " + ban.getReason();
		            }
		            if (ban.getTimeLoaded() < (timeNow - 60)) {
		                methods.bans.remove(ban);
		            }
		        }
		        if (reason == null) {
		        	ResultSet rs = null;
		        	try{
		        		PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT name,reason,unbantime,timeofban FROM j2bans WHERE unbanned=0 and name= ?");
		        		ps.setString(1, name);
		        		rs = J2MC_Manager.getMySQL().executeQuery(ps);
		        		if (rs.next()) {
		        			reason = rs.getString("reason");
		        			reason = "Banned: " + reason;
		        		}
		        	}
		        	catch(final Exception e){
		        		reason = "Try again. Ban system didn't like you.";
		        	}
		        }
		        if (reason != null) {
		            if (!reason.equals("Try again. Ban system didn't like you.")) {
		                reason = "Visit http://www.joe.to/unban/ for unban";
		            }
		            event.setKickMessage(reason);
		            event.disallow(PlayerPreLoginEvent.Result.KICK_BANNED, reason);
		        }
			}
		}
}
