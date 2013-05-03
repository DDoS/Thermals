package me.ddos.thermals;

import me.ddos.thermals.database.HeatLogger;
import me.ddos.thermals.heatmap.HeatColorizer;
import me.ddos.thermals.heatmap.HeatMapGenerator;
import me.ddos.thermals.data.IntLocation;
import me.ddos.thermals.database.HeatDatabase;
import java.util.Timer;
import me.ddos.thermals.database.HeatDatabase.DatabaseConnectionInfo;

/**
 *
 * @author DDoS
 */
public class HeatManager {
	private boolean running = false;
	private final HeatDatabase database;
	private final HeatLogger logger;
	private final Timer timer = new Timer();
	private long loggerDelay = 30000;
	private HeatMapGenerator generator;
	private final Object generatorWait = new Object();

	public HeatManager(String databaseType) {
		database = HeatDatabase.newDatabase(databaseType);
		logger = new HeatLogger(database);
		generator = new HeatMapGenerator(database, generatorWait);
	}

	public void start() {
		if (running) {
			throw new IllegalStateException("Heat manager is already running");
		}
		if (loggerDelay <= 0) {
			throw new IllegalStateException("consumerDelay must be greater than zero");
		}
		database.connect();
		timer.scheduleAtFixedRate(logger, loggerDelay, loggerDelay);
		generator.start();
		running = true;
	}

	public void stop() {
		if (!running) {
			throw new IllegalStateException("Heat manager is not running");
		}
		timer.cancel();
		generator.end();
		generator = new HeatMapGenerator(database, generatorWait);
		database.disconnect();
		running = false;
	}

	public void queueHeatIncrement(int x, int z) {
		queueHeatIncrement(new IntLocation(x, z));
	}

	public void queueHeatIncrement(IntLocation location) {
		if (!running) {
			throw new IllegalStateException("Heat manager is not running");
		}
		logger.queueHeatIncrement(location);
	}

	public void queueHeatMapTask(IntLocation start, IntLocation end, String name) {
		if (!running) {
			throw new IllegalStateException("Heat manager is not running");
		}
		generator.queueHeatMapTask(start, end, name);
		synchronized (generatorWait) {
			generatorWait.notifyAll();
		}
	}

	public void setDatabaseInfo(DatabaseConnectionInfo info) {
		database.setInfo(info);
	}

	public void setLoggerDelay(long loggerDelay) {
		this.loggerDelay = loggerDelay;
	}

	public void setLoggerRunThreshold(int threshold) {
		logger.setRunThreshold(threshold);
	}

	public void shouldLoggerSupressInfo(boolean supressInfo) {
		logger.shouldSuppressInfo(supressInfo);
	}

	public void setMinHeat(int min) {
		generator.setMinHeat(min);
	}

	public void setMaxHeat(int max) {
		generator.setMaxHeat(max);
	}

	public void setGeneratorColorizer(HeatColorizer colorizer) {
		generator.setColorizer(colorizer);
	}
}
