package me.ddos.thermals.heatmap;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import me.ddos.thermals.util.ThermalsUtil;
import org.bukkit.Color;

/**
 *
 * @author DDoS
 */
public class Background {
	private int backgroundColor = 0xff969696;
	private boolean drawGrid = true;
	private int gridLineInterval = 16;
	private int gridLineColor = 0xff00ff00;
	private boolean drawGridCoords = true;
	private int coordsGridLineInterval = 3;
	private int coordsPointColor = 0xffff0000;
	private Color coordsFontColor = Color.fromRGB(0, 0, 0);
	private Font coordsFont = new Font("Myriad Pro", Font.PLAIN, 10);

	public void createBackground(BufferedImage image, int startX, int startZ) {
		final int[] colors = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		final int sizeX = image.getWidth();
		final int sizeZ = image.getHeight();
		Arrays.fill(colors, backgroundColor);
		if (!drawGrid) {
			return;
		}
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				if ((startX + xx) % gridLineInterval == 0 || (startZ + zz) % gridLineInterval == 0) {
					colors[xx + zz * sizeX] = gridLineColor;
				}
			}
		}
		if (!drawGridCoords) {
			return;
		}
		final Graphics graphics = image.getGraphics();
		graphics.setFont(coordsFont);
		graphics.setColor(new java.awt.Color(coordsFontColor.getRed(), coordsFontColor.getGreen(), coordsFontColor.getBlue()));
		final int coordsInterval = gridLineInterval * coordsGridLineInterval;
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				final int x = startX + xx;
				final int z = startZ + zz;
				if (x % coordsInterval == 0 && z % coordsInterval == 0) {
					colors[xx + zz * sizeX] = coordsPointColor;
					graphics.drawString(x + "," + z, xx + 1, zz - 1);
				}
			}
		}
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = ThermalsUtil.OPAQUE_ALPHA | backgroundColor.asRGB();
	}

	public void drawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}

	public void setGridLineInterval(int gridLineInterval) {
		this.gridLineInterval = gridLineInterval;
	}

	public void setGridLineColor(Color gridLineColor) {
		this.gridLineColor = ThermalsUtil.OPAQUE_ALPHA | gridLineColor.asRGB();
	}

	public void drawGridCoords(boolean drawGridCoords) {
		this.drawGridCoords = drawGridCoords;
	}

	public void setCoordsGridLineInterval(int coordsGridLineInterval) {
		this.coordsGridLineInterval = coordsGridLineInterval;
	}

	public void setCoordsPointColor(Color coordsPointColor) {
		this.coordsPointColor = ThermalsUtil.OPAQUE_ALPHA | coordsPointColor.asRGB();
	}

	public void setCoordsFontColor(Color coordsFontColor) {
		this.coordsFontColor = coordsFontColor;
	}

	public void setCoordsFont(Font coordsFont) {
		this.coordsFont = coordsFont;
	}
}
