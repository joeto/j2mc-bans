package to.joe.j2mc.bans;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import to.joe.j2mc.core.event.MessageEvent;

public class BanListener implements Listener{

    J2MC_Bans plugin;
    
    public BanListener(J2MC_Bans Bans){
        this.plugin = Bans;
    }
    
    @EventHandler
    public void onIRCMessageEvent(MessageEvent event) {
        if(event.targetting("NEWADDBAN")){
            String admin = null;
            String target = null;
            String reason = null;
            String[] var = event.getMessage().split("&");
            for(String variable : var){
                String[] lolrunningoutofnames = variable.split("=");
                String key = lolrunningoutofnames[0];
                String value = lolrunningoutofnames[1];
                if(key.equals("admin")){
                    admin = value;
                }
                if(key.equals("target")){
                    target = value.replace("*OMGROFLREPLACEMEIWITHAMPERSAND*", "&").replace("*OMGROFLREPLACEMEWITHEQUALS", "=");
                }
                if(key.equals("reason")){
                    reason = value.replace("*OMGROFLREPLACEMEIWITHAMPERSAND*", "&").replace("*OMGROFLREPLACEMEWITHEQUALS", "=");
                }
            }
            String[] args = new String[30];
            args[0] = target;
            String[] reasonarray = reason.split(" ");
            for(String word : reasonarray){
                args[args.length] = word;
            }
            plugin.callAddBan(admin, args, null);
        }
        if(event.targetting("")){
            
        }
    }
    
}
