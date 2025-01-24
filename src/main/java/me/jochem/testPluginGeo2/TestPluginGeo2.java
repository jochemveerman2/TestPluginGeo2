package me.jochem.testPluginGeo2;

import co.aikar.commands.BukkitCommandManager;
import me.jochem.testPluginGeo2.commands.*;
import me.jochem.testPluginGeo2.listeners.BlockPlaceListener;
import me.jochem.testPluginGeo2.listeners.BlockBreakListener;
import me.jochem.testPluginGeo2.managers.AnnounceManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class TestPluginGeo2 extends JavaPlugin {

    private static TestPluginGeo2 pluginInstance;
    private static BlockPlaceListener blockPlaceListener = new BlockPlaceListener();
    private static BlockBreakListener blockBreakListener = new BlockBreakListener();

    private HikariDataSource dataSource;

    public static TestPluginGeo2 getInstance() {
        return pluginInstance;
    }

    @Override
    public void onEnable() {
        pluginInstance = this;
        saveDefaultConfig();
        setupDatabase();

        AnnounceManager announceManager = new AnnounceManager();

        BukkitCommandManager commandManager = new BukkitCommandManager(TestPluginGeo2.this);
        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new DirtCommand());
        commandManager.registerCommand(new StoneCommand());
        commandManager.registerCommand(new WoodCommand());
        commandManager.registerCommand(new KitBookCommand());
        commandManager.registerCommand(new GroepenCommand());
        commandManager.registerCommand(new BlockLogCommand());
        commandManager.registerCommand(new WarpCommand());
        commandManager.registerCommand(new DeleteWarpCommand());
        commandManager.registerCommand(new WarpListCommand());
        commandManager.registerCommand(new SetWarpCommand());
        commandManager.registerCommand(new SleepCommand());
        commandManager.registerCommand(new AnnounceCommand(announceManager));
        commandManager.registerCommand(new ListAnnouncesCommand(announceManager));
        commandManager.registerCommand(new StopAnnounceCommand(announceManager));
        commandManager.registerCommand(new SleepfixedCommand());

        if (getConfig().getBoolean("bloklog.listenersActive", false)) {
            registerListeners();
        }
    }

    @Override
    public void onDisable() {
        pluginInstance = null;
        deregisterListeners();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public void setupDatabase() {
        String ip = getConfig().getString("warpdatabase.ip");
        int port = getConfig().getInt("warpdatabase.port");
        String dbName = getConfig().getString("warpdatabase.database-name");
        String username = getConfig().getString("warpdatabase.username");
        String password = getConfig().getString("warpdatabase.password");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + dbName);
        config.setUsername(username);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            getLogger().info("Verbonden met database.");

            String createTableQuery = "CREATE TABLE IF NOT EXISTS warps (id INT AUTO_INCREMENT PRIMARY KEY, warpname VARCHAR(30) NOT NULL, worldname VARCHAR(255) NOT NULL, x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL, pitch DOUBLE NOT NULL, yaw DOUBLE NOT NULL, hidden TINYINT(1) DEFAULT 0);";

            try (var statement = connection.createStatement()) {
                statement.executeUpdate(createTableQuery);
            } catch (SQLException e) {
                getLogger().severe("Fout bij het aanmaken van de tabel: " + e.getMessage());
            }

        } catch (SQLException e) {
            getLogger().severe("Databaseverbinding mislukt: " + e.getMessage());
        }
    }


    public void registerListeners() {
        getServer().getPluginManager().registerEvents(blockPlaceListener, this);
        getServer().getPluginManager().registerEvents(blockBreakListener, this);
    }

    public void deregisterListeners() {
        HandlerList.unregisterAll(blockPlaceListener);
        HandlerList.unregisterAll(blockBreakListener);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}