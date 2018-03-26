package game.utils;

import org.dyn4j.DataContainer;

public class SingleValueContainer<T> implements DataContainer {

	private T elem;

	public SingleValueContainer(T elem) {
		this.elem = elem;
	}

	@Override
	public void setUserData(Object data) {
		elem = (T) data;
	}

	@Override
	public Object getUserData() {
		return elem;
	}

}
