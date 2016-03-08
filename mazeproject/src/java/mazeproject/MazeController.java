package mazeproject;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// Add an user controlled agent
public class MazeController extends Environment{
	
	private Logger logger = Logger.getLogger("mazeproject."
			+ MazeController.class.getName());
	
	private MazeModel mazeModel;
	private MazeView mazeView;
	
	private int delay;
	
	/** Called before the MAS execution with the args informed in .mas2j */
	@Override
	public void init(String[] args) {
		super.init(args);
		delay = Integer.parseInt(args[0]);
		mazeModel = new MazeModel();
		mazeView = new MazeView(mazeModel);
		
		informAgsEnvironmentChanged();
		
		updateAllPercepts();
	}
	
	@Override
	public synchronized boolean executeAction(String agName, Structure action) {
		try {
			if (delay > 0) {
				Thread.sleep(delay);
			}
			
			MazeAgent agent = mazeModel.getAgentWithName(agName);
			// Grab a list of all the other agent locations to make sure we don't overlap
			ArrayList<MazeLocation> otherAgentLocations = new ArrayList<MazeLocation>();
			for (MazeAgent ag : mazeModel.agents) {
				//System.out.println("Adding location: " + ag.getCurrentLocation());
				otherAgentLocations.add(ag.getCurrentLocation());
			}
			
			// Move to the given location
			if (action.getFunctor().toString().equals("move")) {
				int x = Integer.parseInt(action.getTerm(0).toString());
				int y = Integer.parseInt(action.getTerm(1).toString());
				
				boolean validMove = true;
				for (MazeLocation l : otherAgentLocations) {
					if (l.getX() == x && l.getY() == y)
						validMove = false;
				}
				
				if (validMove)
					mazeModel.updateAgentLocations(agent, x, y);
			}
			
			// Pick up your goal item
			if (action.getFunctor().toString().equals("pickup")) {
				int x = Integer.parseInt(action.getTerm(0).toString());
				int y = Integer.parseInt(action.getTerm(1).toString());
				
				agent.setCarrying(true);
				mazeModel.set(MazeLocation.EMTPY, x, y);
				// Add the agent again so it doesn't look invisible for a few cycles
				mazeModel.setAgPos(agent.getAgentNum(), x, y);
			}
			
			// Look in agent move history and move back one
			if (action.getFunctor().toString().equals("moveBack")) {
				MazeLocation currentLoc = agent.getMoveHistory().pop();
				MazeLocation loc = agent.getMoveHistory().peek();
				
				if (!otherAgentLocations.contains(loc)) {
					mazeModel.updateAgentLocations(agent, loc.getX(), loc.getY());
				} 
				// Agent in our way so push the popped off move back
				else {
					agent.getMoveHistory().push(currentLoc);
					agent.incrementCyclesBlocked();
				}
			}
			// Make a random valid move
			if (action.getFunctor().toString().equals("moveRandom")) {
				// Need to check this to fix a weird Jason order of execution bug
				if (!agent.hasPathToGoal()) {
					ArrayList<MazeLocation> possibleMoves = new ArrayList<MazeLocation>();
					ArrayList<MazeLocation> movesToTake = new ArrayList<MazeLocation>();
					
					Random rand = new Random();
					rand.setSeed(rand.nextLong());
					
					// Grab all the moves that are not walls
					if (agent.getLeft().getTypeInt() != MazeLocation.WALL )
						possibleMoves.add(agent.getLeft());
					if (agent.getRight().getTypeInt() != MazeLocation.WALL )
						possibleMoves.add(agent.getRight());
					if (agent.getUp().getTypeInt() != MazeLocation.WALL )
						possibleMoves.add(agent.getUp());
					if (agent.getDown().getTypeInt() != MazeLocation.WALL )
						possibleMoves.add(agent.getDown());
					
					// Check if there are any places I haven't been to that are free
					for (MazeLocation l : possibleMoves) {
						if(!agent.getVisitedLocations().contains(l) && !otherAgentLocations.contains(l)) {
							movesToTake.add(l);
						}
					}
					
					// If we some spaces we haven't move pick one randomly and go there
					if (movesToTake.size() > 0) {
						MazeLocation locationToMoveTo = movesToTake.get(rand.nextInt(movesToTake.size()));
						mazeModel.updateAgentLocations(agent, locationToMoveTo.getX(), locationToMoveTo.getY());
					}
					
					// Now check if there are any empty spaces we haven't been to that have agents on them
					// If there are we're just going to not make a move and wait until they move, or if we
					// wait a certain amount of time we will ask them to move.
					else {
						for (MazeLocation l : possibleMoves) {
							if(!agent.getVisitedLocations().contains(l) && otherAgentLocations.contains(l)) {
								movesToTake.add(l);
							}
						}
						// There is a move but its blocked so wait
						if (movesToTake.size() > 0) {
							agent.incrementCyclesBlocked();
						}
						// No places to move we haven't been so we should start going backwards
						else {
							MazeLocation currentLoc = agent.getMoveHistory().pop();
							MazeLocation backtrackLoc = agent.getMoveHistory().peek();
							
							// The location to backtrack to isn't blocked so go there
							if (!otherAgentLocations.contains(backtrackLoc)) {
								mazeModel.updateAgentLocations(agent, backtrackLoc.getX(), backtrackLoc.getY());
							}
							// It is blocked so update our blocked counter and push our location back on
							else {
								agent.getMoveHistory().push(currentLoc);
								agent.incrementCyclesBlocked();
							}
						}	
					}
				}
				
			}
			
			// When the agent needs to record it saw another agents goal
			if (action.getFunctor().toString().equals("recordSeeingGoal")) {
				String goalName = action.getTerm(0).toString();
				int x = Integer.parseInt(action.getTerm(1).toString());
				int y = Integer.parseInt(action.getTerm(2).toString());

				if (goalName.equals("goal0")) {
					agent.setGoal0Location(new MazeLocation(x, y, MazeLocation.GOAL0));
				} 
				else if (goalName.equals("goal1")) {
					agent.setGoal1Location(new MazeLocation(x, y, MazeLocation.GOAL1));
				}
				else if (goalName.equals("goal2")) {
					agent.setGoal2Location(new MazeLocation(x, y,MazeLocation.GOAL2));
				}
				else {
					agent.setGoal3Location(new MazeLocation(x, y, MazeLocation.GOAL3));
				}
			}
			// Action to get the path to the goal from another agent.
			if (action.getFunctor().toString().equals("getPathFromAgent")) {
				MazeAgent agentToGetPathFrom = mazeModel.getAgentWithName(action.getTerm(0).toString());
				if (agName.equals("ag0")) {
					agent.setPathToGoal(agentToGetPathFrom.givePathToGoal("goal0", agent));
				} else if (agName.equals("ag1")) {
					agent.setPathToGoal(agentToGetPathFrom.givePathToGoal("goal1", agent));
				} else if (agName.equals("ag2")) {
					agent.setPathToGoal(agentToGetPathFrom.givePathToGoal("goal2", agent));
				} else {
					agent.setPathToGoal(agentToGetPathFrom.givePathToGoal("goal3", agent));
				}
			}
			if (action.getFunctor().toString().equals("followPath")) {
				MazeLocation nextMove = agent.getPathToGoal().remove(0);
				// If there is an agent in our way add the move back and wait until they are gone
				if (otherAgentLocations.contains(nextMove)) {
					agent.incrementCyclesBlocked();
					agent.getPathToGoal().add(0, nextMove);
				} else {
					mazeModel.updateAgentLocations(agent, nextMove.getX(), nextMove.getY());
				}
			}
			updateAllPercepts();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, ex.getMessage());
		}
		return true;
	}

	private void updateAllPercepts() {
		HashMap<MazeLocation,String> otherAgentLocations = new HashMap<MazeLocation,String>();
		for (MazeAgent ag : mazeModel.agents) {
			otherAgentLocations.put(ag.getCurrentLocation(),ag.getAgentName());
		}
		
		for(MazeAgent agent : mazeModel.agents) {
			String agentName = agent.getAgentName();
			clearPercepts(agentName);
			
			addPercept(agentName,Literal.parseLiteral(String.format("blockedFor(%d)", agent.getCyclesBlocked())));
			
			// Add the current location
			MazeLocation currentLoc = agent.getCurrentLocation();
			addPercept(agentName,Literal.parseLiteral(String.format("at(%d,%d,%s)", currentLoc.getX(), currentLoc.getY(), currentLoc.getTypeString())));
			
			// Add left
			MazeLocation leftLoc = agent.getLeft();
			if (otherAgentLocations.containsKey(leftLoc)) {
				String leftAgentName = (String) otherAgentLocations.get(leftLoc);
				addPercept(agentName,Literal.parseLiteral(String.format("left(%d,%d,%s)", leftLoc.getX(), leftLoc.getY(), leftAgentName)));	
			}
			else {
				addPercept(agentName,Literal.parseLiteral(String.format("left(%d,%d,%s)", leftLoc.getX(), leftLoc.getY(), leftLoc.getTypeString())));
			}
			
			// Add right
			MazeLocation rightLoc = agent.getRight();
			if (otherAgentLocations.containsKey(rightLoc)) {
				String rightAgentName = (String) otherAgentLocations.get(rightLoc);
				addPercept(agentName,Literal.parseLiteral(String.format("right(%d,%d,%s)", rightLoc.getX(), rightLoc.getY(), rightAgentName)));	
			}
			else {
				addPercept(agentName,Literal.parseLiteral(String.format("right(%d,%d,%s)", rightLoc.getX(), rightLoc.getY(), rightLoc.getTypeString())));
			}
			
			// Add up
			MazeLocation topLoc = agent.getUp();
			if (otherAgentLocations.containsKey(topLoc)) {
				String topAgentName = (String) otherAgentLocations.get(topLoc);
				addPercept(agentName,Literal.parseLiteral(String.format("top(%d,%d,%s)", topLoc.getX(), topLoc.getY(), topAgentName)));	
			}
			else {
				addPercept(agentName,Literal.parseLiteral(String.format("top(%d,%d,%s)", topLoc.getX(), topLoc.getY(), topLoc.getTypeString())));
			}
			
			
			// Add down
			MazeLocation downLoc = agent.getDown();
			if (otherAgentLocations.containsKey(downLoc)) {
				String downAgentName = (String) otherAgentLocations.get(downLoc);
				addPercept(agentName,Literal.parseLiteral(String.format("down(%d,%d,%s)", downLoc.getX(), downLoc.getY(), downAgentName)));	
			}
			else {
				addPercept(agentName,Literal.parseLiteral(String.format("down(%d,%d,%s)", downLoc.getX(), downLoc.getY(), downLoc.getTypeString())));
			}
			
			// If the agent has seen goal0 position
			if (agent.getGoal0Location() != null) {
				MazeLocation goal0Loc = agent.getGoal0Location();
				addPercept(agentName,Literal.parseLiteral(String.format("seenGoal(%d,%d,%s)", goal0Loc.getX(), goal0Loc.getY(), goal0Loc.getTypeString())));
			}
			
			// If the agent has seen goal1 position
			if (agent.getGoal1Location() != null) {
				MazeLocation goal1Loc = agent.getGoal1Location();
				addPercept(agentName,Literal.parseLiteral(String.format("seenGoal(%d,%d,%s)", goal1Loc.getX(), goal1Loc.getY(), goal1Loc.getTypeString())));
			}
			
			// If the agent has seen goal2 position
			if (agent.getGoal2Location() != null) {
				MazeLocation goal2Loc = agent.getGoal2Location();
				addPercept(agentName,Literal.parseLiteral(String.format("seenGoal(%d,%d,%s)", goal2Loc.getX(), goal2Loc.getY(), goal2Loc.getTypeString())));
			}
			
			// If the agent has seen goal3 position
			if (agent.getGoal3Location() != null) {
				MazeLocation goal3Loc = agent.getGoal3Location();
				addPercept(agentName,Literal.parseLiteral(String.format("seenGoal(%d,%d,%s)", goal3Loc.getX(), goal3Loc.getY(), goal3Loc.getTypeString())));
			}
			
			// If we have given the path to goal0
			if (agent.hasGivenGoal0Path()) {
				addPercept(agentName,Literal.parseLiteral("givenPathToGoal(goal0)"));
			}
			
			// If we have given the path to goal1
			if (agent.hasGivenGoal1Path()) {
				addPercept(agentName,Literal.parseLiteral("givenPathToGoal(goal1)"));
			}
			
			// If we have given the path to goal2
			if (agent.hasGivenGoal2Path()) {
				addPercept(agentName,Literal.parseLiteral("givenPathToGoal(goal2)"));
			}
			
			// If we have given the path to goal3
			if (agent.hasGivenGoal3Path()) {
				addPercept(agentName,Literal.parseLiteral("givenPathToGoal(goal3)"));
			}
			
			if (agent.hasPathToGoal()) {
				addPercept(agentName,Literal.parseLiteral("followingPathToGoal"));
			}
			
			if (agent.isCarryingGoal()) {
				addPercept(agentName,Literal.parseLiteral("hasGoal"));
			}
		}
	}
	
	/** Called before the end of MAS execution */
	@Override
	public void stop() {
		super.stop();
	}
}
