package me.ddos.thermals.data;

import java.util.regex.Pattern;
import org.bukkit.Location;

/**
 *
 * @author DDoS
 */
public class IntLocation {
	private static final Pattern FORMAT = Pattern.compile("-?\\d+,-?\\d+");
	private final int x;
	private final int z;

	public IntLocation(Location location) {
		this(location.getBlockX(), location.getBlockZ());
	}

	public IntLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public IntLocation offset(int x, int z) {
		return new IntLocation(getX() + x, getZ() + z);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntLocation)) {
			return false;
		}
		final IntLocation other = (IntLocation) obj;
		if (x != other.x) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + x;
		hash = 37 * hash + z;
		return hash;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + z + ")";
	}

	public static boolean isIntLocation(String exp) {
		return FORMAT.matcher(exp).matches();
	}

	public static IntLocation parse(String exp) {
		if (exp == null) {
			return null;
		}
		final String[] coords = exp.split(",");
		if (coords.length < 2) {
			return null;
		}
		try {
			return new IntLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
		} catch (Exception ex) {
			return null;
		}
	}
}
