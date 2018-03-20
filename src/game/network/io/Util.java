package game.network.io;

/**
 * Created by Robert on 8/5/2017.
 */
public class Util {
	public static byte[] toBytes(int n) {
		byte[] ret = new byte[4];

		if (n < 0) {
			n -= Integer.MIN_VALUE;
			ret[3] = -128;
		}

		for (int i = 0; i < 4; i++) {
			ret[i] += (byte) (n % 256);
			n /= 256;
		}

		return ret;
	}

	public static int toInt(byte[] b, int off) {
		return toInt(new byte[] { b[off], b[off + 1], b[off + 2], b[off + 3] });
	}

	public static int toInt(byte[] b) {
		assert b.length == 4;

		int ret = 0;
		for (int i = 0; i < 3; i++) {
			ret += (int) Math.pow(256, i) * (b[i] < 0 ? b[i] + 256 : b[i]);
		}
		ret += (int) Math.pow(256, 3) * b[3];

		return ret;
	}

	public static byte[] combine(byte[] a, byte[] b) {
		byte[] ret = new byte[a.length + b.length];

		System.arraycopy(a, 0, ret, 0, a.length);
		System.arraycopy(b, 0, ret, a.length, b.length);

		return ret;
	}

	public static int bound(int min, int num, int max) {
		return Math.max(Math.min(num, max), min);
	}
}
