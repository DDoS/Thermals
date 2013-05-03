package me.ddos.thermals.data;

/**
 *
 * @author DDoS
 */
public class IntLocation {
	private final int x;
	private final int z;

	public IntLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntLocation)) {
			return false;
		}
		final IntLocation other = (IntLocation) obj;
		if (x != other.x) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + x;
		hash = 37 * hash + z;
		return hash;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + z + ")";
	}
}
