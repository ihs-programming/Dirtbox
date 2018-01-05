package game;

public enum BlockType {
	// Align the blocks like this to help git with version control
	// 	 	Git keeps track of what lines change, so keeping blocks on different
	// 	 	lines should reduce merge conflicts)
	// 		
	//		Notice also that the blocks are alphabetically ordered...
	DIRT(2, 0),
	EMPTY(1, 1),
	GOLD(0, 2),
	GRAVEL(0, 0),
	GRASS(3, 0),
	STONE(1, 0),
	UNDEFINED(10, 1),
	WOOD(4, 1);
	
	int sx;
	int sy;
	
	private BlockType(int x, int y) {
		sx = x;
		sy = y;
	}
}
