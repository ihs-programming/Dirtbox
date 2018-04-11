package game.utils;

public class BodyData {
	private BodyType type;

	public BodyData(BodyType type) {
		this.type = type;
	}
	
	public BodyType getType() {
		return type;
	}

	public boolean compareType(BodyData b) {
		return type.compare(b.getType());
	}
}
