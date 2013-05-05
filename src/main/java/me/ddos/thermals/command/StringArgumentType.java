package me.ddos.thermals.command;

import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class StringArgumentType implements ArgumentType {
	@Override
	public boolean isValid(CommandSender sender, String stringArg) {
		return true;
	}

	@Override
	public Object convert(CommandSender sender, String stringArg) {
		return stringArg;
	}
}
