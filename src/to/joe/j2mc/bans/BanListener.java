package to.joe.j2mc.bans;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import to.joe.j2mc.core.event.MessageEvent;

public class BanListener implements Listener{

    @EventHandler
    public void onIRCMessageEvent(MessageEvent event) {
        if(event.targetting("NEWADDBAN")){
            String admin;
            String target;
            String reason;
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
            
        }
        if(event.targetting("")){
            
        }
    }
    
}
