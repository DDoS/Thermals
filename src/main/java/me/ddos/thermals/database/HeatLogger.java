package me.ddos.thermals.database;

import me.ddos.thermals.data.IntLocation;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.ddos.thermals.ThermalsPlugin;

/**
 *
 * @author DDoS
 */
public class HeatLogger extends TimerTask {
	private final Queue<IntLocation> queue = new ConcurrentLinkedQueue<IntLocation>();
	private final HeatDatabase database;
	private final AtomicInteger runThreshold = new AtomicInteger(-1);
	private final AtomicBoolean infoSuppressed = new AtomicBoolean(false);

	public HeatLogger(HeatDatabase database) {
		this.database = database;
	}

	@Override
	public void run() {
		final int threshold = runThreshold.get();
		int count = 0;
		while (!queue.isEmpty()
				&& (threshold < 0 || count < threshold)) {
			database.incrementHeat(queue.poll());
			count++;
		}
		if (!infoSuppressed.get()) {
			ThermalsPlugin.logInfo("Logged " + count + " heat increments, " + queue.size() + " remaining");
		}
	}

	public void queueHeatIncrement(int x, int z) {
		queueHeatIncrement(new IntLocation(x, z));
	}

	public void queueHeatIncrement(IntLocation location) {
		queue.add(location);
	}

	public void setRunThreshold(int threshold) {
		runThreshold.set(threshold);
	}

	public void shouldSuppressInfo(boolean suppressInfo) {
		infoSuppressed.set(suppressInfo);
	}
}
