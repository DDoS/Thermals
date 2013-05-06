package me.ddos.thermals.util;

import me.ddos.thermals.data.IntLocation;

/**
 *
 * @author DDoS
 */
public class ThermalsUtil {
	public static boolean isInt(String exp) {
		try {
			Integer.parseInt(exp);
		} catch (Exception ex) {
			return false;
		}
		return true;
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

	public static IntLocation getMin(IntLocation a, IntLocation b) {
		return new IntLocation(Math.min(a.getX(), b.getX()), Math.min(a.getZ(), b.getZ()));
	}

	public static IntLocation getMax(IntLocation a, IntLocation b) {
		return new IntLocation(Math.max(a.getX(), b.getX()), Math.max(a.getZ(), b.getZ()));
	}
}
