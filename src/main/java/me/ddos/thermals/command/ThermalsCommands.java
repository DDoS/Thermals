package me.ddos.thermals.command;

import me.ddos.thermals.ThermalsPlugin;
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
	@RequiredValues({"set"})
	public void setLocation(CommandSender sender, String subCommand, IntLocation location, Integer size) {
		ThermalsPlugin.logInfo("SET Success!: " + subCommand + " " + location + " " + size);
	}

	@CommandMethod
	@RequiredValues({"get"})
	public void getLocation(CommandSender sender, String subCommand, IntLocation location, Integer size) {
		ThermalsPlugin.logInfo("GET Success!: " + subCommand + " " + location);
	}
}
