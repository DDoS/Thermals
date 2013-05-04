package me.ddos.thermals.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.ddos.thermals.ThermalsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class CommandHandler implements CommandExecutor {
	private final List<ArgumentType> types = new ArrayList<ArgumentType>();
	private final Map<Method, Object> executors = new HashMap<Method, Object>();
	private final Map<Method, Class<?>[]> methodParams = new HashMap<Method, Class<?>[]>();
	private final Map<Method, String[]> subCommands = new HashMap<Method, String[]>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] stringArgs) {
		final Object[] args = new Object[stringArgs.length];
		for (int i = 0; i < stringArgs.length; i++) {
			for (ArgumentType type : types) {
				if (type.isValid(stringArgs[i])) {
					args[i] = type.convert(stringArgs[i]);
					break;
				}
			}
		}
		final List<Method> matches = new ArrayList<Method>();
		entries:
		for (Entry<Method, Class<?>[]> entry : methodParams.entrySet()) {
			final Class<?>[] argTypes = entry.getValue();
			if (argTypes.length != args.length) {
				continue entries;
			}
			for (int i = 0; i < args.length; i++) {
				if (!argTypes[i].equals(args[i].getClass())) {
					continue entries;
				}
			}
			matches.add(entry.getKey());
		}
		final int size = matches.size();
		try {
			if (size <= 0) {
				return false;
			}
			if (size == 1) {
				final Method method = matches.get(0);
				method.invoke(executors.get(method), args);
				return true;
			} else {
				Method bestMatch = null;
				matches:
				for (Method match : matches) {
					final String[] subCommand = subCommands.get(match);
					if (subCommand == null) {
						if (bestMatch == null) {
							bestMatch = match;
						}
						continue;
					}
					for (int i = 0; i < subCommand.length; i++) {
						if (subCommand[i].equals("")) {
							continue;
						}
						if (!(args[i] instanceof String)) {
							continue;
						}
						if (!subCommand[i].equals((String) args[i])) {
							continue matches;
						}
					}
					bestMatch = match;
				}
				if (bestMatch == null) {
					return false;
				}
				bestMatch.invoke(executors.get(bestMatch), args);
				return true;
			}
		} catch (Exception ex) {
			ThermalsPlugin.logSevere("Reflection error when trying to execute a command: " + ex.getMessage());
			ThermalsPlugin.tell(sender, "Error, please see console and report to author "
					+ ChatColor.YELLOW + ChatColor.BOLD + ChatColor.UNDERLINE + "with error log.");
			return true;
		}
	}

	public void addCommandExecutors(Object executor) {
		for (Method method : executor.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			executors.put(method, executor);
			methodParams.put(method, method.getParameterTypes());
			subCommands.put(method, method.getAnnotation(SubCommand.class).value());
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SubCommand {
		public String[] value() default {};
	}
}
