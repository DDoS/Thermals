package me.ddos.thermals;

import java.awt.Font;
import me.ddos.thermals.heatmap.HeatColorizer;
import me.ddos.thermals.configuration.AnnotatedConfiguration;
import me.ddos.thermals.configuration.ThermalsConfiguration;
import java.io.File;
import java.util.logging.Logger;
import me.ddos.thermals.command.CommandHandler;
import me.ddos.thermals.command.IntLocationArgumentType;
import me.ddos.thermals.command.IntegerArgumentType;
import me.ddos.thermals.command.ThermalsCommands;
import me.ddos.thermals.database.HeatDatabase.DatabaseConnectionInfo;
import me.ddos.thermals.heatmap.Background;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author DDoS
 */
public class ThermalsPlugin extends JavaPlugin {
	private static final Logger LOG = Logger.getLogger(ThermalsPlugin.class.getName());
	private static final AnnotatedConfiguration<ThermalsConfiguration> CONFIG_SETTINGS =
			new AnnotatedConfiguration<ThermalsConfiguration>(ThermalsConfiguration.class);
	public static final File PLUGIN_DIR = new File("plugins/Thermals");
	private static final File CONFIG_FILE = new File(PLUGIN_DIR, "config.yml");
	private final YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(CONFIG_FILE);
	private ThermalsConfiguration config = new ThermalsConfiguration();
	private HeatManager heatManager;

	static {
		try {
			Class.forName("me.ddos.thermals.database.MySQLHeatDatabase");
		} catch (ClassNotFoundException ex) {
			logSevere("Couldn't find the MySQLHeatDatabase class");
		}
		if (!PLUGIN_DIR.exists()) {
			PLUGIN_DIR.mkdirs();
		}
		if (!CONFIG_FILE.exists()) {
			try {
				CONFIG_FILE.createNewFile();
			} catch (Exception ex) {
				logSevere("Couldn't create the empty config file: " + ex.getMessage());
			}
		}
	}

	@Override
	public void onEnable() {
		CONFIG_SETTINGS.load(yamlConfig, config);
		try {
			yamlConfig.save(CONFIG_FILE);
		} catch (Exception ex) {
			logSevere("Couldn't save the config defaults: " + ex.getMessage());
		}
		heatManager = createHeatManager();
		heatManager.start();
		getServer().getPluginManager().registerEvents(new ThermalsListener(this), this);
		final CommandHandler commandHandler = new CommandHandler("th");
		commandHandler.addArgumentTypes(new IntegerArgumentType(), new IntLocationArgumentType());
		commandHandler.addCommandExecutor(new ThermalsCommands(heatManager));
		getCommand("th").setExecutor(commandHandler);
		final PluginDescriptionFile description = getDescription();
		logInfo("Enabled. v" + description.getVersion() + ", by " + description.getAuthors().get(0));
	}

	@Override
	public void onDisable() {
		if (heatManager != null) {
			heatManager.stop();
		}
		CONFIG_SETTINGS.save(config, yamlConfig);
		try {
			yamlConfig.save(CONFIG_FILE);
		} catch (Exception ex) {
			logSevere("Couldn't save the config: " + ex.getMessage());
		}
		final PluginDescriptionFile description = getDescription();
		logInfo("Disabled. v" + description.getVersion() + ", by " + description.getAuthors().get(0));
	}

	private HeatManager createHeatManager() {
		final HeatManager manager = new HeatManager(config.databaseType);
		final DatabaseConnectionInfo info = new DatabaseConnectionInfo();
		info.setHost(config.databaseHost);
		info.setPort(config.databasePort);
		info.setDatabaseName(config.databaseName);
		info.setUser(config.databaseUser);
		info.setPassword(config.databasePassword);
		manager.setDatabaseInfo(info);
		manager.setLoggerDelay(config.loggerDelay);
		manager.setLoggerRunThreshold(config.loggerRunThreshold);
		manager.shouldLoggerSupressInfo(config.loggerSuppressInfo);
		manager.setMinHeat(config.generatorMinHeat);
		manager.setMaxHeat(config.generatorMaxHeat);
		manager.setGeneratorColorizer(new HeatColorizer(config.heatGradient));
		final Background background = new Background();
		background.setBackgroundColor(config.backgroundColor);
		background.drawGrid(config.drawGrid);
		background.setGridLineInterval(config.gridLineInterval);
		background.setGridLineColor(config.gridLineColor);
		background.drawGridCoords(config.drawGridCoords);
		background.setCoordsGridLineInterval(config.coordsGridLineInterval);
		background.setCoordsPointColor(config.coordsPointColor);
		background.setCoordsFontColor(config.coordsFontColor);
		background.setCoordsFont(new Font(config.coordsFontName, Font.PLAIN, config.coordsFontSize));
		manager.setGeneratorBackground(background);
		return manager;
	}

	public HeatManager getHeatManager() {
		return heatManager;
	}

	public static void logInfo(String msg) {
		LOG.info("[Thermals] " + msg);
	}

	public static void logSevere(String msg) {
		LOG.severe("[Thermals] " + msg);
	}

	public static void tell(CommandSender destination, String msg) {
		destination.sendMessage(ChatColor.DARK_RED + "[Thermals] " + ChatColor.GRAY + msg);
	}
}
