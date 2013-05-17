package me.ddos.thermals.command;

import me.ddos.thermals.command.CommandHandler.ArgumentType;
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
	public IntLocation convert(CommandSender sender, String stringArg) {
		if (stringArg.equals("here")) {
			return new IntLocation(((Player) sender).getLocation());
		}
		if (stringArg.equals("there")) {
			return new IntLocation(((Player) sender).getTargetBlock(null, MAX_VIEW).getLocation());
		}
		return IntLocation.parse(stringArg);
	}

	@Override
	public Class<IntLocation> getType() {
		return IntLocation.class;
	}

	@Override
	public String getTypeName() {
		return "location";
	}

	@Override
	public String getDocs() {
		return "Represents a 2D location in a world.\n"
				+ "Format: [(integer) x],[(integer) z] OR here OR there\n"
				+ "Example: -32,76\n"
				+ "\"here\" represents the player's current location (x and z).\n"
				+ "\"there\" represents the player's target location (x and z of the block being looked at, up to 500 blocks away).\n"
				+ "\"here\" and \"there\" can only be used in game.";
	}
}
