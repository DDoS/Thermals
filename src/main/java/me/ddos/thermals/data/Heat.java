package me.ddos.thermals.data;

/**
 *
 * @author DDoS
 */
public class Heat extends IntLocation {
	private final int heat;

	public Heat(int x, int z, int heat) {
		super(x, z);
		this.heat = heat;
	}

	public int getHeat() {
		return heat;
	}
}
