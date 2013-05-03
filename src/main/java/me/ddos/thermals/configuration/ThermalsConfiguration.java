package me.ddos.thermals.configuration;

import java.util.List;
import me.ddos.thermals.configuration.AnnotatedConfiguration.Setting;
import me.ddos.thermals.heatmap.HeatColorizer;
import org.bukkit.Color;

/**
 *
 * @author DDoS
 */
public class ThermalsConfiguration {
	@Setting({"database", "type"})
	public String databaseType = "MySQL";
	@Setting({"database", "host"})
	public String databaseHost = "localhost";
	@Setting({"database", "port"})
	public String databasePort = "3306";
	@Setting({"database", "name"})
	public String databaseName = "minecraft";
	@Setting({"database", "user"})
	public String databaseUser = "root";
	@Setting({"database", "password"})
	public String databasePassword = "pass";
	@Setting({"logger", "delay"})
	public long loggerDelay = 30000;
	@Setting({"logger", "run-threshold"})
	public int loggerRunThreshold = -1;
	@Setting({"logger", "suppress-info"})
	public boolean loggerSuppressInfo = false;
	@Setting({"generator", "heat-bounds", "min"})
	public int generatorMinHeat = 0;
	@Setting({"generator", "heat-bounds", "max"})
	public int generatorMaxHeat = 5000;
	@Setting({"generator", "heat-gradient"})
	public List<Color> heatGradient = HeatColorizer.THERMOGRAPHIC_GRADIENT;
}
