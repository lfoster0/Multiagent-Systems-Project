package mazeproject;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.stdlib.findall;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class MazeModel extends GridWorldModel {

	// Window Size
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	
	public static final int AG0 = 0;
	public static final int AG1 = 1;
	public static final int AG2 = 2;
	public static final int AG3 = 3;
	
	// Logger init
    private Logger logger = Logger.getLogger("mazeproject.mas2j." + MazeModel.class.getName());
    
    public ArrayList<MazeAgent> agents = new ArrayList<MazeAgent>();
    
    public MazeModel() {
        super(WIDTH, HEIGHT, 4);
        
        // Set Empty squares
        for (int i = 0; i < WIDTH; i++) {
        	for (int j = 0; j < HEIGHT; j++) {
        		set(MazeLocation.EMTPY, i, j);
        	}
        } 
        
        initWalls();        
        initStartArea();
        initVerticalMaze();
        
        set(MazeLocation.GOAL0, getFreePos(MazeLocation.WALL | MazeLocation.START));
        set(MazeLocation.GOAL1, getFreePos(MazeLocation.WALL | MazeLocation.START | MazeLocation.GOAL0));
        set(MazeLocation.GOAL2, getFreePos(MazeLocation.WALL | MazeLocation.START | MazeLocation.GOAL0 | MazeLocation.GOAL1));
        set(MazeLocation.GOAL3, getFreePos(MazeLocation.WALL | MazeLocation.START | MazeLocation.GOAL0 | MazeLocation.GOAL1 | MazeLocation.GOAL2));
        
        addAgentToMaze(AG0, 1, 1);
        addAgentToMaze(AG1, 1, 2);
        addAgentToMaze(AG2, 2, 2);
        addAgentToMaze(AG3, 2, 1);
    }
    
    public void initVerticalMaze() {
    	Random random = new Random();
    	for (int i = 3; i < WIDTH; i+=2) {
    		int emptySpace1 = random.nextInt(HEIGHT);
    		int emptySpace2 = random.nextInt(HEIGHT);
    		int emptySpace3 = random.nextInt(HEIGHT);
    		int emptySpace4 = random.nextInt(HEIGHT);
    		for (int j = 0; j < HEIGHT;j++) {
    			if (j != emptySpace1 && j != emptySpace2 && j != emptySpace3 && j != emptySpace4)
    				set(MazeLocation.WALL,new Location(i,j));
    		}
    	}
    }
    
    
    public void addAgentToMaze(int agentNum, int x, int y) {
    	MazeAgent newAgent = new MazeAgent(agents.size());
    	agents.add(newAgent);
    	updateAgentLocations(newAgent,x,y);
    }
    
    public void initWalls() {
        // Top Walls
        for (int i = 0; i < WIDTH; i++) {
        	set(MazeLocation.WALL, new Location(i, 0));
        }
        // Bottom Walls
        for (int i = 0; i<WIDTH; i++) {
        	set(MazeLocation.WALL,new Location(i,HEIGHT - 1));
        }
        // Left walls
        for (int i = 0; i < WIDTH; i++) {
        	set(MazeLocation.WALL,new Location(0,i));
        }
        // Right Walls
        for (int i = 0; i<WIDTH; i++) {
        	set(MazeLocation.WALL, new Location(WIDTH - 1, i));
        }
    }
    
    public void initStartArea() {
        // Start Area
        set(MazeLocation.START, new Location(1,1));
        set(MazeLocation.START, new Location(1,2));
        set(MazeLocation.START, new Location(2,2));
        set(MazeLocation.START, new Location(2,1));
    }

    public void set(int object, Location l) {
    	super.set(object, l.x, l.y);
    }
    
    public void updateAgentLocations(MazeAgent agent, int newX, int newY) {
    	// Update the agent location in the model in the super class
    	setAgPos(agent.getAgentNum(), newX, newY);
    	agent.setCyclesBlocked(0);
    	
    	// Add current location
    	agent.setCurrentLocation(new MazeLocation(newX, newY, data[newX][newY] ^ AGENT));
    	
    	// Add tile to the left
    	if ( (data[newX-1][newY] & AGENT) == 2)
    		agent.addLocation(new MazeLocation(newX-1, newY, data[newX-1][newY] ^ AGENT));
    	else
    		agent.addLocation(new MazeLocation(newX-1, newY, data[newX-1][newY]));
    	
    	
    	// Add tile to the right
    	if(( data[newX+1][newY] & AGENT) == 2)
    		agent.addLocation(new MazeLocation(newX+1, newY, data[newX+1][newY] ^ AGENT));  
    	else
    		agent.addLocation(new MazeLocation(newX+1, newY, data[newX+1][newY]));
    	
    	// Add tile to the top
    	if((data[newX][newY-1] & AGENT) == 2)
    		agent.addLocation(new MazeLocation(newX, newY-1, (data[newX][newY-1] ^ AGENT)));
    	else
    		agent.addLocation(new MazeLocation(newX, newY-1, (data[newX][newY-1])));
    	
    	// Add tile to the bottom
    	if((data[newX][newY+1] & AGENT) == 2 )
    		agent.addLocation(new MazeLocation(newX, newY+1, data[newX][newY+1] ^ AGENT));
    	else
    		agent.addLocation(new MazeLocation(newX, newY+1, data[newX][newY+1]));
    }
    
    public MazeAgent getAgentWithID(int id) {
    	for (MazeAgent ag : agents) {
    		if (ag.getAgentNum() == id)
    			return ag;
    	}
    	
    	return null;
    }
    
    public MazeAgent getAgentWithName(String name) {
    	for (MazeAgent ag : agents) {
    		if (ag.getAgentName().equals(name))
    			return ag;
    	}
    	
    	return null;
    }
}
