package me.pythontest.pythoncombat.managers;

import me.pythontest.pythoncombat.objects.PCGroup;
import me.pythontest.pythoncombat.pythoncombat.Pythoncombat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    Connection conn;
    Pythoncombat plugin;
    public DatabaseManager(Pythoncombat plugin){
        this.plugin=plugin;
    }
    public void throwError(String error){
        plugin.getServer().getConsoleSender().sendMessage(error);
    }
    public void connect() {
        if(!this.plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        String dbPath = this.plugin.getDataFolder().getAbsolutePath()+"\\db.db";
        String connString = "jdbc:sqlite:"+dbPath;
        try{
            conn = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throwError(e.getMessage());
        } finally {
            try {
                this.createTables();
            } catch (SQLException e) {
                throwError(e.getMessage());
            }
        }

    };
    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS \"user_config\" (\n" +
                "\t\"uuid\"\tTEXT,\n" +
                "\t\"key\"\tTEXT,\n" +
                "\t\"value\"\tTEXT\n" +
                ");";
        stmt.execute(sql);
        sql="CREATE TABLE IF NOT EXISTS \"groups\" (\n" +
            "\t\"id\"\tTEXT,\n" +
            "\t\"name\"\tTEXT,\n" +
            "\t\"priority\"\tINTEGER\n" +
            ");";
        stmt.execute(sql);
        sql="CREATE TABLE IF NOT EXISTS \"groups_members\" (\n" +
                "\t\"gid\"\tTEXT,\n" +
                "\t\"mid\"\tTEXT,\n" +
                "\t\"mtype\"\tTEXT\n" +
                ");";
        stmt.execute(sql);
        sql="CREATE TABLE IF NOT EXISTS \"groups_config\" (\n" +
                "\t\"gid\"\tTEXT,\n" +
                "\t\"key\"\tTEXT,\n" +
                "\t\"value\"\tTEXT\n" +
                ");";
        stmt.execute(sql);

    }
    public void updateUserConfig(String uid, String key, String value) throws SQLException {
        String sql = "SELECT count(*) FROM \"main\".\"user_config\" WHERE \"uuid\"=? AND \"key\"=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,uid);
        pstmt.setString(2,key);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if(rs.getInt("count(*)")==0) {
            sql = "INSERT INTO \"main\".\"user_config\"(\"uuid\",\"key\",\"value\") VALUES (?,?,?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,uid);
            pstmt.setString(2,key);
            pstmt.setString(3,value);
        }
        else{
            sql="UPDATE \"main\".\"user_config\" SET \"value\"=? WHERE \"uuid\"=? AND \"key\"=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,value);
            pstmt.setString(2,uid);
            pstmt.setString(3,key);
        }
        pstmt.executeUpdate();

    }
    public String getUserConfig(String uuid,String key) throws SQLException {
        String sql = "SELECT * FROM \"main\".\"user_config\" WHERE \"uuid\"=? AND \"key\"=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,uuid);
        pstmt.setString(2,key);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            return rs.getString("value");
        }
        return null;
    }
    public List<String> getGroupMembers(String gid,String utype) throws SQLException {
        List<String> members = new ArrayList<String>();
        String sql = "Select * from groups_members where gid=? AND mtype=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.setString(2,utype);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next()){
            members.add(rs.getString("mid"));
        }
        return members;
    }
    public boolean isGroupMember(String gid,String mid) throws SQLException {
        String sql = "Select count(*) from groups_members where gid=? AND mid=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.setString(2,mid);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt("count(*)")!=0;
    }
    public boolean addGroupMember(String gid, String mid, String mtype) throws SQLException {
        if(!isGroupMember(gid,mid)){
            String sql = "INSERT INTO \"main\".\"groups_members\"(\"gid\",\"mid\",\"mtype\") VALUES (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,gid);
            pstmt.setString(2,mid);
            pstmt.setString(3,mtype);
            pstmt.execute();
            return true;
        }
        return false;
    }
    public void removeGroupMemeber(String gid,String mid) throws SQLException {
        if(isGroupMember(gid,mid)){
            String sql = "Delete from groups_members Where gid=? AND mid=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,gid);
            pstmt.setString(2,mid);
            pstmt.execute();
        }
    }
    public void updateGroupConfig(String gid, String key, String value) throws SQLException {
        String sql = "SELECT count(*) FROM \"main\".\"groups_config\" WHERE \"gid\"=? AND \"key\"=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.setString(2,key);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if(rs.getInt("count(*)")==0) {
            sql = "INSERT INTO \"main\".\"groups_config\"(\"gid\",\"key\",\"value\") VALUES (?,?,?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,gid);
            pstmt.setString(2,key);
            pstmt.setString(3,value);
        }
        else{
            sql="UPDATE \"main\".\"groups_config\" SET \"value\"=? WHERE \"gid\"=? AND \"key\"=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,value);
            pstmt.setString(2,gid);
            pstmt.setString(3,key);
        }
        pstmt.execute();

    }
    public String getGroupConfig(String gid,String key) throws SQLException {
        String sql = "SELECT * FROM \"main\".\"groups_config\" WHERE \"gid\"=? AND \"key\"=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.setString(2,key);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            return rs.getString("value");
        }
        return null;
    }
    public void removeGroupConfig(String gid,String key) throws SQLException {
        String sql = "DELETE FROM \"main\".\"groups_config\" WHERE \"gid\"=? AND \"key\"=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.setString(2,key);
        pstmt.execute();
    }
    public List<PCGroup> getGroups() throws SQLException {
        List<PCGroup> groups = new ArrayList<PCGroup>();
        String sql = "Select * from groups ORDER by priority DESC";
        Statement stmt= conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            groups.add(new PCGroup(plugin,rs.getString("id"),rs.getString("name"),rs.getInt("priority")));
        }

        return groups;
    }
    public Connection getConn(){
        return conn;
    }
    public void addGroup(String name, Integer priority) throws SQLException {
        String sql = "INSERT INTO \"main\".\"groups\"(\"id\",\"name\",\"priority\") VALUES (?,?,?);";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        UUID newGroupId = UUID.randomUUID();
        pstmt.setString(1,newGroupId.toString());
        pstmt.setString(2,name);
        pstmt.setInt(3,priority);
        pstmt.execute();
    }

    public void removeGroups(String gid) throws SQLException {
        String sql = "Delete from \"main\".\"groups\" WHERE id=?;";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,gid);
        pstmt.execute();
    }
}
