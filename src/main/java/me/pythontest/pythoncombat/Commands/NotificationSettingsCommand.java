package me.pythontest.pythoncombat.Commands;

import me.pythontest.pythoncombat.managers.NotificationManager;
import me.pythontest.pythoncombat.statics.ChatMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NotificationSettingsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length<2){
                sender.sendMessage(ChatMessages.LowArgs());
                return false;
            }
            boolean state;
            if(args[1].equals("allow"))
                state=true;
            else if(args[1].equals("deny"))
                state=false;
            else{
                sender.sendMessage(ChatMessages.Error());
                return false;
            }
            sender.sendMessage(Component.text("Zmieniono ustawienia powiadomień", NamedTextColor.GREEN));
            NotificationManager.setNotification(player,args[0],state);

        }
        else
            sender.sendMessage(ChatMessages.NotPlayer());
        return true;
    }

}
