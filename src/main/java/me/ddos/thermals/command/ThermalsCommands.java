package me.ddos.thermals.command;

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
	private final ThermalsPlugin plugin;

	public ThermalsCommands(ThermalsPlugin plugin) {
		this.plugin = plugin;
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear", "all"})
	public void clearAll(CommandSender sender, String subCommand1, String subCommand2) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearLocation(CommandSender sender, String subCommand1, IntLocation location) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand1, IntLocation from, IntLocation to) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "clear"})
	@RequiredArgumentValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand1, IntLocation location, Integer size) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "get"})
	@RequiredArgumentValues({"get"})
	public void getLocation(CommandSender sender, String subCommand, IntLocation location) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setLocation(CommandSender sender, String subCommand, IntLocation location, Integer heat) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, Integer heat) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "set"})
	@RequiredArgumentValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation location, Integer size, Integer heat) {
	}

	@CommandMethod
	@CommandPermission({"thermals", "gen"})
	@RequiredArgumentValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, String fileName) {
		plugin.getHeatManager().queueHeatMapTask(from, to, fileName);
		ThermalsPlugin.tell(sender, "Generation queued");
	}

	@CommandMethod
	@CommandPermission({"thermals", "gen"})
	@RequiredArgumentValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation location, Integer size, String fileName) {
		final int halfSize = size / 2;
		generateArea(sender, subCommand, location.offset(-halfSize, -halfSize), location.offset(halfSize, halfSize), fileName);
	}
}
