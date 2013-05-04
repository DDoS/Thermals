package me.ddos.thermals.command;

import me.ddos.thermals.data.IntLocation;

/**
 *
 * @author DDoS
 */
public class IntLocationArgumentType implements ArgumentType {
	@Override
	public boolean isValid(String stringArg) {
		return IntLocation.isIntLocation(stringArg);
	}

	@Override
	public Object convert(String stringArg) {
		return IntLocation.parse(stringArg);
	}
}
