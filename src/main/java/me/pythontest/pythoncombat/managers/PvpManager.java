package me.pythontest.pythoncombat.managers;

import javafx.util.Pair;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;
import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.objects.PCPlayerGroups;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.util.BoundingBox;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

public class PvpManager {
    public static Map<String, Location> checkedLocation=null;

    public static boolean canDamage(PCPlayer damager, PCPlayer damaged){
        PCPlayerGroups PCdamager = new PCPlayerGroups(damager);
        PCPlayerGroups PCdamaged = new PCPlayerGroups(damaged);
            PCdamaged.findGroupMembers();
            PCdamager.findGroupMembers();

        if(damaged.getId().equals(damager.getId()))
            return true;
        if(PCdamager.isInDuel(damaged.getId()))
            return true;
        Long Timeout = PCdamager.getTimeout(damaged.getId());
        Date now = new Date();
        if(PCdamager.getTimeout(damaged.getId()) instanceof Long && Timeout>now.getTime())
            return true;
        if(PCdamager.getPvpStatus()&&PCdamaged.getPvpStatus())
            return true;
        return false;
    }
    public static boolean canDisconnect(PCPlayer player){
        Pair<Long,String> lasthit = player.getlastHit();
        if(lasthit!=null){
            Date now = new Date();
            if(lasthit.getKey()+PCPlayer.TimeoutTime*1000>now.getTime())
                return false;
        }
        return true;
    }
    public static boolean isDamagingEntity(Entity entity, Pythoncombat plugin){
        DamagerManager damagerManager = plugin.getDamagerManager();
        String eid = entity.getUniqueId().toString();
        if(entity.getType()==EntityType.PLAYER)
            return true;
        if(entity.getType()==EntityType.PRIMED_TNT&&((TNTPrimed) entity).getSource()instanceof Player)
            return true;
        if(entity instanceof Projectile&&((Projectile) entity).getShooter() instanceof Player)
            return true;
        if(damagerManager.isDelayedDamageEntity(entity.getType())&&damagerManager.getDelayedDamage(eid)!=null)
            return true;
        return false;
    }
    public static Player GetDamager(Entity entity, Pythoncombat plugin){
        Player damager=null;
        DamagerManager damagerManager = plugin.getDamagerManager();
        String eid = entity.getUniqueId().toString();
        if(entity instanceof Projectile&&((Projectile) entity).getShooter() instanceof Player){
            damager = (Player) ((Projectile) entity).getShooter();
            entity.remove();
        } else if (entity instanceof TNTPrimed&&((TNTPrimed) entity).getSource()instanceof Player) {
            damager = (Player) ((TNTPrimed) entity).getSource();
        } else if (damagerManager.isDelayedDamageEntity(entity.getType())) {
            damager = damagerManager.getDelayedDamage(eid);
        } else
            damager = (Player) entity;
        return damager;
    }
    public static boolean isDelayedDamageBlock(Block block){
        if(block.getType()==Material.RESPAWN_ANCHOR)
            return true;
        if(block.getBlockData() instanceof Bed){
            return block.getWorld().getEnvironment()!= World.Environment.NORMAL;
        }

        return false;
    }
    public static Collection<Entity> canDestroyBlock(Block block, Pythoncombat plugin){
        if(checkedLocation==null)
            checkedLocation=new HashMap<>();
        World world = block.getWorld();
        Location blockLocation = new Location(block.getLocation().getWorld(),block.getX(),block.getY(),block.getZ());
        Location blockAboveLocation = new Location(block.getLocation().getWorld(),block.getX(),block.getY(),block.getZ());
        blockAboveLocation.setY(blockLocation.getY()+1);
        Block blockabove = world.getBlockAt(blockAboveLocation);
        Collection<Entity> players=new ArrayList<>();
        if(!blockabove.getType().isAir()){
            if(blockabove.getType().hasGravity())
                return canDestroyBlock(blockabove,plugin);
        }
        else{
            BoundingBox container = new BoundingBox(blockAboveLocation.getX(),blockAboveLocation.getY(),blockAboveLocation.getZ(),blockAboveLocation.getX() + 1, blockAboveLocation.getY() + 1, blockAboveLocation.getZ() + 1);

            Collection<Entity> finded = world.getNearbyEntities(container, new Predicate<Entity>() {
                @Override
                public boolean test(Entity entity) {
                    if(entity.getType()==EntityType.PLAYER)
                        return true;
                    return false;
                }
            });
            for (Entity entity : finded) {
                players.add(entity);
            }
        }
        Location blockTesting;
        Collection<Entity> finded;
        for(int i=0;i<5;i++){
            blockTesting = new Location(block.getLocation().getWorld(),block.getX(),block.getY(),block.getZ());

            switch (i){
                case 0:
                    blockTesting.setX(blockTesting.getX()-1);
                    break;
                case 1:
                    blockTesting.setX(blockTesting.getX()+1);
                    break;
                case 2:
                    blockTesting.setZ(blockTesting.getZ()-1);
                    break;
                case 3:
                    blockTesting.setZ(blockTesting.getZ()+1);
                    break;
                case 4:
                    blockTesting.setY(blockTesting.getY()+1);
                    break;

            }
            Block testingBlock = world.getBlockAt(blockTesting);

            if(checkedLocation.getOrDefault(blockTesting.toString(),null)==null)
                checkedLocation.put(blockTesting.toString(),blockTesting);
            else
                continue;
            if(isChainDestroyBlock(testingBlock)) {

                finded = canDestroyBlock(testingBlock,plugin);
                for (Entity entity : finded) {
                    players.add(entity);
                }
            }
        }
        return players;
    }
    public static boolean isChainDestroyBlock(Block block){
        if(block.getBlockData() instanceof Rail)
            return true;
        if(block.getType().toString().toLowerCase().contains("torch"))
            return true;
        if(block.getType().toString().toLowerCase().contains("sign"))
            return true;
        return false;
    }

}
