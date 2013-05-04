package me.ddos.thermals.command;

import me.ddos.thermals.command.CommandHandler.CommandMethod;
import me.ddos.thermals.command.CommandHandler.RequiredValues;
import me.ddos.thermals.data.IntLocation;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class ThermalsCommands {
	@CommandMethod
	@RequiredValues({"clear", "all"})
	public void clearAll(CommandSender sender, String subCommand1, String subCommand2) {
	}

	@CommandMethod
	@RequiredValues({"clear"})
	public void clearLocation(CommandSender sender, String subCommand1, IntLocation location) {
	}

	@CommandMethod
	@RequiredValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand1, IntLocation from, IntLocation to) {
	}

	@CommandMethod
	@RequiredValues({"clear"})
	public void clearArea(CommandSender sender, String subCommand1, IntLocation location, Integer size) {
	}

	@CommandMethod
	@RequiredValues({"get"})
	public void getLocation(CommandSender sender, String subCommand, IntLocation location) {
	}

	@CommandMethod
	@RequiredValues({"set"})
	public void setLocation(CommandSender sender, String subCommand, IntLocation location, Integer heat) {
	}

	@CommandMethod
	@RequiredValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, Integer heat) {
	}

	@CommandMethod
	@RequiredValues({"set"})
	public void setArea(CommandSender sender, String subCommand, IntLocation from, Integer size, Integer heat) {
	}

	@CommandMethod
	@RequiredValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation from, IntLocation to, String fileName) {
	}

	@CommandMethod
	@RequiredValues({"gen"})
	public void generateArea(CommandSender sender, String subCommand, IntLocation from, Integer size, String fileName) {
	}
}
