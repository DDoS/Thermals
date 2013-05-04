package me.ddos.thermals.command;

/**
 *
 * @author DDoS
 */
public interface ArgumentType {
	public boolean isValid(String stringArg);
	
	public Object convert(String stringArg);
}
