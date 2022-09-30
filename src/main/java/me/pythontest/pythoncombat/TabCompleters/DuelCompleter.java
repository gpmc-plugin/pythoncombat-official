package me.pythontest.pythoncombat.TabCompleters;

import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelCompleter implements TabCompleter {
    private  Pythoncombat plugin;
    public DuelCompleter(Pythoncombat plugin){
        this.plugin=plugin;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<String>();
        if (sender instanceof Player) {

            PCPlayer pcSender = plugin.getStorageManager().getPlayer(((Player) sender).getUniqueId().toString());
            if (args.length == 1) {
                arguments.add("invite");
                arguments.add("accept");
                arguments.add("deny");
                arguments.add("remove");
            }
            if (args.length == 2) {
                if (args[0].equals("remove")) {
                    for (String duel : pcSender.getDuels()) {
                        Player duelPlayer = plugin.getServer().getPlayer(UUID.fromString(duel));
                        if(duelPlayer!=null)
                            arguments.add(duelPlayer.getName());
                    }
                }
                if(args[0].equals("accept")||args[0].equals("deny"))
                    for (String duel : pcSender.getDuelsInvites()) {
                        Player duelPlayer = plugin.getServer().getPlayer(UUID.fromString(duel));
                        if(duelPlayer!=null)
                            arguments.add(duelPlayer.getName());
                    }
                if(args[0].equals("invite")){
                    for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                        String onlinePlayerId = onlinePlayer.getUniqueId().toString();
                        if(!pcSender.isInDuel(onlinePlayerId)&&!pcSender.existInvite(onlinePlayerId))
                            arguments.add(onlinePlayer.getName());
                    }
                }

            }

        }
        return arguments;
    }
}
