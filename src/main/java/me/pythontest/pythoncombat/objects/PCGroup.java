package me.pythontest.pythoncombat.objects;

import me.pythontest.pythoncombat.managers.DatabaseManager;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PCGroup {
    List<String> members = new ArrayList<String>();
    List<String> regionMembers = new ArrayList<String>();
    private Pythoncombat plugin;
    private String gid;
    private Integer pvpAllow = null;
    private Boolean duelAllow = null;
    private Boolean timeoutPvpAllow = null;
    private DatabaseManager databaseManager;
    private String gname;
    private Integer priority;

    public PCGroup(Pythoncombat plugin,String gid,String name,Integer priority){
        this.plugin = plugin;
        this.gid = gid;
        this.databaseManager = plugin.getDatabaseManager();
        this.gname=name;
        this.priority = priority;
        this.loadMembers();
        try {
            this.readconfig();
        } catch (SQLException e) {
            databaseManager.throwError(e.getMessage());
        }

    }
    private void loadMembers(){
        try {
            members = databaseManager.getGroupMembers(gid,"user");
            regionMembers = databaseManager.getGroupMembers(gid,"region");
        } catch (SQLException e) {
            databaseManager.throwError(e.getMessage());
        }
    }
    private void readconfig() throws SQLException {
        String pvpConfig = databaseManager.getGroupConfig(this.gid,"pvp");
        if(pvpConfig==null)
            pvpAllow=null;
        else if(pvpConfig.equals("fallow"))
            pvpAllow=2;
        else if(pvpConfig.equals("allow"))
            pvpAllow=1;
        else if(pvpConfig.equals("deny"))
            pvpAllow=0;
        String duelConfig = databaseManager.getGroupConfig(this.gid,"duel");
        if(duelConfig!=null)
            duelAllow = duelConfig.equals("allow")?true:false;
        String timeoutConfig = databaseManager.getGroupConfig(this.gid,"timeout");
        if(timeoutConfig!=null)
            timeoutPvpAllow = timeoutConfig.equals("allow")?true:false;

    }
    public void addGroupMember(String id, String type){
        boolean successAdd = false;
        try {
            successAdd = plugin.getDatabaseManager().addGroupMember(this.gid,id,type);
        } catch (SQLException e) {
           databaseManager.throwError(e.getMessage());
        }
        if(successAdd){
            if(type.equals("user"))
                members.add(id);
            else if(type.equals("region")) {
                regionMembers.add(id);
                plugin.getStorageManager().onCreate();
            }
        }

    }
    public boolean isGroupMember(String id){
            for (String member : members) {
                if(member.equals(id))
                    return true;
            }
        for (String member : regionMembers) {
            if(member.equals(id))
                return true;
        }
        return false;
    }
    public void removeGroupMember(String id){
        try {
            databaseManager.removeGroupMemeber(this.gid,id);
            for (int i = 0; i < members.size(); i++) {
                members.remove(id);
            }
            for (int i = 0; i < regionMembers.size(); i++) {
                regionMembers.remove(id);
                plugin.getStorageManager().onCreate();
            }
        } catch (SQLException e) {
            databaseManager.throwError(e.getMessage());
        }
    }

    public Boolean getDuelAllow() {
        return duelAllow;
    }
    public Boolean getTimeoutPvpAllow(){
        return timeoutPvpAllow;
    }
    public Integer getPvpAllow(){
        return pvpAllow;
    }
    public Integer getPriority(){
        return priority;
    }

    public String getName() {
        return gname;
    }

    public String getId() {
        return gid;
    }
    public void setPvpAllow(String status) throws SQLException {
        if(status==null)
            pvpAllow=null;
        else if(status.equals("fallow"))
            pvpAllow=2;
        else if(status.equals("allow"))
            pvpAllow=1;
        else if(status.equals("deny"))
            pvpAllow=0;
        else
            return;
        if(status==null)
            plugin.getDatabaseManager().removeGroupConfig(this.gid,"pvp");
        else
            plugin.getDatabaseManager().updateGroupConfig(this.gid,"pvp",status);
    }
    public void setDuelAllow(Boolean status) throws SQLException {
        duelAllow = status;
        if(status==null)
            plugin.getDatabaseManager().removeGroupConfig(this.gid,"duel");
        else
            plugin.getDatabaseManager().updateGroupConfig(this.gid,"duel",status?"allow":"deny");
    }
    public void setTimeoutPvpAllow(Boolean status) throws SQLException {
        duelAllow = status;
        if(status==null)
            plugin.getDatabaseManager().removeGroupConfig(this.gid,"timeout");
        else
            plugin.getDatabaseManager().updateGroupConfig(this.gid,"timeout",status?"allow":"deny");
    }
    public static boolean isValidPvpStatus(String status){
        if(status.equals("fallow"))
            return true;
        if(status.equals("allow"))
            return true;
        if(status.equals("deny"))
            return true;
        if(status.equals("inherit"))
            return true;
        return false;
    }
    public static boolean isValidDuelStatus(String status){
        if(status.equals("allow"))
            return true;
        if(status.equals("deny"))
            return true;
        if(status.equals("inherit"))
            return true;
        return false;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getRegionMembers() {
        return regionMembers;
    }
}
