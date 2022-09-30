package me.pythontest.pythoncombat.events;

import me.pythontest.pythoncombat.managers.NotificationManager;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import me.pythontest.pythoncombat.statics.ActionBarMessages;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class InfoEventListener implements Listener {
    Map<String,Boolean> isSended = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerMoveEvent e){
        Entity entity = e.getPlayer().getTargetEntity(20,false);
        if(entity instanceof Player){
            Player targetedPlayer = (Player) entity;
            boolean pvpstatus = Pythoncombat.getInstance().getStorageManager().getPlayer(targetedPlayer.getUniqueId().toString()).getPvpStatus();
            if(NotificationManager.allowNotification(e.getPlayer(),"pvpinfo")){
                e.getPlayer().sendActionBar(ActionBarMessages.PvpStatus(targetedPlayer,pvpstatus));
                isSended.put(e.getPlayer().getUniqueId().toString(),true);
            }
        }
        else{
            if(isSended.getOrDefault(e.getPlayer().getUniqueId().toString(),false)){
                isSended.remove(e.getPlayer().getUniqueId().toString());
                e.getPlayer().sendActionBar(Component.empty());
            }
        }
    }

}
