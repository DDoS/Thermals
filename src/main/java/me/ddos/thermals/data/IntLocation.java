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

	public IntLocation(IntLocation location) {
		this(location.getX(), location.getZ());
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

	public long getID() {
		return (long) x << 32 | z & 0xffffffffl;
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
