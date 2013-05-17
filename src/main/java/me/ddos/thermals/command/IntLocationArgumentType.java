package me.ddos.thermals.command;

import me.ddos.thermals.location.IntLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class IntLocationArgumentType implements ArgumentType {
	private static final int MAX_VIEW = 500;

	@Override
	public boolean isValid(CommandSender sender, String stringArg) {
		return sender instanceof Player && (stringArg.equals("here") || stringArg.equals("there"))
				|| IntLocation.isIntLocation(stringArg);
	}

	@Override
	public Object convert(CommandSender sender, String stringArg) {
		if (stringArg.equals("here")) {
			return new IntLocation(((Player) sender).getLocation());
		}
		if (stringArg.equals("there")) {
			return new IntLocation(((Player) sender).getTargetBlock(null, MAX_VIEW).getLocation());
		}
		return IntLocation.parse(stringArg);
	}
}
