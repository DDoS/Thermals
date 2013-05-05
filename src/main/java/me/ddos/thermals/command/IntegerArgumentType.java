package me.ddos.thermals.command;

import me.ddos.thermals.util.ThermalsUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class IntegerArgumentType implements ArgumentType {
	@Override
	public boolean isValid(CommandSender sender, String stringArg) {
		return ThermalsUtil.isInt(stringArg);
	}

	@Override
	public Object convert(CommandSender sender, String stringArg) {
		return Integer.parseInt(stringArg);
	}
}
