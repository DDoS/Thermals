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
			+ "(id BIGINT PRIMARY KEY, "
			+ "x INT NOT NULL, "
			+ "z INT NOT NULL, "
			+ "heat INT UNSIGNED NOT NULL)";
	private static final String INCREMENT_HEAT =
			"INSERT INTO " + TABLE_NAME + " "
			+ "(id, x, z, heat) VALUES (?, ?, ?, 1) "
			+ "ON DUPLICATE KEY UPDATE heat = heat + 1";
	private static final String DELETE_ALL_HEATS =
			"DELETE FROM " + TABLE_NAME;
	private static final String DELETE_HEAT =
			"DELETE FROM " + TABLE_NAME + " "
			+ "WHERE x = ? AND z = ?";
	private static final String DELETE_HEATS =
			"DELETE FROM " + TABLE_NAME + " "
			+ "WHERE x >= ? AND z >= ? AND x <= ? and z <= ?";
	private static final String RETRIEVE_HEAT =
			"SELECT heat FROM " + TABLE_NAME + " "
			+ "WHERE x = ? AND z = ?";
	private static final String RETRIEVE_HEATS =
			"SELECT x, z, heat FROM " + TABLE_NAME + " "
			+ "WHERE x >= ? AND z >= ? AND x <= ? and z <= ?";
	private static final String SET_HEAT =
			"REPLACE INTO " + TABLE_NAME + " "
			+ "(id, x, z, heat) VALUES (?, ?, ?, ?)";
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
		verifyTable();
	}

	private void verifyTable() {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(CREATE_TABLE);
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't create MySQL table '" + TABLE_NAME + "': " + ex.getMessage());
		} finally {
			closeStatement(statement);
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
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(INCREMENT_HEAT);
			statement.setLong(1, location.getID());
			statement.setInt(2, location.getX());
			statement.setInt(3, location.getZ());
			statement.executeUpdate();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't update location heat in MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
	}

	@Override
	public void clearHeat(IntLocation location) {
		if (connection == null) {
			return;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(DELETE_HEAT);
			statement.setInt(1, location.getX());
			statement.setInt(2, location.getZ());
			statement.executeUpdate();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't clear heat in MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
	}

	@Override
	public void clearHeats(IntLocation min, IntLocation max) {
		if (connection == null) {
			return;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(DELETE_HEATS);
			statement.setInt(1, min.getX());
			statement.setInt(2, min.getZ());
			statement.setInt(3, max.getX());
			statement.setInt(4, max.getZ());
			statement.executeUpdate();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't clear heats in MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
	}

	@Override
	public void clearAllHeats() {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(DELETE_ALL_HEATS);
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't clear all heats in MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
	}

	@Override
	public Heat getHeat(IntLocation location) {
		if (connection == null) {
			return new Heat(location);
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(RETRIEVE_HEAT);
			statement.setInt(1, location.getX());
			statement.setInt(2, location.getZ());
			final ResultSet results = statement.executeQuery();
			results.next();
			return new Heat(location, results.getInt("heat"));
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't retrieve heat from MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
		return new Heat(location);
	}

	@Override
	public List<Heat> getHeats(IntLocation min, IntLocation max) {
		final List<Heat> heats = new ArrayList<Heat>();
		if (connection == null) {
			return heats;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(RETRIEVE_HEATS);
			statement.setInt(1, min.getX());
			statement.setInt(2, min.getZ());
			statement.setInt(3, max.getX());
			statement.setInt(4, max.getZ());
			final ResultSet results = statement.executeQuery();
			while (results.next()) {
				heats.add(new Heat(results.getInt("x"), results.getInt("z"), results.getInt("heat")));
			}
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't retrieve heats from MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
		return heats;
	}

	@Override
	public void setHeat(IntLocation location, int heat) {
		if (connection == null) {
			return;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(SET_HEAT);
			statement.setLong(1, location.getID());
			statement.setInt(2, location.getX());
			statement.setInt(3, location.getZ());
			statement.setInt(4, heat);
			statement.executeUpdate();
		} catch (SQLException ex) {
			ThermalsPlugin.logSevere("Couldn't set heat in MySQL table: " + ex.getMessage());
		} finally {
			closeStatement(statement);
		}
	}

	@Override
	public void setHeats(IntLocation min, IntLocation max, int heat) {
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int z = min.getZ(); z <= max.getZ(); z++) {
				setHeat(new IntLocation(x, z), heat);
			}
		}
	}

	private static void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException ex) {
				ThermalsPlugin.logSevere("Couldn't close MySQL statement: " + ex.getMessage());
			}
		}
	}
}
