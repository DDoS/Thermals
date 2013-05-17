package me.ddos.thermals.command;

import java.lang.annotation.Annotation;
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
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class CommandHandler implements CommandExecutor {
	private final List<ArgumentType> argumentTypes = new ArrayList<ArgumentType>();
	private final Map<Method, Object> methodExecutors = new HashMap<Method, Object>();
	private final Map<Method, Class<?>[]> methodParameters = new HashMap<Method, Class<?>[]>();
	private final Map<Method, String[]> methodRequiredValues = new HashMap<Method, String[]>();
	private final Map<Method, String> methodPermissions = new HashMap<Method, String>();
	private String missingPermissionMessage = ChatColor.RED + "You don't have permission to use this command";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] stringArgs) {
		final Object[] args = convertStringArguments(sender, stringArgs);
		final List<Method> matches = findMatchingMethods(args);
		final int size = matches.size();
		try {
			if (size <= 0) {
				return false;
			}
			if (size == 1) {
				final Method method = matches.get(0);
				final String[] requiredValues = methodRequiredValues.get(method);
				if (requiredValues == null || hasRequiredValues(stringArgs, requiredValues)) {
					executeCommand(method, sender, args);
					return true;
				}
				return false;
			} else {
				Method bestMatch = null;
				matches:
				for (Method match : matches) {
					final String[] requiredValues = methodRequiredValues.get(match);
					if (requiredValues == null) {
						if (bestMatch == null) {
							bestMatch = match;
						}
						continue;
					}
					if (hasRequiredValues(stringArgs, requiredValues)) {
						bestMatch = match;
					}
				}
				if (bestMatch != null) {
					executeCommand(bestMatch, sender, args);
					return true;
				}
				return false;
			}
		} catch (Exception ex) {
			ThermalsPlugin.logSevere("Reflection error when trying to execute a command: " + ex.getMessage());
			ThermalsPlugin.tell(sender, "Error, please see console and report to author "
					+ ChatColor.UNDERLINE + ChatColor.BOLD + ChatColor.YELLOW + "with error log.");
			ex.printStackTrace();
			return true;
		}
	}

	private Object[] convertStringArguments(CommandSender sender, String[] stringArgs) {
		final Object[] args = new Object[stringArgs.length];
		for (int i = 0; i < stringArgs.length; i++) {
			for (ArgumentType type : argumentTypes) {
				if (type.isValid(sender, stringArgs[i])) {
					args[i] = type.convert(sender, stringArgs[i]);
					break;
				}
			}
		}
		return args;
	}

	private List<Method> findMatchingMethods(Object[] params) {
		final List<Method> matches = new ArrayList<Method>();
		entries:
		for (Entry<Method, Class<?>[]> entry : methodParameters.entrySet()) {
			final Class<?>[] argTypes = entry.getValue();
			if (argTypes.length != params.length) {
				continue;
			}
			for (int i = 0; i < params.length; i++) {
				if (!argTypes[i].equals(params[i].getClass())) {
					continue entries;
				}
			}
			matches.add(entry.getKey());
		}
		return matches;
	}

	private void executeCommand(Method executor, CommandSender sender, Object[] args) throws Exception {
		final String permission = methodPermissions.get(executor);
		if (permission != null && !sender.hasPermission(permission)) {
			ThermalsPlugin.tell(sender, missingPermissionMessage);
			return;
		}
		executor.invoke(methodExecutors.get(executor), ArrayUtils.add(args, 0, sender));
	}

	public void addArgumentTypes(ArgumentType... types) {
		for (ArgumentType type : types) {
			addArgumentType(type);
		}
	}

	public void addArgumentType(ArgumentType type) {
		argumentTypes.add(type);
	}

	public void addCommandExecutors(Object... executors) {
		for (Object executor : executors) {
			addCommandExecutor(executor);
		}
	}

	public void addCommandExecutor(Object executor) {
		for (Method method : executor.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (!method.isAnnotationPresent(CommandMethod.class)) {
				continue;
			}
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length < 1 || !parameterTypes[0].isAssignableFrom(CommandSender.class)) {
				throw new IllegalArgumentException("Command methods must have for first argument \"CommandSender\"");
			}
			methodExecutors.put(method, executor);
			final Class<?>[] parameterTypesArgs = new Class<?>[parameterTypes.length - 1];
			System.arraycopy(parameterTypes, 1, parameterTypesArgs, 0, parameterTypesArgs.length);
			methodParameters.put(method, parameterTypesArgs);
			final String permissionNodes = method.getAnnotation(CommandMethod.class).value();
			if (!permissionNodes.isEmpty()) {
				methodPermissions.put(method, permissionNodes);
			}
			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			final List<String> requiredValues = new ArrayList<String>();
			parameterAnnotations:
			for (int i = 1; i < parameterAnnotations.length; i++) {
				for (Annotation annotation : parameterAnnotations[i]) {
					if (annotation instanceof Require) {
						requiredValues.add(((Require) annotation).value());
						continue parameterAnnotations;
					}
				}
				requiredValues.add("");
			}
			if (requiredValues.size() > 0) {
				methodRequiredValues.put(method, requiredValues.toArray(new String[requiredValues.size()]));
			}
		}
	}

	public void setMissingPermissionMessage(String missingPermissionMessage) {
		this.missingPermissionMessage = missingPermissionMessage;
	}

	private static boolean hasRequiredValues(String[] args, String[] required) {
		for (int i = 0; i < required.length; i++) {
			if (required[i].isEmpty()) {
				continue;
			}
			if (!required[i].equals(args[i])) {
				return false;
			}
		}
		return true;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CommandMethod {
		public String value() default "";
	}

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Require {
		public String value() default "";
	}
}
