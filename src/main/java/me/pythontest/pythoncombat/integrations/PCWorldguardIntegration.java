package me.pythontest.pythoncombat.integrations;

import com.sk89q.worldguard.WorldGuard;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.plugin.Plugin;

public class PCWorldguardIntegration {
    public static boolean IsWorldGuardInstalled(Plugin plugin){
        return plugin.getServer().getPluginManager().getPlugin("worldguard")!=null;
    }
    public static WorldGuard GetWorldguard(){
        return WorldGuard.getInstance();
    }

}
