package me.pythontest.pythoncombat.pythoncombat;

import me.pythontest.pythoncombat.events.EventsListener;
import me.pythontest.pythoncombat.events.InfoEventListener;
import me.pythontest.pythoncombat.managers.CommandManager;
import me.pythontest.pythoncombat.managers.DamagerManager;
import me.pythontest.pythoncombat.managers.DatabaseManager;
import me.pythontest.pythoncombat.managers.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;

public final class Pythoncombat extends JavaPlugin {
    private DamagerManager damagerManager = new DamagerManager(this);
    private DatabaseManager databaseManager = new DatabaseManager(this);
    private StorageManager storageManager;
    private CommandManager commandManager = new CommandManager(this);
    private EventsListener eventsListener = new EventsListener(this);
    private static Pythoncombat instance;


    @Override
    public void onEnable() {
        instance=this;
        commandManager.registercommands();
        this.getServer().getPluginManager().registerEvents(eventsListener,this);
        this.getServer().getPluginManager().registerEvents(new InfoEventListener(),this);
        databaseManager.connect();
        storageManager = new StorageManager(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public StorageManager getStorageManager(){
        return this.storageManager;
    }
    public DatabaseManager getDatabaseManager(){return this.databaseManager;}

    public DamagerManager getDamagerManager() {
        return damagerManager;
    }
    public static Pythoncombat getInstance(){
        return instance;
    }

}
