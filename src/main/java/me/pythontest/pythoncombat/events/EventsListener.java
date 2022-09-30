package me.pythontest.pythoncombat.events;

import me.pythontest.pythoncombat.managers.DamagerManager;
import me.pythontest.pythoncombat.managers.NotificationManager;
import me.pythontest.pythoncombat.managers.PvpManager;
import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import me.pythontest.pythoncombat.statics.ChatMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventsListener implements Listener {
    private Pythoncombat plugin;
    private double lastMagicDamage=0;
    private DamagerManager damagerManager;
    private Map<String,Location> blockedBlocks = new HashMap<>();
    public EventsListener(Pythoncombat plugin){
        this.plugin=plugin;
        this.damagerManager = plugin.getDamagerManager();
    }
    @EventHandler(priority = EventPriority.HIGH,ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getCause()== EntityDamageEvent.DamageCause.MAGIC)
            e.setDamage(lastMagicDamage);
        if(damagerManager.isDelayedDamageEntity(e.getEntityType())){
            String eid=e.getEntity().getUniqueId().toString();
            if(PvpManager.isDamagingEntity(e.getDamager(),plugin)){
                Player damager = PvpManager.GetDamager(e.getDamager(),plugin);
                damagerManager.registerDelayedDamage(eid,damager);
            }
            else
                damagerManager.registerDelayedDamage(eid,null);
            return;

        }
        if(PvpManager.isDamagingEntity(e.getDamager(),plugin)&&e.getEntity().getType()==EntityType.PLAYER){
            Player damager = PvpManager.GetDamager(e.getDamager(),plugin);

            Player damaged = (Player) e.getEntity();
            PCPlayer Pcdamager = plugin.getStorageManager().getPlayer(damager.getUniqueId().toString());
            PCPlayer Pcdamaged = plugin.getStorageManager().getPlayer(damaged.getUniqueId().toString());
            if(PvpManager.canDamage(Pcdamager,Pcdamaged)){
                e.setCancelled(false);
                if(!Pcdamaged.getId().equals(Pcdamager.getId())) {
                    Pcdamaged.registerHit(damager.getName(), Pcdamager.getId(), damaged);
                    Pcdamager.registerHit(damaged.getName(), Pcdamaged.getId(), damager);
                }
            }
            else{
                e.setCancelled(true);
                cantFightNotify(damager,damaged);

            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        PCPlayer pcPlayer = plugin.getStorageManager().getPlayer(player.getUniqueId().toString());
        if(!PvpManager.canDisconnect(pcPlayer)){
            String idOfDamager = pcPlayer.getlastHit().getValue();
            pcPlayer.setCombatKill(true);
            player.setHealth(0.0);
        }
        plugin.getStorageManager().removePlayer(player.getUniqueId().toString());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        plugin.getStorageManager().addPlayer(player.getUniqueId().toString());
    }
    /**
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e){
        Player player = e.getPlayer();
        PCPlayer pcPlayer = plugin.getStorageManager().getPlayer(player.getUniqueId().toString());
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if(!PvpManager.isTeleportationCorrect(cause))
            e.setCancelled(!PvpManager.canDisconnect(pcPlayer));
        if(e.isCancelled()){
            if(!player.isOp()) {
                pcPlayer.setCombatKill(true);
                player.setHealth(0.0);
            }
            player.sendMessage(ChatMessages.TeleportationError());
        }

    }
     **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDied(PlayerDeathEvent e){
        Player player = e.getPlayer();
        PCPlayer pcPlayer = plugin.getStorageManager().getPlayer(player.getUniqueId().toString());
        pcPlayer.removeAllTimeouts();
        if(pcPlayer.getCombatKill()){
            Player killer = plugin.getServer().getPlayer(UUID.fromString(pcPlayer.getlastHit().getValue()));
            e.deathMessage(ChatMessages.CombatKill(player.getName(),killer.getName()));
            pcPlayer.setCombatKill(false);
        }
    }
    @EventHandler(priority = EventPriority.LOW,ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            lastMagicDamage=e.getDamage();
            if(e.getCause()== EntityDamageEvent.DamageCause.MAGIC)
                e.setDamage(0.0);
        }
    }
    @EventHandler(priority=EventPriority.LOWEST,ignoreCancelled = true)
    public void onPlayerDamageByBlock(EntityDamageByBlockEvent e) {
        if (e.getEntity() instanceof Player && e.getCause()== EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            Player damaged = (Player) e.getEntity();
            Player damager = plugin.getDamagerManager().getDelayedDamage("blockExplosion");
            if (damager != null) {
                PCPlayer Pcdamager = plugin.getStorageManager().getPlayer(damager.getUniqueId().toString());
                PCPlayer Pcdamaged = plugin.getStorageManager().getPlayer(damaged.getUniqueId().toString());
                if (PvpManager.canDamage(Pcdamager, Pcdamaged)) {
                    e.setCancelled(false);
                    if (!Pcdamaged.getId().equals(Pcdamager.getId())) {
                        Pcdamaged.registerHit(damager.getName(), Pcdamager.getId(), damaged);
                        Pcdamager.registerHit(damaged.getName(), Pcdamaged.getId(), damager);
                    }
                } else {
                    e.setCancelled(true);
                    cantFightNotify(damager,damaged);
                }
            }

        }
    }
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction()== Action.RIGHT_CLICK_BLOCK&& PvpManager.isDelayedDamageBlock(e.getClickedBlock())){
            Player getDamager = plugin.getDamagerManager().getDelayedDamage("blockExplosion");
            if(getDamager!=null) {
                e.setCancelled(true);
                return;
            }
            plugin.getDamagerManager().registerDelayedDamage("blockExplosion",e.getPlayer());
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getDamagerManager().removeDelayedDamage("blockExplosion");
                }
            });

        }
    }
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent e){
        boolean errorcancel=false;
        try {
            Collection<Entity> players = PvpManager.canDestroyBlock(e.getBlock(), plugin);
            PvpManager.checkedLocation=null;
            for (Entity entity : players) {
                Player damager = e.getPlayer();
                Player damaged = (Player) entity;
                PCPlayer Pcdamager = plugin.getStorageManager().getPlayer(damager.getUniqueId().toString());
                PCPlayer Pcdamaged = plugin.getStorageManager().getPlayer(damaged.getUniqueId().toString());
                if(!PvpManager.canDamage(Pcdamager, Pcdamaged)){
                    e.setCancelled(true);
                }

            }
        } catch (StackOverflowError ex) {

            plugin.getServer().getConsoleSender().sendMessage("Nie bój się to nie błąd pluginu tylko celowe działanie gracza sprawdź kordynaty: "+e.getBlock().getLocation().toString());
           errorcancel=true;
            blockedBlocks.put(e.getBlock().getLocation().toString(),e.getBlock().getLocation());
        }
        if(blockedBlocks.containsKey(e.getBlock().getLocation().toString()))
            errorcancel=true;
        if(errorcancel){
            if(e.getPlayer().hasPermission("pythoncombat.combatmanager"))
                return;
            e.getPlayer().sendMessage(Component.text("Ze względu na problem z przetworzeniem twojego żądania powiadomiliśmy administracje.", NamedTextColor.RED));
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if(onlinePlayer.hasPermission("pythoncombat.combatmanager"))
                    onlinePlayer.sendMessage("[Pythoncombat] Gracz "+e.getPlayer().getName()+" próbował wykonąc podejrzaną operacje na kordynatach: "+e.getBlock().getLocation());
            }
            e.setCancelled(true);
        }
    }
    private void cantFightNotify(Player player,Player damaged){
        if(NotificationManager.allowNotification(player,"cantfight"))
            player.sendMessage(ChatMessages.CantFight(damaged));
    }

}
