package me.ddos.thermals.command;

import me.ddos.thermals.command.CommandHandler.ArgumentType;
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
	public Integer convert(CommandSender sender, String stringArg) {
		return Integer.parseInt(stringArg);
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	@Override
	public String getTypeName() {
		return "integer";
	}

	@Override
	public String getDocs() {
		return "Represents an integer (whole) number with a value range of about -2 to 2 billion.\n"
				+ "Decimal places are not accepted.";
	}
}
