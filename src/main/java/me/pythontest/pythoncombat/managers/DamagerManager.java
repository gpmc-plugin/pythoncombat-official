package me.pythontest.pythoncombat.managers;

import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DamagerManager {
    private Pythoncombat plugin;
    private Map<String, Player> delayedDamage = new HashMap<String,Player>();
    public DamagerManager(Pythoncombat plugin){
        this.plugin=plugin;
    }
    public void registerDelayedDamage(String eid,Player damager){
        boolean exist = delayedDamage.containsKey(eid);
        if(exist)
            delayedDamage.replace(eid,damager);
        else
            delayedDamage.put(eid,damager);
    }
    public Player getDelayedDamage(String eid){
        return delayedDamage.getOrDefault(eid,null);
    }
    public Boolean isDelayedDamageEntity(EntityType entityType){
        if(entityType==EntityType.ENDER_CRYSTAL)
            return true;
        return false;
    }
    public void removeDelayedDamage(String eid){
        delayedDamage.remove(eid);
    }
}
