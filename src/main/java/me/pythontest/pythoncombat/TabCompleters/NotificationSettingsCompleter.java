package me.pythontest.pythoncombat.TabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotificationSettingsCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments=new ArrayList<>();
        if(args.length==1){
            arguments.add("pvpinfo");
            arguments.add("cantfight");
        }
        else if(args.length==2){
            arguments.add("allow");
            arguments.add("deny");
        }
        return arguments;
    }
}
