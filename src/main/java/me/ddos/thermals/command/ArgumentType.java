package me.ddos.thermals.command;

import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public interface ArgumentType {
	public boolean isValid(CommandSender sender, String stringArg);
	
	public Object convert(CommandSender sender, String stringArg);
}
