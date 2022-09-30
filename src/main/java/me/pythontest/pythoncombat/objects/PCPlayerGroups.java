package me.pythontest.pythoncombat.objects;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javafx.util.Pair;
import me.pythontest.pythoncombat.integrations.PCWorldguardIntegration;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class  PCPlayerGroups {
    private List<PCGroup> member = new ArrayList<PCGroup>();
    private List<String> regionIn = new ArrayList<String>();
    private Player player;
    private PCPlayer pcPlayer;
    private Pythoncombat plugin;
    public PCPlayerGroups(PCPlayer pcPlayer) {
        this.pcPlayer=pcPlayer;
        this.plugin = pcPlayer.getPlugin();
        this.player = plugin.getServer().getPlayer(UUID.fromString(pcPlayer.getId()));
    }
    public void findGroupMembers() {
        if(PCWorldguardIntegration.IsWorldGuardInstalled(this.plugin)){
            String wid = player.getLocation().getWorld().getUID().toString();
            RegionManager regionManager =  PCWorldguardIntegration.GetWorldguard().getPlatform().getRegionContainer().get(BukkitAdapter.adapt( player.getWorld()));
            List<Pair<String,Integer>> intrestingRegions = plugin.getStorageManager().getGroupsRegionMembers();
            for (int i = 0; i < intrestingRegions.size(); i++) {
                Pair<String,Integer> iRegion= intrestingRegions.get(i);
                String[] regionInfoSplited = iRegion.getKey().split(";");
                if(iRegion.getKey().split(";")[0].equals(wid)){
                    assert regionManager != null;
                    ProtectedRegion region = regionManager.getRegion(regionInfoSplited[1]);
                    Location playerLocation = player.getLocation();
                    if(Objects.requireNonNull(region).contains( playerLocation.getBlockX(),playerLocation.getBlockY(),playerLocation.getBlockZ())){
                        regionIn.add(iRegion.getKey());
                    }
                }
            }
        }

            List<PCGroup> groups = plugin.getStorageManager().getGroups();
            for (int i = 0; i < groups.size(); i++) {
                if(groups.get(i).isGroupMember(pcPlayer.getId()))
                    member.add(groups.get(i));
                for (String s : regionIn) {
                    if(groups.get(i).isGroupMember(s)){
                        member.add(groups.get(i));
                    }
                }
            }

    }
    public boolean getPvpStatus() {

        boolean normalresponse = pcPlayer.getPvpStatus();
        for (int i = 0; i < this.member.size(); i++) {
            PCGroup group = member.get(i);
            Integer pvpStatus = group.getPvpAllow();
            if (pvpStatus != null) {
                if (pvpStatus == 0)
                    return false;
                else if (pvpStatus == 1)
                    return normalresponse;
                else if (pvpStatus == 2)
                    return true;
            }

        }
        return normalresponse;
    }
    public boolean isInDuel(String id) {
        boolean normalresponse = pcPlayer.isInDuel(id);
        for (int i = 0; i < this.member.size(); i++) {
            PCGroup group = member.get(i);
            Boolean duelStatus = group.getDuelAllow();

            if (duelStatus != null) {
                if(!duelStatus)
                    return false;
                if(duelStatus)
                    break;
            }

        }
        return normalresponse;
    }
    public Long getTimeout(String id) {
        Long normalresponse =  pcPlayer.getTimeout(id);
        for (int i = 0; i < this.member.size(); i++) {
            PCGroup group = member.get(i);
            Boolean timeoutStatus = group.getTimeoutPvpAllow();
            if (timeoutStatus != null) {
                if(!timeoutStatus)
                    return null;
                if(timeoutStatus)
                    break;
            }

        }
        return normalresponse;
    }
}
