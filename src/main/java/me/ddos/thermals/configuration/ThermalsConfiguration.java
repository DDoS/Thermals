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
	@Setting("database.type")
	public String databaseType = "MySQL";
	@Setting("database.host")
	public String databaseHost = "localhost";
	@Setting("database.port")
	public String databasePort = "3306";
	@Setting("database.name")
	public String databaseName = "minecraft";
	@Setting("database.user")
	public String databaseUser = "root";
	@Setting("database.password")
	public String databasePassword = "pass";
	@Setting("logger.delay")
	public long loggerDelay = 30000;
	@Setting("logger.run-threshold")
	public int loggerRunThreshold = -1;
	@Setting("logger.suppress-info")
	public boolean loggerSuppressInfo = false;
	@Setting("generator.heat-bounds.min")
	public int generatorMinHeat = 0;
	@Setting("generator.heat-bounds.max")
	public int generatorMaxHeat = 5000;
	@Setting("generator.heat-gradient")
	public List<Color> heatGradient = HeatColorizer.THERMOGRAPHIC_GRADIENT;
	@Setting("generator.background.color")
	public Color backgroundColor = Color.fromRGB(150, 150, 150);
	@Setting("generator.background.grid.draw")
	public boolean drawGrid = true;
	@Setting("generator.background.grid.block-interval")
	public int gridLineInterval = 16;
	@Setting("generator.background.grid.line-color")
	public Color gridLineColor = Color.fromRGB(0, 255, 0);
	@Setting("generator.background.grid.coords.draw")
	public boolean drawGridCoords = true;
	@Setting("generator.background.grid.coords.grid-line-interval")
	public int coordsGridLineInterval = 3;
	@Setting("generator.background.grid.coords.point-color")
	public Color coordsPointColor = Color.fromRGB(255, 0, 0);
	@Setting("generator.background.grid.coords.font.color")
	public Color coordsFontColor = Color.fromRGB(0, 0, 0);
	@Setting("generator.background.grid.coords.font.name")
	public String coordsFontName = "Myriad Pro";
	@Setting("generator.background.grid.coords.font.size")
	public int coordsFontSize = 10;
}
