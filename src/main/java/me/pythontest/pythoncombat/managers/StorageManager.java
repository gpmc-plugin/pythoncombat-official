package me.pythontest.pythoncombat.managers;

import com.sk89q.worldguard.WorldGuard;
import javafx.util.Pair;
import me.pythontest.pythoncombat.integrations.PCWorldguardIntegration;
import me.pythontest.pythoncombat.objects.PCGroup;
import me.pythontest.pythoncombat.objects.PCPlayer;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private Pythoncombat plugin;
    private DatabaseManager databaseManager;
    public StorageManager(Pythoncombat plugin){
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.onCreate();
    }
    private List<PCPlayer> PlayerList = new ArrayList<PCPlayer>();
    private List<PCGroup> GroupsList = new ArrayList<PCGroup>();
    private List<Integer> GroupsPriorityList = new ArrayList<Integer>();
    private List<Pair<String,Integer>> groupsRegionMembers = new ArrayList<Pair<String,Integer>>();
    public void addPlayer(String id){
        PlayerList.add(new PCPlayer(id,plugin));
    }
    public void removePlayer(String id){
        for (int i = 0; i < PlayerList.size(); i++) {
            if(PlayerList.get(i).getId().equals(id))
                PlayerList.remove(i);
        }
    }
    public PCPlayer getPlayer(String id){
        for (PCPlayer pcPlayer : PlayerList) {
            if(pcPlayer.getId().equals(id))
                return pcPlayer;
        }
        return null;
    }
    public void onCreate() {
        try {
            GroupsList = databaseManager.getGroups();
            for (int i = 0; i < GroupsList.size(); i++) {
                GroupsPriorityList.add(GroupsList.get(i).getPriority());
            }
            String sql = "Select mid from groups_members where mtype='region' order by mid ASC";
            Connection conn = databaseManager.getConn();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            groupsRegionMembers=new ArrayList<>();
            String lastId = null;
            Integer timeOccured = 1;
            while (rs.next()){
                if(lastId==null)
                    lastId=rs.getString("mid");
                else{
                    String id = rs.getString("mid");
                    if(lastId.equals(id))
                        timeOccured++;
                    else{
                        groupsRegionMembers.add(new Pair<>(lastId,timeOccured));
                        lastId=id;
                        timeOccured=1;

                    }
                }

            }
            if(lastId!=null)
                groupsRegionMembers.add(new Pair<>(lastId,timeOccured));

        } catch (SQLException e) {
            databaseManager.throwError(e.getMessage());
        }

    }

    public List<Pair<String, Integer>> getGroupsRegionMembers() {
        return groupsRegionMembers;
    }
    public PCGroup getGroup(String name){
        for (PCGroup pcGroup : GroupsList) {
            if(pcGroup.getName().equals(name))
                return pcGroup;
        }
        return null;
    }
    public Boolean addGroup(String name, Integer priority) throws SQLException {
        if(getGroup(name)==null){
            databaseManager.addGroup(name,priority);
            this.onCreate();
            return true;
        }
        return false;
    }
    public void removeGroups(String gid) throws SQLException {
        databaseManager.removeGroups(gid);
        this.onCreate();
    }

    public List<PCGroup> getGroups() {
        return GroupsList;
    }
}
