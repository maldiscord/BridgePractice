package lol.maltest.buildpractice;

import jdk.nashorn.internal.ir.Block;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.cmds.admin.CreateArena;
import lol.maltest.buildpractice.cmds.admin.ForceJoinArena;
import lol.maltest.buildpractice.cmds.admin.LeaveArena;
import lol.maltest.buildpractice.listeners.ArenaListener;
import lol.maltest.buildpractice.listeners.BlockListener;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BuildPractice extends JavaPlugin {

    public static BuildPractice plugin;

    private ArenaManager arenaManager;
    private BlockListener blockListener;
    private ArenaListener arenaListener;

    public void onEnable() {

        plugin = this;
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        arenaManager = new ArenaManager(this);
        blockListener = new BlockListener(this);
        arenaListener = new ArenaListener(this);

        arenaManager.deserialise();
        getCommand("createarena").setExecutor(new CreateArena(this)); // admin
        getCommand("leavearena").setExecutor(new LeaveArena(this));
        getCommand("joinarena").setExecutor(new ForceJoinArena(this));


        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    }

    public void onDisable() {
        arenaManager.getArenaList().clear();
        getServer().getConsoleSender().sendMessage(ChatUtil.clr("&cGoodbye <3"));
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BlockListener getBlockListener() {
        return blockListener;
    }

}
