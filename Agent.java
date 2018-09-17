import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Vector;
import javax.imageio.ImageIO;

class Agent {
	public Vector<int[]> path = new Vector<int[]>(); //elem 0 is goal. path.get(path.size()-1) is init pos
	int[] bigGoal = new int[] {100, 100};
	PriorityQueue<State> frontier = new PriorityQueue<>();
	Image bigGoalGrn, curPosPur;
	boolean useAStar = false;
	float minSpeed = -1.0f;

	void drawPlan(Graphics g, Model m) {
		drawDots(g, m);
		drawFrontier(g);
		drawPath(g, m);
		
	}

	void update(Model m) throws IOException {
		Controller c = m.getController();
		
		Planner.PathFrontCombo combo;
		if(!useAStar)
			combo = (new Planner(m, bigGoal[0], bigGoal[1])).ucs();
		else
			combo = (new Planner(m, bigGoal[0], bigGoal[1])).ucs();
		path = combo.path;
		frontier = combo.frontier;
		if(m.getX() == m.getDestX() && m.getY() == m.getDestY()) {
			if(path.size() > 0)
				m.setDest(path.get(path.size()-1)[0], path.get(path.size()-1)[1]);
			else if(isWithinTen(bigGoal[0], bigGoal[1], (int)m.getX(), (int)m.getY()))
				m.setDest((float)bigGoal[0], (float)bigGoal[1]);
		}
		while(true) {
			MouseEvent e = c.nextMouseEvent();
			if(e == null)
				break;
			if(e.getButton() == 1)
				useAStar = false;
			else if(e.getButton() == 3) {
				useAStar = true;
				if(minSpeed < 0)
					getMinSpeed(m);
			}
			bigGoal = new int[] {e.getX(), e.getY()};
		}
	}
	
	void getMinSpeed(Model m) {
		float currentMin = m.getTravelSpeed(0,0);
		for(int i = 0; i < Model.XMAX; i+=10)
			for(int k = 0; k < Model.YMAX; k+=10)
				if(m.getTravelSpeed(i, k) < currentMin)
					currentMin = m.getTravelSpeed(i, k);
		minSpeed = currentMin;
	}
	
	boolean isWithinTen(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) < 10 && Math.abs(y1 - y2) < 10;
	}
	
	void drawDots(Graphics g, Model m) {
		g.drawImage(bigGoalGrn, bigGoal[0]-3, (int)bigGoal[1] -3, null);
//		System.out.println("bigGoalGrn: " + (bigGoal[0]-3) + "," + ((int)bigGoal[1] -3));
		g.drawImage(curPosPur, (int)m.getX() - 3, (int)m.getY() - 3, null);
//		System.out.println("curPosPur: " + ((int)m.getX() - 3) + "," + ((int)m.getY() - 3));
	}
	
	void drawPath(Graphics g, Model m) {
			g.setColor(Color.white);
			int[] last = bigGoal;
			for(int i = 0; i < path.size(); i++) {
				g.drawLine(last[0], last[1], path.get(i)[0], path.get(i)[1]);
				last = new int[] {path.get(i)[0], path.get(i)[1]};
			}
			if(!path.isEmpty())
				g.drawLine((int)m.getX(), (int)m.getY(), path.get(path.size()-1)[0], path.get(path.size()-1)[1]);
	}
	
	void drawFrontier(Graphics g) {
		g.setColor(Color.YELLOW);
		for(State s : frontier)
			g.fillOval(s.pos[0], s.pos[1], 10, 10);
	}
	
	public static void main(String[] args) throws Exception {
		Controller.playGame();
	}
	
	Agent() throws IOException {
		bigGoalGrn = ImageIO.read(new File("grnDot.png"));
		curPosPur = ImageIO.read(new File("purDot.png"));
	}

}