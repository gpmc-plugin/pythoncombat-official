package me.pythontest.pythoncombat.managers;

import me.pythontest.pythoncombat.Commands.*;
import me.pythontest.pythoncombat.TabCompleters.CombatManagerCompleter;
import me.pythontest.pythoncombat.TabCompleters.DuelCompleter;
import me.pythontest.pythoncombat.TabCompleters.NotificationSettingsCompleter;
import me.pythontest.pythoncombat.TabCompleters.PvpCompleter;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static Pythoncombat plugin;
    public CommandManager(Pythoncombat plugin){
        this.plugin = plugin;
    }
    public static PvpCommand pvpCommand;
    public static PCinfoCommand pcinfoCommand;
    public static DuelCommand duelCommand;
    public static DuelCompleter duelCompleter;
    public static CombatManagerCommand combatManagerCommand;
    public static CombatManagerCompleter combatManagerCompleter;
    public static NotificationSettingsCommand notificationSettingsCommand;
    public void registercommands(){
         pcinfoCommand = new PCinfoCommand();
         pvpCommand = new PvpCommand(plugin);
         duelCommand = new DuelCommand(plugin);
         duelCompleter = new DuelCompleter(plugin);
         notificationSettingsCommand = new NotificationSettingsCommand();
         combatManagerCommand = new CombatManagerCommand(plugin);
         combatManagerCompleter = new CombatManagerCompleter(plugin);
        plugin.getCommand("pcinfo").setExecutor(pcinfoCommand);
        plugin.getCommand("pcinfo").setTabCompleter(new TabCompleter() {
            @Override
            public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
                return new ArrayList<>();
            }
        });
        plugin.getCommand("pvp").setExecutor(pvpCommand);
        plugin.getCommand("pvp").setTabCompleter(new PvpCompleter());
        plugin.getCommand("duel").setExecutor(duelCommand);
        plugin.getCommand("duel").setTabCompleter(duelCompleter);
        plugin.getCommand("combatmanager").setExecutor(combatManagerCommand);
        plugin.getCommand("combatmanager").setTabCompleter(combatManagerCompleter);
        plugin.getCommand("notificationsettings").setExecutor(notificationSettingsCommand);
        plugin.getCommand("notificationsettings").setTabCompleter(new NotificationSettingsCompleter());
    }
}
