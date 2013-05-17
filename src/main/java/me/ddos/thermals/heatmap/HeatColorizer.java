package me.ddos.thermals.heatmap;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.ddos.thermals.util.ThermalsUtil;
import org.bukkit.Color;

/**
 *
 * @author DDoS
 */
public class HeatColorizer {
	public static final List<Color> THERMOGRAPHIC_GRADIENT =
			Collections.unmodifiableList(Lists.newArrayList(
			new Color[]{
				Color.fromRGB(0, 0, 0),
				Color.fromRGB(50, 0, 150),
				Color.fromRGB(200, 15, 140),
				Color.fromRGB(250, 120, 0),
				Color.fromRGB(250, 200, 0),
				Color.fromRGB(255, 255, 255)
			}));
	private final Color[] gradient;

	public HeatColorizer() {
		this(THERMOGRAPHIC_GRADIENT);
	}

	public HeatColorizer(Collection<Color> gradient) {
		this(gradient.toArray(new Color[gradient.size()]));
	}

	public HeatColorizer(Color... gradient) {
		if (gradient.length < 2) {
			throw new IllegalArgumentException("Gradient size must be at least 2, got " + gradient.length);
		} else {
			this.gradient = gradient.clone();
		}
	}

	public void colorizeTo(float[][] heatMap, int[] destination, int sizeX, int sizeZ) {
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				if (heatMap[xx][zz] > 0) {
					final float gradientIndexHigh = heatMap[xx][zz] * (gradient.length - 1);
					final int gradientIndexLow = (int) Math.floor(gradientIndexHigh);
					final Color colorLow = gradient[gradientIndexLow];
					final Color colorHigh = gradient[gradientIndexLow + 1];
					final float percent = gradientIndexHigh - gradientIndexLow;
					final int red = lerp(colorLow.getRed(), colorHigh.getRed(), percent);
					final int green = lerp(colorLow.getGreen(), colorHigh.getGreen(), percent);
					final int blue = lerp(colorLow.getBlue(), colorHigh.getBlue(), percent);
					destination[xx + zz * sizeX] =
							ThermalsUtil.OPAQUE_ALPHA | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
				}
			}
		}
	}

	private static int lerp(float a, float b, float percent) {
		return (int) ((1 - percent) * a + percent * b);
	}
}
