package me.ddos.thermals.data;

import org.bukkit.Location;

/**
 *
 * @author DDoS
 */
public class Heat extends IntLocation {
	private final int heat;

	public Heat(Location location) {
		this(location, 0);
	}

	public Heat(Location location, int heat) {
		super(location);
		this.heat = heat;
	}

	public Heat(IntLocation location) {
		this(location, 0);
	}

	public Heat(IntLocation location, int heat) {
		super(location);
		this.heat = heat;
	}

	public Heat(int x, int z) {
		this(x, z, 0);
	}

	public Heat(int x, int z, int heat) {
		super(x, z);
		this.heat = heat;
	}

	public int getHeat() {
		return heat;
	}
}
