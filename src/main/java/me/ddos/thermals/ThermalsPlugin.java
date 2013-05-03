package me.ddos.thermals;

import me.ddos.thermals.heatmap.HeatColorizer;
import me.ddos.thermals.data.IntLocation;
import me.ddos.thermals.configuration.AnnotatedConfiguration;
import me.ddos.thermals.configuration.ThermalsConfiguration;
import me.ddos.thermals.util.ThermalsUtil;
import java.io.File;
import java.util.logging.Logger;
import me.ddos.thermals.database.HeatDatabase.DatabaseConnectionInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
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
			Class.forName("me.ddos.thermals.MySQLHeatDatabase");
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
		final DatabaseConnectionInfo info = new DatabaseConnectionInfo();
		info.setHost(config.databaseHost);
		info.setPort(config.databasePort);
		info.setDatabaseName(config.databaseName);
		info.setUser(config.databaseUser);
		info.setPassword(config.databasePassword);
		heatManager = new HeatManager(config.databaseType);
		heatManager.setDatabaseInfo(info);
		heatManager.setLoggerDelay(config.loggerDelay);
		heatManager.setLoggerRunThreshold(config.loggerRunThreshold);
		heatManager.shouldLoggerSupressInfo(config.loggerSuppressInfo);
		heatManager.setMinHeat(config.generatorMinHeat);
		heatManager.setMaxHeat(config.generatorMaxHeat);
		heatManager.setGeneratorColorizer(new HeatColorizer(config.heatGradient));
		heatManager.start();
		getServer().getPluginManager().registerEvents(new ThermalsListener(this), this);
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 3) {
			tell(sender, "Missing arguments");
			return false;
		}
		final IntLocation start = ThermalsUtil.parse(args[0]);
		final IntLocation end = ThermalsUtil.parse(args[1]);
		if (start == null || end == null) {
			tell(sender, "Invalid location format");
			return false;
		}
		heatManager.queueHeatMapTask(start, end, args[2]);
		tell(sender, "Heat map generation queued");
		return true;
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

	public static void tell(CommandSender dest, String msg) {
		dest.sendMessage(ChatColor.DARK_RED + "[Thermals] " + ChatColor.GRAY + msg);
	}
}
