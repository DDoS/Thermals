package me.ddos.thermals.command;

import me.ddos.thermals.util.ThermalsUtil;

/**
 *
 * @author DDoS
 */
public class IntegerArgumentType implements ArgumentType {
	@Override
	public boolean isValid(String stringArg) {
		return ThermalsUtil.isInt(stringArg);
	}

	@Override
	public Object convert(String stringArg) {
		return Integer.parseInt(stringArg);
	}
}
