package me.ddos.thermals.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.ddos.thermals.ThermalsPlugin;
import org.bukkit.configuration.Configuration;

/**
 *
 * @author DDoS
 */
public class AnnotatedConfiguration<T> {
	private final Map<String, Field> properties = new LinkedHashMap<String, Field>();

	public AnnotatedConfiguration(Class<T> config) {
		for (Field field : config.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Setting.class)) {
				final String path = field.getAnnotation(Setting.class).value();
				if (!path.isEmpty()) {
					properties.put(path, field);
				} else {
					properties.put(field.getName(), field);
				}
			}
		}
	}

	public void load(Configuration source, T destination) {
		try {
			for (Entry<String, Field> property : properties.entrySet()) {
				final String path = property.getKey();
				if (!source.contains(path)) {
					source.set(path, property.getValue().get(destination));
				} else {
					property.getValue().set(destination, source.get(path));
				}
			}
		} catch (Exception ex) {
			ThermalsPlugin.logSevere("Couldn't load the configuration: " + ex.getMessage());
		}
	}

	public void save(T source, Configuration destination) {
		try {
			for (Entry<String, Field> property : properties.entrySet()) {
				destination.set(property.getKey(), property.getValue().get(source));
			}
		} catch (Exception ex) {
			ThermalsPlugin.logSevere("Couldn't load the configuration: " + ex.getMessage());
		}
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Setting {
		public String value() default "";
	}
}
