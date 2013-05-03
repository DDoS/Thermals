package me.ddos.thermals.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DDoS
 */
public class TypeFactory<T> {
	private final Map<String, Constructor<? extends T>> types =
			new HashMap<String, Constructor<? extends T>>();
	private final Class<?>[] constructorParams;

	public TypeFactory(Class<?>... constructorParams) {
		this.constructorParams = constructorParams;
	}

	public void register(String name, Class<? extends T> type) {
		if (types.containsKey(name)) {
			throw new IllegalArgumentException("Type \"" + type + "\" has already been registered");
		}
		try {
			types.put(name, type.getConstructor(constructorParams));
		} catch (NoSuchMethodException ex) {
			throw new IllegalArgumentException("Type \"" + type + "\" doesn't have the required constructor");
		}
	}

	public T newInstance(String type, Object... constructorParams) {
		if (!types.containsKey(type)) {
			throw new IllegalArgumentException("Type \"" + type + "\" is not a registered type");
		}
		try {
			return types.get(type).newInstance(constructorParams);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}