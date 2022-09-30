package me.pythontest.pythoncombat.TabCompleters;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pythontest.pythoncombat.integrations.PCWorldguardIntegration;
import me.pythontest.pythoncombat.objects.PCGroup;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatManagerCompleter implements TabCompleter {
    private Pythoncombat plugin;
    public CombatManagerCompleter(Pythoncombat plugin){
        this.plugin=plugin;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<String>();
        switch (args.length) {
            case 1:
                arguments.add("groups");
                break;
            case 2:
                arguments.add("add");
                arguments.add("remove");
                arguments.add("manage");
                break;
            case 3:
                switch (args[1]) {
                    case "remove":
                    case "manage":
                        for (PCGroup group : plugin.getStorageManager().getGroups()) {
                            arguments.add(group.getName());
                        }
                        break;

                }
                break;
            case 4:
                if (args[1].equals("manage")) {
                    arguments.add("addmember");
                    arguments.add("removemember");
                    arguments.add("addregion");
                    arguments.add("removeregion");
                    arguments.add("pvp");
                    arguments.add("duel");
                    arguments.add("timeout");
                    break;
                }
                break;
            case 5:
                if (args[1].equals("manage")) {
                    PCGroup group = plugin.getStorageManager().getGroup(args[2]);
                    if (group != null && sender instanceof Player) {
                        Player playerSender = (Player) sender;
                        switch (args[3]) {
                            case "removemember":
                                for (String member : group.getMembers()) {
                                    Player playerMember = plugin.getServer().getPlayer(UUID.fromString(member));
                                    arguments.add(playerMember.getName());
                                }
                                break;
                            case "addmember":
                                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                    if (!group.isGroupMember(onlinePlayer.getUniqueId().toString()))
                                        arguments.add(onlinePlayer.getName());
                                }
                                break;
                            case "addregion": {
                                World world = playerSender.getWorld();
                                RegionManager rgm = PCWorldguardIntegration.GetWorldguard().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
                                for (Map.Entry<String, ProtectedRegion> entry : rgm.getRegions().entrySet()) {
                                    String groupRegionId = world.getUID().toString();
                                    String regionId = groupRegionId + ";" + entry.getValue().getId();
                                    if (!group.isGroupMember(regionId)) {
                                        arguments.add(entry.getValue().getId());
                                    }
                                }
                                break;
                            }
                            case "removeregion": {
                                World world = playerSender.getWorld();
                                for (String regionMember : group.getRegionMembers()) {
                                    String[] splitedGroup = regionMember.split(";");
                                    if (splitedGroup[0].equals(world.getUID().toString())) {
                                        arguments.add(splitedGroup[1]);
                                    }
                                }
                                break;
                            }
                            case "timeout":
                            case "duel":
                                arguments.add("allow");
                                arguments.add("deny");
                                arguments.add("inherit");
                                break;
                            case "pvp":
                                arguments.add("fallow");
                                arguments.add("allow");
                                arguments.add("deny");
                                arguments.add("inherit");
                                break;
                        }

                    }
                }
                break;
        }

        return arguments;
    }
}
