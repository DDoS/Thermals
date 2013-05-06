package me.ddos.thermals.command;

import me.ddos.thermals.HeatManager;
import me.ddos.thermals.ThermalsPlugin;
import me.ddos.thermals.command.CommandHandler.CommandMethod;
import me.ddos.thermals.command.CommandHandler.CommandPermission;
import me.ddos.thermals.command.CommandHandler.RequiredArgumentValues;
import me.ddos.thermals.data.IntLocation;
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

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearLocation(CommandSender sender, String subCommand, IntLocation location) {
		manager.clearHeat(location);
		ThermalsPlugin.tell(sender, "Heat cleared at " + location);
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand, IntLocation location, Integer size) {
		clearArea(sender, subCommand, getMin(location, size), getMax(location, size));
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to) {
		manager.clearHeats(from, to);
		ThermalsPlugin.tell(sender, "Heats cleared from " + from + " to " + to);
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear", "all"})
	public void clearAll(CommandSender sender, String subCommand1, String subCommand2) {
		manager.clearAllHeats();
		ThermalsPlugin.tell(sender, "All heats cleared");
	}

	@CommandMethod
	@CommandPermission({"thermals", "get"})
	@RequiredArgumentValues({"get"})
	public void getLocation(CommandSender sender, String subCommand, IntLocation location) {
		ThermalsPlugin.tell(sender, "Heat at " + location + ": " + manager.getHeat(location).getHeat());
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setLocation(CommandSender sender, String subCommand, IntLocation location, Integer heat) {
		manager.setHeat(location, heat);
		ThermalsPlugin.tell(sender, "Heat set at " + location + " to " + heat);
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation location, Integer size, Integer heat) {
		setArea(sender, subCommand, getMin(location, size), getMax(location, size), heat);
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, Integer heat) {
		manager.setHeats(from, to, heat);
		ThermalsPlugin.tell(sender, "Heats set from " + from + " to " + to + " to " + heat);
	}

	@CommandMethod
	@CommandPermission({"thermals", "gen"})
	@RequiredArgumentValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation location, Integer size, String fileName) {
		generateArea(sender, subCommand, getMin(location, size), getMax(location, size), fileName);
	}

	@CommandMethod
	@CommandPermission({"thermals", "gen"})
	@RequiredArgumentValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, String fileName) {
		manager.queueHeatMapTask(from, to, fileName);
		ThermalsPlugin.tell(sender, "Generation of heat map from " + from + " to " + to + " queued");
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
