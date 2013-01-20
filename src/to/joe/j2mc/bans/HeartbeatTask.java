package to.joe.j2mc.bans;

import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;

public class HeartbeatTask implements Runnable {

    J2MC_Bans plugin;

    public HeartbeatTask(J2MC_Bans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (final Player player : J2MC_Manager.getPermissions().getPlayerCache()) {
            final String reason = this.plugin.getBanReason(player.getName());
            if (reason != null) {
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.kickPlayer(String.format(J2MC_Bans.banMessage, "have been", reason));
                    }
                });
            }
        }
    }

}
