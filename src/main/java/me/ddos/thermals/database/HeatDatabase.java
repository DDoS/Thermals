package me.ddos.thermals.database;

import java.util.List;
import me.ddos.thermals.location.Heat;
import me.ddos.thermals.location.IntLocation;
import me.ddos.thermals.util.TypeFactory;

/**
 *
 * @author DDoS
 */
public abstract class HeatDatabase {
	private static final TypeFactory<HeatDatabase> DATABASES = new TypeFactory<HeatDatabase>();
	protected String host;
	protected String port;
	protected String databaseName;
	protected String user;
	protected String password;

	public boolean connect() {
		if (host == null || port == null || databaseName == null || user == null || password == null) {
			return false;
		}
		return true;
	}

	public abstract void disconnect();

	public abstract boolean hasConnection();

	public abstract void incrementHeat(IntLocation location);

	public abstract void clearHeat(IntLocation location);

	public abstract void clearHeats(IntLocation min, IntLocation max);

	public abstract void clearAllHeats();

	public abstract Heat getHeat(IntLocation location);

	public abstract List<Heat> getHeats(IntLocation min, IntLocation max);

	public abstract void setHeat(IntLocation location, int heat);

	public abstract void setHeats(IntLocation min, IntLocation max, int heat);

	public void setInfo(DatabaseConnectionInfo info) {
		setHost(info.getHost());
		setPort(info.getPort());
		setDatabaseName(info.getDatabaseName());
		setUser(info.getUser());
		setPassword(info.getPassword());
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void register(String type, Class<? extends HeatDatabase> shape) {
		DATABASES.register(type.toLowerCase(), shape);
	}

	public static HeatDatabase newDatabase(String type) {
		return DATABASES.newInstance(type.toLowerCase());
	}

	public static class DatabaseConnectionInfo {
		private String host;
		private String port;
		private String databaseName;
		private String user;
		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
