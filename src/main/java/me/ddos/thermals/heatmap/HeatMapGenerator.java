package me.ddos.thermals.heatmap;

import me.ddos.thermals.data.Heat;
import me.ddos.thermals.data.IntLocation;
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
	private final Object wait;
	private final AtomicInteger minHeat = new AtomicInteger(0);
	private final AtomicInteger maxHeat = new AtomicInteger(5000);
	private final AtomicReference<HeatColorizer> colorizer = new AtomicReference<HeatColorizer>();
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final Queue<HeatMapTaskData> tasks = new ConcurrentLinkedQueue<HeatMapTaskData>();

	public HeatMapGenerator(HeatDatabase database, Object wait) {
		this.database = database;
		this.wait = wait;
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
					final IntLocation start = task.getStart();
					final IntLocation end = task.getEnd();
					final int startX = start.getX();
					final int startZ = start.getZ();
					final int endX = end.getZ();
					final int endZ = end.getZ();
					final int sizeX = endX - startX + 1;
					final int sizeZ = endZ - startZ + 1;
					final List<Heat> heats = database.getHeats(start, end);
					final float[][] heatMap = new float[sizeX][sizeZ];
					for (Heat heat : heats) {
						heatMap[heat.getX() - startX][heat.getZ() - startZ] = heat.getHeat();
					}
					ThermalsUtil.normalize(heatMap, minHeat.get(), maxHeat.get());
					final int[] colors = colorizer.get().colorize(heatMap);
					final BufferedImage image = new BufferedImage(sizeX, sizeZ, BufferedImage.TYPE_INT_ARGB);
					final int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
					System.arraycopy(colors, 0, data, 0, colors.length);
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

	public void queueHeatMapTask(IntLocation start, IntLocation end, String fileName) {
		tasks.add(new HeatMapTaskData(start, end, fileName));
	}

	public Object getWait() {
		return wait;
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

	public void setColorizer(HeatColorizer colorizer) {
		this.colorizer.set(colorizer);
	}

	private static class HeatMapTaskData {
		private final IntLocation start;
		private final IntLocation end;
		private final String fileName;

		private HeatMapTaskData(IntLocation start, IntLocation end, String fileName) {
			this.start = start;
			this.end = end;
			this.fileName = fileName;
		}

		private IntLocation getStart() {
			return start;
		}

		private IntLocation getEnd() {
			return end;
		}

		private String getFileName() {
			return fileName;
		}
	}
}
