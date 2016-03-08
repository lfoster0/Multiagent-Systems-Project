package mazeproject;

import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.Logger;

import sun.management.resources.agent;

public class MazeView extends GridWorldView {
	
	private Logger logger = Logger.getLogger("mazeproject.mas2j." + MazeView.class.getName());
	private MazeModel model;
	
	private static Color startColor = new Color(68,75,81);
	private static Color emptyColor = new Color(192,157,109);
	private static Color wallColor = new Color(110,79,54);

	private static Color[] goalColors = {new Color(242,100,48), new Color(140,63,106), new Color(132,191,174), new Color(217,61,102)};
	
    public MazeView(MazeModel model) {
        super(model, "Maze Project", 1000);
        this.model = model;
        defaultFont = new Font("Arial", Font.BOLD, 16);
        setVisible(true);
        repaint();
    }
    
    @Override
    public void draw(Graphics g, int x, int y, int object) {
    	switch (object) {
    		case MazeLocation.WALL:
    			 g.setColor(wallColor);
    		     g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    		     g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    			break;
    		case MazeLocation.EMTPY:
    			g.setColor(emptyColor);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    		case MazeLocation.START:
    			g.setColor(startColor);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    		case MazeLocation.GOAL0:
    			g.setColor(goalColors[0]);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    		case MazeLocation.GOAL1:
    			g.setColor(goalColors[1]);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    		case MazeLocation.GOAL2:
    			g.setColor(goalColors[2]);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    		case MazeLocation.GOAL3:
    			g.setColor(goalColors[3]);
    			g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
   		     	g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
   		     	break;
    			
        }
    }
    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
    	// Get the agent corresponding to this draw
    	MazeAgent ag = model.getAgentWithID(id);
    	
    	Color agColor;
    	Color textColor;
    	// If the agent is carrying the goal color draw it the color of the goal w/ black text
        if (ag.isCarryingGoal()) {
           agColor = goalColors[id];
           textColor = Color.BLACK;
        }
        // Draw it black with the number being the goal color
        else {
        	agColor = Color.BLACK;
        	if (ag.hasPathToGoal())
        		textColor = Color.WHITE;
        	else
        		textColor = goalColors[id];
        	
        }

        // Draw the agent
        super.drawAgent(g, x, y,  agColor, -1);
 
        // Draw the number of the agent
        g.setColor(textColor);
        drawString(g, x, y, defaultFont, String.valueOf(id));
    }
}
