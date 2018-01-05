package game;

public enum BlockType {
	Dirt(2, 0), Stone(1, 0), Wood(4, 1);
	
	int sx;
	int sy;
	
	private BlockType(int x, int y) {
		sx = x;
		sy = y;
	}
}
