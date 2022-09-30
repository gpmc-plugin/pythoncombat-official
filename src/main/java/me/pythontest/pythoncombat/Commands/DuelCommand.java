package me.pythontest.pythoncombat.Commands;

import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import me.pythontest.pythoncombat.statics.ChatMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DuelCommand implements CommandExecutor {
    private Pythoncombat plugin;
    public DuelCommand(Pythoncombat plugin){
        this.plugin=plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            if(args.length<2){
                sender.sendMessage(ChatMessages.LowArgs());
                return false;
            }
            Player mentioned = plugin.getServer().getPlayer(args[1]);
            PCPlayer pcSender = plugin.getStorageManager().getPlayer(((Player) sender).getUniqueId().toString());
            PCPlayer pcMentioned=null;
            if(mentioned!=null) {
                pcMentioned = plugin.getStorageManager().getPlayer(mentioned.getUniqueId().toString());
                mentioned=mentioned.isOnline()?mentioned:null;
            }

            if(mentioned==null&&!args[1].equals("-a")&&!args[0].equals("remove")&&!args[1].equals(sender.getName())){
                sender.sendMessage(ChatMessages.UnknownUser(args[1]));
                return true;
            }
            if(args[0].equals("invite")){
                if(pcSender.createInvite(pcMentioned.getId())){
                    pcMentioned.createInvite(pcSender.getId());
                    mentioned.sendMessage(ChatMessages.DuelInviteCreated(sender.getName()));
                    sender.sendMessage(ChatMessages.DuelInviteSended(mentioned.getName()));
                }
                else
                    sender.sendMessage(ChatMessages.DuelInviteFailed());
            }
            if(args[0].equals("accept")){
                if(pcSender.existInvite(pcMentioned.getId())){
                    pcSender.removeInvite(pcMentioned.getId());
                    pcSender.addDuel(pcMentioned.getId());
                    pcMentioned.removeInvite(pcSender.getId());
                    pcMentioned.addDuel(pcSender.getId());
                    sender.sendMessage(ChatMessages.DuelInviteAccepted(mentioned.getName(), false));
                    mentioned.sendMessage(ChatMessages.DuelInviteAccepted(sender.getName(),true));


                }
                else
                    sender.sendMessage(ChatMessages.DuelInviteNotExist(mentioned.getName()));
            }
            if(args[0].equals("deny")){
                if(pcSender.existInvite(pcMentioned.getId())){
                    pcSender.removeInvite(pcMentioned.getId());
                    pcMentioned.removeInvite(pcSender.getId());
                    sender.sendMessage(ChatMessages.DuelInviteDeclined(mentioned.getName()));


                }
                else
                    sender.sendMessage(ChatMessages.DuelInviteNotExist(mentioned.getName()));
            }
            if(args[0].equals("remove")){
                if(args[1].equals("-a")){
                    pcSender.removeAllDuels();
                    return true;
                }
                if(pcSender.isInDuel(pcMentioned.getId())){
                    pcSender.removeDuel(pcMentioned.getId());
                    pcMentioned.removeDuel(pcSender.getId());
                    sender.sendMessage(ChatMessages.DuelRemove(mentioned.getName()));
                    mentioned.sendMessage(ChatMessages.DuelRemove(sender.getName()));
                }
                else
                    sender.sendMessage(ChatMessages.DuelNotExist(mentioned.getName()));
            }
        }else
            sender.sendMessage(ChatMessages.NotPlayer());
        return true;
    }
}
