package game.network.event;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import game.network.io.Util;

/**
 * So probably have an event driven system.
 *
 * Events derive from the client and are sent to the server. These are in turn
 * propagated throughout every client.
 *
 * Furthermore, events can propagate other events. For example, a blockbreak
 * event creates an entity drop event. New event creation is always server-side.
 *
 * We should try and design an event-driven client.
 *
 * The reason why we don't use ObjectOutputStream is because that has its own
 * headers to set up, and wouldn't work with the current model of TCP packets
 * that I wrote...
 *
 * @author rober_000
 *
 */
public abstract class Event {
	/**
	 * If a class implements this, it must also have a constructor accepting a byte
	 * array.
	 *
	 * @return A byte representation of the object. That is, <code>new
	 *         Event(event.toBytes())</code> should produce the same object;
	 *
	 */
	public abstract byte[] toBytes();

	/**
	 * This seems overkill.
	 *
	 * @param b
	 * @return
	 * @throws ReflectiveOperationException
	 */
	public static Event fromBytes(byte[] b) throws ReflectiveOperationException {
		int byteHeaderLen = Util.toInt(b, 0);
		Class<?> eventClass = Class.forName(new String(b, 4, byteHeaderLen));

		Constructor<?> cons = eventClass.getConstructor(byte[].class);

		Object o = cons.newInstance(Arrays.copyOfRange(b, 4 + byteHeaderLen, b.length));

		return (Event) o;
	}

	/**
	 * This is the header that tells us what class the Event is. This removes any
	 * need for a massive switch statement!
	 *
	 * Unfortuantely also means we have to use reflection.
	 *
	 * @param c
	 * @return
	 */
	public static byte[] getByteHeader(Class<?> c) {
		String name = c.getName();
		byte[] ret = new byte[4 /* Length of int in bytes */ + name.length()];

		byte[] len = Util.toBytes(name.length());
		for (int i = 0; i < 4; i++) {
			ret[i] = len[i];
		}

		byte[] nameBytes = name.getBytes();
		for (int i = 0; i < name.length(); i++) {
			ret[i + 4] = nameBytes[i];
		}

		return ret;
	}

	/**
	 * You should call this if you want to serialize an object.
	 *
	 * @param e
	 * @return
	 */
	public static byte[] toBytes(Event e) {
		return Util.combine(getByteHeader(e.getClass()), e.toBytes());
	}
}
