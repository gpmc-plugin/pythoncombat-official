package me.pythontest.pythoncombat.Commands;

import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import me.pythontest.pythoncombat.statics.ChatMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public class PvpCommand implements CommandExecutor {
    Pythoncombat plugin;
    public PvpCommand(Pythoncombat plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length<1){
            sender.sendMessage(ChatMessages.LowArgs());
            return false;
        }
        if(sender instanceof Player){
            Player player = (Player) sender;
            PCPlayer pcPlayer = plugin.getStorageManager().getPlayer(player.getUniqueId().toString());
            if(args[0].equals("allow"))
                pcPlayer.setPvpStatus(true);
            else if(args[0].equals("deny"))
                pcPlayer.setPvpStatus(false);
            else
                return false;
            sender.sendMessage(ChatMessages.PvpUpdate(args[0]));
        }
        else
            sender.sendMessage(ChatMessages.NotPlayer());
        return true;
    }

}
