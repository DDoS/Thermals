package me.ddos.thermals.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import me.ddos.thermals.data.Heat;
import me.ddos.thermals.data.IntLocation;
import me.ddos.thermals.ThermalsPlugin;

/**
 *
 * @author DDoS
 */
public class MySQLHeatDatabase extends HeatDatabase {
	private static final String TABLE_NAME = "thermals_heats";
	private static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " "
			+ "(id INT PRIMARY KEY, "
			+ "x INT NOT NULL, "
			+ "z INT NOT NULL, "
			+ "heat INT NOT NULL)";
	private static final String INCREMENT_HEAT =
			"INSERT INTO " + TABLE_NAME + " "
			+ "(id, x, z, heat) VALUES (?, ?, ?, 1) "
			+ "ON DUPLICATE KEY UPDATE heat = heat + 1";
	private static final String RETRIEVE_HEATS =
			"SELECT x, z, heat "
			+ "FROM " + TABLE_NAME + " "
			+ "WHERE x >= ? AND z >= ? AND x <= ? and z <= ?";
	private Connection connection;

	static {
		HeatDatabase.register("MySQL", MySQLHeatDatabase.class);
	}

	@Override
	public void connect() {
		super.connect();
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, user, password);
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't connect to MySQL server: " + ex.getMessage());
		}
		if (connection == null) {
			return;
		}
		ThermalsPlugin.logInfo("Connection to MySQL server was established");
		try {
			verifyTable();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't close MySQL statement: " + ex.getMessage());
		}
	}

	private void verifyTable() throws SQLException {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(CREATE_TABLE);
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't create MySQL table '" + TABLE_NAME + "': " + ex.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	@Override
	public boolean hasConnection() {
		return connection != null;
	}

	@Override
	public void disconnect() {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
			ThermalsPlugin.logInfo("Disconnected from MySQL server successfully");
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't close connection to MySQL server: " + ex.getMessage());
		}
	}

	@Override
	public void incrementHeat(IntLocation location) {
		if (connection == null) {
			return;
		}
		PreparedStatement update = null;
		try {
			update = connection.prepareStatement(INCREMENT_HEAT);
			update.setInt(1, location.hashCode());
			update.setInt(2, location.getX());
			update.setInt(3, location.getZ());
			update.executeUpdate();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't update location heat in MySQL server: " + ex.getMessage());
		} finally {
			if (update != null) {
				try {
					update.close();
				} catch (SQLException ex) {
					ThermalsPlugin.logSevere("Couldn't close MySQL statement: " + ex.getMessage());
				}
			}
		}
	}

	@Override
	public List<Heat> getHeats(IntLocation start, IntLocation end) {
		final List<Heat> heats = new ArrayList<Heat>();
		if (connection == null) {
			return heats;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(RETRIEVE_HEATS);
			statement.setInt(1, start.getX());
			statement.setInt(2, start.getZ());
			statement.setInt(3, end.getX());
			statement.setInt(4, end.getZ());
			final ResultSet results = statement.executeQuery();
			while (results.next()) {
				heats.add(new Heat(results.getInt("x"), results.getInt("z"), results.getInt("heat")));
			}
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't retrieve heats from MySQL server: " + ex.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ThermalsPlugin.logSevere("Couldn't close MySQL statement: " + ex.getMessage());
				}
			}
		}
		return heats;
	}
}
