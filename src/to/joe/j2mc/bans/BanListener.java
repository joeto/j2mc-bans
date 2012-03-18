package to.joe.j2mc.bans;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class BanListener implements Listener{

    J2MC_Bans plugin;
    
    public BanListener(J2MC_Bans Bans){
        this.plugin = Bans;
    }
    
    @EventHandler
    public void onIRCMessageEvent(MessageEvent event) {
        if(event.targetting("NEWADDBAN")){
            String[] var = event.getMessage().split(":");
            String admin = var[0];
            String target = var[1].replace("/OMGREPLACEWITHCOLON\\", ":");
            String reason = var[2].replace("/OMGREPLACEWITHCOLON\\", ":");
            String sender = var[3];
            ArrayList<String> prep = new ArrayList<String>();
            prep.add(target);
            prep.add(reason);
            String[] args = (String[]) prep.toArray(new String[0]);
            plugin.callAddBan(admin, args, null);
            HashSet<String> targets = new HashSet<String>();
            targets.add("SendNotice " + sender);
            plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, "Banned " + target));
        }
        
        if(event.targetting("NEWBAN")){
            String[] var = event.getMessage().split(":");
            String admin = var[0];
            String target = var[1].replace("/OMGREPLACEWITHCOLON\\", ":");
            String reason = var[2].replace("/OMGREPLACEWITHCOLON\\", ":");
            String sender = var[3];
            Player player = null;
            try{
                player = J2MC_Manager.getVisibility().getPlayer(target, null);
            }catch(BadPlayerMatchException e){
                HashSet<String> targets = new HashSet<String>();
                targets.add("SendNotice " + sender);
                plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, e.getMessage()));
                return;
            }
            ArrayList<String> prep = new ArrayList<String>();
            prep.add(player.getName());
            prep.add(reason);
            String[] args = (String[]) prep.toArray(new String[0]);
            plugin.callAddBan(admin, args, null);
            HashSet<String> targets = new HashSet<String>();
            targets.add("SendNotice " + sender);
            plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, "Banned " + player.getName()));
        }
        
        if(event.targetting("UNBAN")){
            String[] var = event.getMessage().split(":");
            String admin = var[0];
            String unbannee = var[1];
            String sender = var[2];
            plugin.unban(unbannee, admin);
            HashSet<String> targets = new HashSet<String>();
            targets.add("SendNotice " + sender);
            plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, "Unbanned " + unbannee));
        }
    }
    
}
