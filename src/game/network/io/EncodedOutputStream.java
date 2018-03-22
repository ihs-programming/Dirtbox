package game.network.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Robert on 8/5/2017.
 */
public class EncodedOutputStream {
	private OutputStream out;

	public EncodedOutputStream(OutputStream out) {
		this.out = out;
	}

	public void writeWarn(Header h, byte[]... data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(Util.toBytes(h.ordinal()));
		int tot = 0;
		for (byte[] aData : data) {
			tot += aData.length;
		}
		out.write(Util.toBytes(tot));

		for (byte[] aData : data) {
			out.write(aData);
		}

		this.out.write(out.toByteArray());

	}

	public void write(Header h, byte[]... data) {
		try {
			writeWarn(h, data);
		} catch (IOException ignored) {
		}
	}
}
