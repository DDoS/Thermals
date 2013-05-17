package me.ddos.thermals.heatmap;

import me.ddos.thermals.location.Heat;
import me.ddos.thermals.location.IntLocation;
import me.ddos.thermals.util.ThermalsUtil;
import me.ddos.thermals.database.HeatDatabase;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import me.ddos.thermals.ThermalsPlugin;

/**
 *
 * @author DDoS
 */
public class HeatMapGenerator extends Thread {
	private final HeatDatabase database;
	private final Object wait = new Object();
	private final AtomicInteger minHeat = new AtomicInteger(0);
	private final AtomicInteger maxHeat = new AtomicInteger(5000);
	private final AtomicReference<Background> background = new AtomicReference<Background>();
	private final AtomicReference<HeatColorizer> colorizer = new AtomicReference<HeatColorizer>();
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final Queue<HeatMapTaskData> tasks = new ConcurrentLinkedQueue<HeatMapTaskData>();

	public HeatMapGenerator(HeatDatabase database) {
		this.database = database;
	}

	@Override
	public void run() {
		ThermalsPlugin.logInfo("Generator started");
		while (running.get()) {
			try {
				synchronized (wait) {
					ThermalsPlugin.logInfo("Generator going to sleep");
					wait.wait();
				}
				ThermalsPlugin.logInfo("Generator working");
				while (!tasks.isEmpty()) {
					final HeatMapTaskData task = tasks.poll();
					final IntLocation min = task.getMin();
					final IntLocation max = task.getMax();
					final int minX = min.getX();
					final int minZ = min.getZ();
					final int maxX = max.getX();
					final int maxZ = max.getZ();
					final int sizeX = maxX - minX + 1;
					final int sizeZ = maxZ - minZ + 1;
					final float[][] heatMap = new float[sizeX][sizeZ];
					final List<Heat> heats = database.getHeats(min, max);
					for (Heat heat : heats) {
						heatMap[heat.getX() - minX][heat.getZ() - minZ] = heat.getHeat();
					}
					ThermalsUtil.normalize(heatMap, minHeat.get(), maxHeat.get());
					final BufferedImage image = new BufferedImage(sizeX, sizeZ, BufferedImage.TYPE_INT_ARGB);
					final int[] colors = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
					background.get().createBackground(image, minX, minZ);
					colorizer.get().colorizeTo(heatMap, colors, sizeX, sizeZ);
					final File file = new File(ThermalsPlugin.PLUGIN_DIR, task.getFileName() + ".png");
					if (file.exists()) {
						file.delete();
					}
					ImageIO.write(image, "PNG", file);
				}
			} catch (Exception ex) {
				ThermalsPlugin.logSevere("The heat map generator thread encountered an error: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		ThermalsPlugin.logInfo("Generator shutting down");
	}

	public void queueHeatMapTask(IntLocation from, IntLocation to, String fileName) {
		tasks.add(new HeatMapTaskData(ThermalsUtil.getMin(from, to), ThermalsUtil.getMax(from, to), fileName));
	}

	public void execute() {
		synchronized (wait) {
			wait.notifyAll();
		}
	}

	public void end() {
		tasks.clear();
		running.set(false);
		synchronized (wait) {
			wait.notifyAll();
		}
	}

	public void setMinHeat(int minHeat) {
		this.minHeat.set(minHeat);
	}

	public void setMaxHeat(int maxHeat) {
		this.maxHeat.set(maxHeat);
	}

	public void setBackground(Background background) {
		this.background.set(background);
	}

	public void setColorizer(HeatColorizer colorizer) {
		this.colorizer.set(colorizer);
	}

	private static class HeatMapTaskData {
		private final IntLocation min;
		private final IntLocation max;
		private final String fileName;

		private HeatMapTaskData(IntLocation min, IntLocation max, String fileName) {
			this.min = min;
			this.max = max;
			this.fileName = fileName;
		}

		private IntLocation getMin() {
			return min;
		}

		private IntLocation getMax() {
			return max;
		}

		private String getFileName() {
			return fileName;
		}
	}
}
