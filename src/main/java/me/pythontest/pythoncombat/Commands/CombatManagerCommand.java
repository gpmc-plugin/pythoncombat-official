package me.pythontest.pythoncombat.Commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pythontest.pythoncombat.integrations.PCWorldguardIntegration;
import me.pythontest.pythoncombat.objects.PCGroup;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import me.pythontest.pythoncombat.statics.ChatMessages;
import me.pythontest.pythoncombat.statics.CombatMenagerMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CombatManagerCommand implements CommandExecutor {
    private Pythoncombat plugin;

    public CombatManagerCommand(Pythoncombat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            if (args[0].equals("groups"))
                return this.GroupManage(sender, command, label, args);
        } else
            sender.sendMessage(ChatMessages.LowArgs());
        return false;
    }

    public boolean GroupManage(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            switch (args[1]) {
                case "add":
                    if (args.length >= 4) {
                        try {
                            if (!plugin.getStorageManager().addGroup(args[2], Integer.valueOf(args[3]))) {
                                sender.sendMessage(ChatMessages.Error());
                            } else
                                sender.sendMessage(CombatMenagerMessages.groupCreate(args[2]));
                        } catch (SQLException e) {
                            plugin.getDatabaseManager().throwError(e.getMessage());
                            sender.sendMessage(ChatMessages.Error());
                            return true;
                        }
                    } else
                        sender.sendMessage(ChatMessages.LowArgs());
                    break;
                case "remove":
                    if (args.length >= 3) {
                        PCGroup groupToDelete = plugin.getStorageManager().getGroup(args[2]);
                        if (groupToDelete != null) {
                            try {
                                plugin.getStorageManager().removeGroups(groupToDelete.getId());
                                sender.sendMessage(CombatMenagerMessages.groupDelete(args[2]));
                            } catch (SQLException e) {
                                plugin.getDatabaseManager().throwError(e.getMessage());
                                sender.sendMessage(ChatMessages.Error());
                            }

                        } else
                            sender.sendMessage(ChatMessages.Error());
                    } else
                        sender.sendMessage(ChatMessages.LowArgs());
                    break;
                case "manage":
                    return this.GroupManageCommand(sender, command, label, args);
            }


        return true;
    }

    public boolean GroupManageCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 5) {
            PCGroup group = plugin.getStorageManager().getGroup(args[2]);
            if (group != null) {
                switch (args[3]) {
                    case "pvp":
                        try {
                            if (PCGroup.isValidPvpStatus(args[4])) {
                                group.setPvpAllow(args[4].equals("inherit") ? null : args[4]);
                                sender.sendMessage(CombatMenagerMessages.groupConfigUpdated(args[3], args[4]));
                            } else
                                sender.sendMessage(ChatMessages.valueNotFound(args[4]));

                        } catch (SQLException e) {
                            sender.sendMessage(ChatMessages.Error());
                            plugin.getDatabaseManager().throwError(e.getMessage());
                        }
                        break;
                    case "duel":
                        try {
                            if (PCGroup.isValidDuelStatus(args[4])) {
                                Boolean status = args[4].equals("allow") ? true : false;
                                if (args[4].equals("inherit"))
                                    status = null;
                                group.setDuelAllow(status);
                                sender.sendMessage(CombatMenagerMessages.groupConfigUpdated(args[3], args[4]));
                            } else
                                sender.sendMessage(ChatMessages.valueNotFound(args[4]));

                        } catch (SQLException e) {
                            sender.sendMessage(ChatMessages.Error());
                            plugin.getDatabaseManager().throwError(e.getMessage());
                        }
                        break;
                    case "timeout":
                        try {
                            if (PCGroup.isValidDuelStatus(args[4])) {
                                Boolean status = args[4].equals("allow") ? true : false;
                                if (args[4].equals("inherit"))
                                    status = null;
                                group.setTimeoutPvpAllow(status);
                                sender.sendMessage(CombatMenagerMessages.groupConfigUpdated(args[3], args[4]));

                            } else
                                sender.sendMessage(ChatMessages.valueNotFound(args[4]));

                        } catch (SQLException e) {
                            sender.sendMessage(ChatMessages.Error());
                            plugin.getDatabaseManager().throwError(e.getMessage());
                        }
                        break;
                    case "addmember": {
                        Player player = plugin.getServer().getPlayer(args[4]);
                        if (player instanceof Player) {
                            group.addGroupMember(player.getUniqueId().toString(), "user");
                            sender.sendMessage(CombatMenagerMessages.groupUserAdd(args[4], group.getName()));
                        } else
                            sender.sendMessage(ChatMessages.UnknownUser(args[4]));
                        break;
                    }
                    case "removemember": {
                        Player player = plugin.getServer().getPlayer(args[4]);
                        if (player instanceof Player) {
                            group.removeGroupMember(player.getUniqueId().toString());
                            sender.sendMessage(CombatMenagerMessages.groupUserRemove(args[4], group.getName()));
                        } else
                            sender.sendMessage(ChatMessages.UnknownUser(args[4]));
                        break;
                    }
                    case "addregion": {
                        Player send = (Player) sender;
                        ProtectedRegion region = PCWorldguardIntegration.GetWorldguard().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(send.getWorld())).getRegion(args[4]);
                        if (region instanceof ProtectedRegion) {
                            group.addGroupMember(((Player) sender).getLocation().getWorld().getUID().toString() + ";" + region.getId(),"region");
                            sender.sendMessage(CombatMenagerMessages.groupRegionAdd(args[4], group.getName()));
                        } else
                            sender.sendMessage(ChatMessages.UnknownUser(args[4]));
                        break;
                    }
                    case "removeregion": {
                        Player send = (Player) sender;
                        ProtectedRegion region = PCWorldguardIntegration.GetWorldguard().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(send.getWorld())).getRegion(args[4]);
                        if (true) {
                            send.sendMessage(send.getLocation().getWorld().getUID().toString() + ";" + region.getId());
                            group.removeGroupMember(((Player) sender).getLocation().getWorld().getUID().toString() + ";" + region.getId());
                            sender.sendMessage(CombatMenagerMessages.groupRegionDelete(args[4], group.getName()));
                        } else
                            sender.sendMessage(ChatMessages.UnknownUser(args[4]));
                        break;
                    }
                }
            }
            else
                sender.sendMessage(CombatMenagerMessages.groupNotExist(args[2]));
        } else
            sender.sendMessage(ChatMessages.LowArgs());
        return true;
    }
}
