package me.ddos.thermals.command;

import me.ddos.thermals.HeatManager;
import me.ddos.thermals.ThermalsPlugin;
import me.ddos.thermals.command.CommandHandler.CommandDocs;
import me.ddos.thermals.command.CommandHandler.CommandMethod;
import me.ddos.thermals.command.CommandHandler.Require;
import me.ddos.thermals.location.IntLocation;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class ThermalsCommands {
	private final HeatManager manager;

	public ThermalsCommands(HeatManager manager) {
		this.manager = manager;
	}

	@CommandMethod("thermals.clear")
	@CommandDocs(argNames = {"", "location"}, desc = "Clears the heat value at the given location.")
	public void clearLocation(CommandSender sender, @Require("clear") String subCommand, IntLocation location) {
		manager.clearHeat(location);
		ThermalsPlugin.tell(sender, "Heat cleared at " + location);
	}

	@CommandMethod("thermals.clear")
	@CommandDocs(argNames = {"", "middle", "radius"}, desc = "Clears all of the heat values in a square of given radius, with the location being the middle.")
	public void clearArea(CommandSender sender, @Require("clear") String subCommand, IntLocation location, Integer size) {
		clearArea(sender, subCommand, getMin(location, size), getMax(location, size));
	}

	@CommandMethod("thermals.clear")
	@CommandDocs(argNames = {"", "from", "to"}, desc = "Clears all of the heat values in the area included between the first and second locations.")
	public void clearArea(CommandSender sender, @Require("clear") String subCommand, IntLocation from, IntLocation to) {
		manager.clearHeats(from, to);
		ThermalsPlugin.tell(sender, "Heats cleared from " + from + " to " + to);
	}

	@CommandMethod("thermals.clear")
	@CommandDocs(desc = "Clears all of the heat values. This action is irreversible!")
	public void clearAll(CommandSender sender, @Require("clear") String subCommand1, @Require("all") String subCommand2) {
		manager.clearAllHeats();
		ThermalsPlugin.tell(sender, "All heats cleared");
	}

	@CommandMethod("thermals.get")
	@CommandDocs(argNames = {"", "location"}, desc = "Gets the heat value at the given location.")
	public void getLocation(CommandSender sender, @Require("get") String subCommand, IntLocation location) {
		ThermalsPlugin.tell(sender, "Heat at " + location + ": " + manager.getHeat(location).getHeat());
	}

	@CommandMethod("thermals.set")
	@CommandDocs(argNames = {"", "location", "heat"}, desc = "Sets the heat value at the given location.")
	public void setLocation(CommandSender sender, @Require("set") String subCommand, IntLocation location, Integer heat) {
		manager.setHeat(location, heat);
		ThermalsPlugin.tell(sender, "Heat set at " + location + " to " + heat);
	}

	@CommandMethod("thermals.set")
	@CommandDocs(argNames = {"", "middle", "radius", "heat"}, desc = "Sets all of the heat values in a square of given radius, with the location being the middle, to the given heat.")
	public void setArea(CommandSender sender, @Require("set") String subCommand, IntLocation location, Integer size, Integer heat) {
		setArea(sender, subCommand, getMin(location, size), getMax(location, size), heat);
	}

	@CommandMethod("thermals.set")
	@CommandDocs(argNames = {"", "from", "to", "heat"}, desc = "Sets all of the heat values in the area included between the first and second locations to the given heat.")
	public void setArea(CommandSender sender, @Require("set") String subCommand, IntLocation from, IntLocation to, Integer heat) {
		manager.setHeats(from, to, heat);
		ThermalsPlugin.tell(sender, "Heats set from " + from + " to " + to + " to " + heat);
	}

	@CommandMethod("thermals.gen")
	@CommandDocs(argNames = {"", "middle", "radius", "file name"}, desc = "Generates a heat map of all of the heat values in a square of given radius, with the location being the middle,"
			+ " and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.")
	public void generateArea(CommandSender sender, @Require("gen") String subCommand, IntLocation location, Integer size, String fileName) {
		generateArea(sender, subCommand, getMin(location, size), getMax(location, size), fileName);
	}

	@CommandMethod("thermals.gen")
	@CommandDocs(argNames = {"", "from", "to", "file name"}, desc = "Generates a heat map of all of the heat values in the area included between the first and second locations"
			+ " and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.")
	public void generateArea(CommandSender sender, @Require("gen") String subCommand, IntLocation from, IntLocation to, String fileName) {
		manager.queueHeatMapTask(from, to, fileName);
		ThermalsPlugin.tell(sender, "Generation of heat map from " + from + " to " + to + " queued");
	}

	@CommandMethod("thermals.connect")
	@CommandDocs(desc = "Attemps to establish a connection to the database.")
	public void connectDatabase(CommandSender sender, @Require("connect") String subCommand) {
		if (manager.ensureDatabaseConnected()) {
			ThermalsPlugin.tell(sender, "The database connection has been established.");
			return;
		}
		ThermalsPlugin.tell(sender, "The database connection failed or has already been established.");
	}

	private static IntLocation getMin(IntLocation location, int size) {
		final int halfSize = size / 2;
		return location.offset(-halfSize, -halfSize);
	}

	private static IntLocation getMax(IntLocation location, int size) {
		final int halfSize = size / 2;
		return location.offset(halfSize, halfSize);
	}
}
