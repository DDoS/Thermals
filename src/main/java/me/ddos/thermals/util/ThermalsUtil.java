package me.ddos.thermals.util;

import me.ddos.thermals.location.IntLocation;

/**
 *
 * @author DDoS
 */
public class ThermalsUtil {
	public static final int OPAQUE_ALPHA = 0xff000000;

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
		for (int xx = 0; xx < vals.length; xx++) {
			for (int zz = 0; zz < vals[xx].length; zz++) {
				vals[xx][zz] = clamp(vals[xx][zz], min, max);
				vals[xx][zz] -= min;
				vals[xx][zz] /= divider;
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
