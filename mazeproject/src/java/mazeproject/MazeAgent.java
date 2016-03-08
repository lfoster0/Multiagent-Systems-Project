package mazeproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

public class MazeAgent {
	
	// Numerical representation of the agent: 0,1,2,3 etc.
	private int agentNum;
	// String representation of the agent: ag0,ag1,ag2,ag3 etc.
	private String agentName;
	
	// Holds the locations seen to the left right up down
	private ArrayList<MazeLocation> seenLocations;
	
	// Holds the locations the agent has physically been on.
	private ArrayList<MazeLocation> visitedLocations;
	
	// Used to backtrack so the agent can get to the start
	private Stack<MazeLocation> moveHistory;
	
	private MazeLocation currentLocation;
	private boolean isCarryingGoal;
	private int cyclesBlocked;
	
	private MazeLocation goal0Location;
	private MazeLocation goal1Location;
	private MazeLocation goal2Location;
	private MazeLocation goal3Location;
	
	// Related to when the agent gets told the path to his goal by another agent
	private boolean hasPathToGoal;
	private ArrayList<MazeLocation> pathToGoal;
	
	private boolean givenGoal0Path;
	private boolean givenGoal1Path;
	private boolean givenGoal2Path;
	private boolean givenGoal3Path;
	
	
	public MazeAgent(int num) {
		seenLocations = new ArrayList<MazeLocation>();
		visitedLocations = new ArrayList<MazeLocation>();
		moveHistory = new Stack<MazeLocation>();
		
		this.agentNum = num;
		this.agentName = "ag"+num;
	}
	
	public void addLocation(MazeLocation newLocation) {
		seenLocations.add(newLocation);
	}
	
	public void incrementCyclesBlocked() {
		cyclesBlocked++;
	}
	
	// Looks through its move history to get a path from the current location to the given goal
	// This method DOES NOT check if it has actually seen the goal or not the check is done in
	//   the agent files.
	public ArrayList<MazeLocation> givePathToGoal(String goal, MazeAgent agentToGivePathTo) {
		if (goal.equals("goal0")) {
			givenGoal0Path = true;
		} else if (goal.equals("goal1")) {
			givenGoal1Path = true;
		} else if (goal.equals("goal2")) {
			givenGoal2Path = true;
		} else if (goal.equals("goal3")) {
			givenGoal3Path = true;
		}
		
		MazeLocation goalLocation = null;
		MazeLocation originalGoal = null;
		ArrayList<MazeLocation> pathToGivenGoal = new ArrayList<MazeLocation>();
		
		// Find the location of the goal we're looking for
		for (MazeLocation loc : seenLocations) {
			if (loc.getTypeString().equals(goal)) {
				goalLocation = loc;
				break;
			}
		}
		
		// We might not have actually visited the location where the goal is just seen it 
		// to the left or right or top or bottom.  If we haven't visited it then we need to
		// find the space next to it we did visit and get the path to there.
		
		boolean hasNearestLocation = false;
		if(!moveHistory.contains(goalLocation)) {
			// Find the nearest location visited
			for (MazeLocation visitedLoc : moveHistory) {
				// Left of the goal
				if (visitedLoc.getX()-1 == goalLocation.getX() && visitedLoc.getY() == goalLocation.getY()) {
					originalGoal = goalLocation;
					goalLocation = visitedLoc;
					hasNearestLocation = true;
					break;
				}
				// Right of the goal
				if (visitedLoc.getX()+1 == goalLocation.getX() && visitedLoc.getY() == goalLocation.getY()) {
					originalGoal = goalLocation;
					goalLocation = visitedLoc;
					hasNearestLocation = true;
					break;
				}
				// Below of the goal
				if (visitedLoc.getX()== goalLocation.getX() && visitedLoc.getY()-1 == goalLocation.getY()) {
					originalGoal = goalLocation;
					goalLocation = visitedLoc;
					hasNearestLocation = true;
					break;
				}
				// Above of the goal
				if (visitedLoc.getX() == goalLocation.getX() && visitedLoc.getY()+1 == goalLocation.getY()) {
					originalGoal = goalLocation;
					goalLocation = visitedLoc;
					hasNearestLocation = true;
					break;
				}
			}
		}
		
		if (hasNearestLocation || moveHistory.contains(goalLocation)) {
			
			// At this point we have the correct goal location we need
			ArrayList<MazeLocation> moveHistoryList = new ArrayList<MazeLocation>(moveHistory);
			Collections.reverse(moveHistoryList);
			
			// Iterate over history adding moves to the list until we hit the goal.
			// Use while loop and iterator to avoid concurrent exception
			Iterator<MazeLocation> iter = moveHistoryList.iterator();
			while (iter.hasNext()) {
				MazeLocation loc = iter.next();
		    	pathToGivenGoal.add(loc);
			    if (loc.equals(goalLocation)) {
			        break;
			    }
			}
			
			// If we were getting the path to the closest location instead of the actual goal
			// add the location to the goal to the end.
			if (originalGoal != null) {
				pathToGivenGoal.add(originalGoal);
			}
			
			return pathToGivenGoal;
		}
		
		else {
			return null;
		}
	}
	
	/************************************************/
	/***************** GETTERS **********************/
	/************************************************/
	
	// Get tile to the left
	public MazeLocation getLeft() {
		for (MazeLocation l : seenLocations) {
			if (l.getX() == currentLocation.getX() - 1 && l.getY() == currentLocation.getY())
				return l;
		}
		
		System.out.println("Left returning null");
		return null;
	}
	
	// Get the tile to the right
	public MazeLocation getRight() {
		for (MazeLocation l : seenLocations) {
			if (l.getX() == currentLocation.getX() + 1 && l.getY() == currentLocation.getY())
				return l;
		}
		System.out.println("Right returning null");
		return null;
	}
	
	// Get the location above the agent
	public MazeLocation getUp() {
		for (MazeLocation l : seenLocations) {
			if (l.getX() == currentLocation.getX() && l.getY() == currentLocation.getY() - 1)
				return l;
		}
		System.out.println("Up returning null");
		return null;
	}
	
	// Get the location below the agent
	public MazeLocation getDown() {
		for (MazeLocation l : seenLocations) {
			if (l.getX() == currentLocation.getX() && l.getY() == currentLocation.getY() + 1)
				return l;
		}
		System.out.println("Down returning null");
		return null;
	}
	
	public MazeLocation getCurrentLocation() {
		return currentLocation;
	}
	
	public MazeLocation getGoal0Location() {
		return goal0Location;
	}
	
	public boolean hasGivenGoal0Path() {
		return givenGoal0Path;
	}
	
	public MazeLocation getGoal1Location() {
		return goal1Location;
	}
	
	public boolean hasGivenGoal1Path() {
		return givenGoal1Path;
	}
	
	public MazeLocation getGoal2Location() {
		return goal2Location;
	}
	
	public boolean hasGivenGoal2Path() {
		return givenGoal2Path;
	}
	
	public MazeLocation getGoal3Location() {
		return goal3Location;
	}
	
	public boolean hasGivenGoal3Path() {
		return givenGoal3Path;
	}
	
	public boolean hasPathToGoal() {
		return hasPathToGoal;
	}
	
	public ArrayList<MazeLocation> getPathToGoal() {
		return pathToGoal;
	}
	
	public boolean isCarryingGoal() {
		return this.isCarryingGoal;
	}
	
	public String getAgentName() {
		return this.agentName;
	}
	
	public int getCyclesBlocked() {
		return cyclesBlocked;
	}
	
	public int getAgentNum() {
		return this.agentNum;
	}
	
	public ArrayList<MazeLocation> getVisitedLocations() {
		return visitedLocations;
	}
	
	public Stack<MazeLocation> getMoveHistory() {
		return moveHistory;
	}
	
	/************************************************/
	/***************** SETTERS **********************/
	/************************************************/

	public void setGoal0Location(MazeLocation goal0Location) {
		this.goal0Location = goal0Location;
	}

	public void setGoal1Location(MazeLocation goal1Location) {
		this.goal1Location = goal1Location;
	}

	public void setGoal2Location(MazeLocation goal2Location) {
		this.goal2Location = goal2Location;
	}

	public void setGoal3Location(MazeLocation goal3Location) {
		this.goal3Location = goal3Location;
	}
	
	public void setPathToGoal(ArrayList<MazeLocation> path) {
		this.pathToGoal = path;
		if (path != null)
			hasPathToGoal = true;
	}
	
	public void setCarrying(boolean b) {
		this.isCarryingGoal = b;
	}
	
	public void setCurrentLocation(MazeLocation currentLocation) {
		this.currentLocation = currentLocation;
		if (!this.visitedLocations.contains(currentLocation)) {
			this.visitedLocations.add(currentLocation);
			this.moveHistory.push(currentLocation);
		}
	}
	
	public void setCyclesBlocked(int numBlocked) {
		cyclesBlocked = numBlocked;
	}
}
