package me.ddos.thermals.command;

import me.ddos.thermals.ThermalsPlugin;
import me.ddos.thermals.command.CommandHandler.SubCommand;
import me.ddos.thermals.data.IntLocation;

/**
 *
 * @author DDoS
 */
public class ThermalsCommands {
	@SubCommand({"set"})
	public void setLocation(String subCommand, IntLocation location, int size) {
		ThermalsPlugin.logInfo("Success!: " + subCommand + " " + location + " " + size);
	}
}
