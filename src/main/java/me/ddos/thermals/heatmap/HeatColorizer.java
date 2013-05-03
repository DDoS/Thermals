package me.ddos.thermals.heatmap;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Color;

/**
 *
 * @author DDoS
 */
public class HeatColorizer {
	private static final int OPAQUE_ALPHA = 0xff000000;
	private static final int COLORLESS = 0x00ffffff;
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

	public int[] colorize(float[][] heatMap) {
		final int length = heatMap.length;
		final int width = heatMap[0].length;
		final int[] colors = new int[length * width];
		for (int x = 0; x < length; x++) {
			for (int z = 0; z < width; z++) {
				if (heatMap[x][z] > 0) {
					final float gradientIndexHigh = heatMap[x][z] * (gradient.length - 1);
					final int gradientIndexLow = (int) Math.floor(gradientIndexHigh);
					final Color colorLow = gradient[gradientIndexLow];
					final Color colorHigh = gradient[gradientIndexLow + 1];
					final float percent = gradientIndexHigh - gradientIndexLow;
					final int red = lerp(colorLow.getRed(), colorHigh.getRed(), percent);
					final int green = lerp(colorLow.getGreen(), colorHigh.getGreen(), percent);
					final int blue = lerp(colorLow.getBlue(), colorHigh.getBlue(), percent);
					colors[x + z * length] = OPAQUE_ALPHA | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
				} else {
					colors[x + z * length] = COLORLESS;
				}
			}
		}
		return colors;
	}

	private static int lerp(float a, float b, float percent) {
		return (int) ((1 - percent) * a + percent * b);
	}
}
