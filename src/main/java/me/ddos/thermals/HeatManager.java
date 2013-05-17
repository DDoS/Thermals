package me.ddos.thermals;

import java.util.List;
import me.ddos.thermals.database.HeatLogger;
import me.ddos.thermals.heatmap.HeatColorizer;
import me.ddos.thermals.heatmap.HeatMapGenerator;
import me.ddos.thermals.location.IntLocation;
import me.ddos.thermals.database.HeatDatabase;
import java.util.Timer;
import me.ddos.thermals.location.Heat;
import me.ddos.thermals.database.HeatDatabase.DatabaseConnectionInfo;
import me.ddos.thermals.heatmap.Background;
import me.ddos.thermals.util.ThermalsUtil;

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

	public HeatManager(String databaseType) {
		database = HeatDatabase.newDatabase(databaseType);
		logger = new HeatLogger(database);
		generator = new HeatMapGenerator(database);
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

	public void queueHeatMapTask(IntLocation from, IntLocation to, String name) {
		if (!running) {
			throw new IllegalStateException("Heat manager is not running");
		}
		generator.queueHeatMapTask(from, to, name);
		generator.execute();
	}

	public void incrementHeat(IntLocation location) {
		database.incrementHeat(location);
	}

	public void clearHeat(IntLocation location) {
		database.clearHeat(location);
	}

	public void clearHeats(IntLocation from, IntLocation to) {
		database.clearHeats(ThermalsUtil.getMin(from, to), ThermalsUtil.getMax(from, to));
	}

	public void clearAllHeats() {
		database.clearAllHeats();
	}

	public Heat getHeat(IntLocation location) {
		return database.getHeat(location);
	}

	public List<Heat> getHeats(IntLocation from, IntLocation to) {
		return database.getHeats(ThermalsUtil.getMin(from, to), ThermalsUtil.getMax(from, to));
	}

	public void setHeat(IntLocation location, int heat) {
		database.setHeat(location, heat);
	}

	public void setHeats(IntLocation from, IntLocation to, int heat) {
		database.setHeats(ThermalsUtil.getMin(from, to), ThermalsUtil.getMax(from, to), heat);
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

	public void setGeneratorBackground(Background background) {
		generator.setBackground(background);
	}
}
