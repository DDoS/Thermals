package me.ddos.thermals;

import me.ddos.thermals.heatmap.HeatColorizer;
import me.ddos.thermals.configuration.AnnotatedConfiguration;
import me.ddos.thermals.configuration.ThermalsConfiguration;
import java.io.File;
import java.util.logging.Logger;
import me.ddos.thermals.command.CommandHandler;
import me.ddos.thermals.command.IntLocationArgumentType;
import me.ddos.thermals.command.IntegerArgumentType;
import me.ddos.thermals.command.StringArgumentType;
import me.ddos.thermals.command.ThermalsCommands;
import me.ddos.thermals.database.HeatDatabase.DatabaseConnectionInfo;
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
		final CommandHandler commandHandler = new CommandHandler();
		commandHandler.addArgumentTypes(new IntegerArgumentType(), new IntLocationArgumentType(), new StringArgumentType());
		commandHandler.addCommandExecutor(new ThermalsCommands());
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

//	@Override
//	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//		// CLEAR ALL: /th 'clear' 'all'
//		// CLEAR lOC: /th 'clear' loc
//		// CLEAR AREA: /th 'clear' loc loc
//		// GET LOC: /th 'get' loc
//		// SET LOC: /th 'set' loc int
//		// SET AREA 1: /th 'set' loc loc int
//		// SET AREA 2: /th 'set' loc int int
//		// GEN AREA 1: /th 'gen' loc loc string
//		// GEN AREA 2: /th 'gen' loc int string
//		if (!cmd.getName().equalsIgnoreCase("th")) {
//			return false;
//		}
//		if (args.length >= 1) {
//			if (args[0].equalsIgnoreCase("clear")) {
//				if (args.length >= 2) {
//					if (args[1].equalsIgnoreCase("all")) {
//						// CLEAR ALL
//						return true;
//					} else if (IntLocation.isIntLocation(args[1])) {
//						if (args.length >= 3) {
//							if (IntLocation.isIntLocation(args[2])) {
//								// CLEAR AREA
//								return true;
//							}
//						}
//						// CLEAR LOC
//						return true;
//					}
//				}
//			} else if (args[0].equalsIgnoreCase("get")) {
//				if (args.length >= 2) {
//					if (IntLocation.isIntLocation(args[1])) {
//						// GET LOC
//						return true;
//					}
//				}
//			} else if (args[0].equalsIgnoreCase("set")) {
//				if (args.length >= 2) {
//					if (IntLocation.isIntLocation(args[1])) {
//						if (args.length >= 3) {
//							if (ThermalsUtil.isInt(args[2])) {
//								// SET LOC
//								return true;
//							} else if (IntLocation.isIntLocation(args[2])) {
//								if (args.length >= 4) {
//									if (ThermalsUtil.isInt(args[3])) {
//										// SET AREA 1
//										return true;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
////		if (args.length < 3) {
////			tell(sender, "Missing arguments");
////			return false;
////		}
////		final IntLocation start = ThermalsUtil.parse(args[0]);
////		final IntLocation end = ThermalsUtil.parse(args[1]);
////		if (start == null || end == null) {
////			tell(sender, "Invalid location format");
////			return false;
////		}
////		heatManager.queueHeatMapTask(start, end, args[2]);
////		tell(sender, "Heat map generation queued");
////		return true;
//		return false;
//	}
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
