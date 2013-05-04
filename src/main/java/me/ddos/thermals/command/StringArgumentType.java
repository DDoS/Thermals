package me.ddos.thermals.command;

/**
 *
 * @author DDoS
 */
public class StringArgumentType implements ArgumentType {
	@Override
	public boolean isValid(String stringArg) {
		return true;
	}

	@Override
	public Object convert(String stringArg) {
		return stringArg;
	}
}
