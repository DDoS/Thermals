package me.ddos.thermals.util;

import me.ddos.thermals.data.IntLocation;

/**
 *
 * @author DDoS
 */
public class ThermalsUtil {
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

	public static void normalize(float[][] vals, float min, float max) {
		final float divider = max - min + 1;
		for (int x = 0; x < vals.length; x++) {
			for (int z = 0; z < vals[x].length; z++) {
				vals[x][z] = clamp(vals[x][z], min, max);
				vals[x][z] -= min;
				vals[x][z] /= divider;
			}
		}
	}

	public static float clamp(float val, float min, float max) {
		if (val < min) {
			return min;
		} else if (val > max) {
			return max;
		}
		return val;
	}
}
