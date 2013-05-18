package me.ddos.thermals.command;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.ddos.thermals.ThermalsPlugin;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author DDoS
 */
public class CommandHandler implements CommandExecutor {
	private final String rootCommand;
	private final List<ArgumentType> argumentTypes = new ArrayList<ArgumentType>();
	private final Map<Method, Object> methodExecutors = new LinkedHashMap<Method, Object>();
	private final Map<Method, Class<?>[]> methodParameters = new LinkedHashMap<Method, Class<?>[]>();
	private final Map<Method, String[]> methodRequiredValues = new LinkedHashMap<Method, String[]>();
	private final Map<Method, String> methodPermissions = new LinkedHashMap<Method, String>();
	private String missingPermissionMessage = ChatColor.RED + "You don't have permission to use this command";
	private Document documentation;

	public CommandHandler(String rootCommand) {
		this.rootCommand = rootCommand;
		argumentTypes.add(new StringArgumentType());
	}

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
		argumentTypes.add(argumentTypes.size() - 1, type);
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
			removeTrailingEmptyStrings(requiredValues);
			if (requiredValues.size() > 0) {
				methodRequiredValues.put(method, requiredValues.toArray(new String[requiredValues.size()]));
			}
		}
	}

	public void setMissingPermissionMessage(String missingPermissionMessage) {
		this.missingPermissionMessage = missingPermissionMessage;
	}

	public Document getCommandDocs() {
		return getCommandDocs(false);
	}

	public Document getCommandDocs(boolean refresh) {
		if (documentation != null && !refresh) {
			return documentation;
		}
		final Document document = new Document();
		int i = 0;
		final String[] argTypeDocs = new String[argumentTypes.size() - 1];
		for (; i < argTypeDocs.length; i++) {
			final ArgumentType argType = argumentTypes.get(i);
			argTypeDocs[i] = WordUtils.capitalize(argType.getTypeName()) + "\n" + argType.getDocs();
		}
		document.addParagraphs("Argument Types", argTypeDocs);
		i = 0;
		final String[] paramDocs = new String[methodExecutors.size()];
		for (Method method : methodExecutors.keySet()) {
			final CommandDocs docs = method.getAnnotation(CommandDocs.class);
			final String[] argNames;
			if (docs != null) {
				final String desc = docs.desc();
				if (desc != null && !desc.isEmpty()) {
					paramDocs[i] = desc;
				}
				argNames = docs.argNames();
			} else {
				argNames = null;
				paramDocs[i] = "No description provided.";
			}
			paramDocs[i] += "\n";
			final String[] requiredValues = methodRequiredValues.get(method);
			String parameters = "/" + rootCommand;
			int ii = -1;
			for (Class<?> paramClass : methodParameters.get(method)) {
				if (requiredValues != null && ++ii < requiredValues.length && !requiredValues[ii].isEmpty()) {
					parameters += " " + requiredValues[ii];
					continue;
				}
				for (ArgumentType argType : argumentTypes) {
					if (paramClass.equals(argType.getType())) {
						final String argTypeName = argType.getTypeName();
						final String argName;
						if (argNames != null && ii < argNames.length && !argNames[ii].isEmpty()) {
							argName = argNames[ii];
						} else {
							argName = argTypeName.substring(0, Math.min(3, argTypeName.length()));
						}
						parameters += " [(" + argTypeName + ") " + argName + "]";
						break;
					}
				}
			}
			paramDocs[i] += parameters.trim();
			final String permission = methodPermissions.get(method);
			if (permission != null) {
				paramDocs[i] += "\n" + permission;
			}
			i++;
		}
		document.addParagraphs("Command Usage", paramDocs);
		return document;
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

	private static void removeTrailingEmptyStrings(List<String> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isEmpty()) {
				list.remove(i);
			} else {
				break;
			}
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CommandMethod {
		public String value() default "";
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CommandDocs {
		public String[] argNames() default {};

		public String desc() default "";
	}

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Require {
		public String value() default "";
	}

	public static interface ArgumentType {
		public boolean isValid(CommandSender sender, String stringArg);

		public Object convert(CommandSender sender, String stringArg);

		public Class<?> getType();

		public String getTypeName();

		public String getDocs();
	}

	private static class StringArgumentType implements ArgumentType {
		@Override
		public boolean isValid(CommandSender sender, String stringArg) {
			return true;
		}

		@Override
		public String convert(CommandSender sender, String stringArg) {
			return stringArg;
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}

		@Override
		public String getTypeName() {
			return "string";
		}

		@Override
		public String getDocs() {
			return "";
		}
	}

	public static class Document {
		private final Map<String, List<String>> sections = new LinkedHashMap<String, List<String>>();

		public Set<String> getSections() {
			return sections.keySet();
		}

		public void addParagraphs(String section, String... paragraphs) {
			final List<String> sectionParagraphs;
			if (!sections.containsKey(section)) {
				sectionParagraphs = new ArrayList<String>();
				sections.put(section, sectionParagraphs);
			} else {
				sectionParagraphs = sections.get(section);
			}
			sectionParagraphs.addAll(Arrays.asList(paragraphs));
		}

		public List<String> getParagraphs(String section) {
			return sections.get(section);
		}

		public String getParagraph(String section, int index) {
			if (!sections.containsKey(section)) {
				return null;
			}
			final List<String> paragraphs = sections.get(section);
			if (index >= paragraphs.size()) {
				return null;
			}
			return paragraphs.get(index);
		}

		@Override
		public String toString() {
			final String[] sectionStrings = new String[sections.size()];
			int i = 0;
			for (Entry<String, List<String>> section : sections.entrySet()) {
				sectionStrings[i++] = section.getKey() + "\n\n"
						+ StringUtils.join(section.getValue().toArray(), "\n\n");
			}
			StringUtils.join(sectionStrings, "\n\n");
			return StringUtils.join(sectionStrings, "\n\n--------------------\n\n");
		}
	}
}
