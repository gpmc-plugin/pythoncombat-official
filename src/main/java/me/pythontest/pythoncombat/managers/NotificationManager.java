package me.pythontest.pythoncombat.managers;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NotificationManager {
    public static boolean allowNotification(Player player, String notificationID){
        NamespacedKey notificationSettings = new NamespacedKey("pythoncombatnotification",notificationID);
        boolean notify = player.getPersistentDataContainer().getOrDefault(notificationSettings, PersistentDataType.INTEGER,1)!=0;
        return notify;
    }
    public static void setNotification(Player player, String notificationID,boolean state){
        NamespacedKey notificationSettings = new NamespacedKey("pythoncombatnotification",notificationID);
        player.getPersistentDataContainer().set(notificationSettings,PersistentDataType.INTEGER,state?1:0);
    }
}
