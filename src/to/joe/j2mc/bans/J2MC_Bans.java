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

import to.joe.j2mc.bans.command.AddBanCommand;
import to.joe.j2mc.core.J2MC_Manager;

public class J2MC_Bans extends JavaPlugin{

	@Override
	public void onDisable() {
		J2MC_Manager.getLog().info("Bans module disabled");
	}

	@Override
	public void onEnable() {
		J2MC_Manager.getLog().info("Bans module enabled");
		
		this.getCommand("addban").setExecutor(new AddBanCommand(this));
		
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
		new PlayerListener(){
			public void onPlayerPreLogin(PlayerPreLoginEvent event) {
				final String name = event.getName();
		        final Date curTime = new Date();
		        final long timeNow = curTime.getTime() / 1000;
				String reason = null;
				BanFunctions methods = new BanFunctions();
		        final ArrayList<Ban> banhat = new ArrayList<Ban>(methods.bans);
		        for (final Ban ban : banhat) {
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
					ps.setString(0, name);
					rs = J2MC_Manager.getMySQL().executeQuery(ps);
	                while (rs.next()) {
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
		}, Priority.Normal, this);
	}

}
