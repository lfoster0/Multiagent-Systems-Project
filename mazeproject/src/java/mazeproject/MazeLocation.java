package mazeproject;

import jason.environment.grid.Location;

public class MazeLocation {

	
	// Codes for different objects
	public static final int EMTPY = 16;
	public static final int WALL = 32;
	public static final int START = 64;
	
	public static final int GOAL0 = 128;
	public static final int GOAL1 = 256;
	public static final int GOAL2 = 512;
	public static final int GOAL3 = 1024;
	
	private int type;
	private int x;
	private int y;
	
	public MazeLocation (Location l, int type) {
		this.x = l.x;
		this.y = l.y;
		this.type = type;
	}
	
	public MazeLocation (int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public int getTypeInt() {
		return type;
	}
	
	public String getTypeString() {
		switch (this.type) {
			case MazeLocation.EMTPY: return "empty";
			case MazeLocation.WALL: return "wall"; 
			case MazeLocation.START: return "start"; 
			case MazeLocation.GOAL0: return "goal0"; 
			case MazeLocation.GOAL1: return "goal1";
			case MazeLocation.GOAL2: return "goal2";
			case MazeLocation.GOAL3: return "goal3";
		}
		
		return "error";
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MazeLocation other = (MazeLocation) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "X: " + this.x + " Y: " + this.y + " TILE: " + this.type;
	}
	
	public MazeLocation copy() {
		return new MazeLocation(this.x, this.y, this.type);
	}
}
