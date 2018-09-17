import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.Vector;

class Planner {
	PathFrontCombo ucs() {
		PriorityQueue<State> frontier = new PriorityQueue<State>(new CostComp()) {{add(new State(0.0, null, startPos));}};
		TreeSet<State> visited = new TreeSet<State>(new PosComp()) {{add(new State(0.0, null, startPos));}};
		while(!frontier.isEmpty()) {
			State s = frontier.poll();
			if(isWithinTen(s))
				return new PathFrontCombo(state2moves(s), frontier);
			for(int i = 0; i < 16; i+=2) {
				int[] newPos = new int[] {s.pos[0] + act[i], s.pos[1] + act[i+1]};
				if(newPos[0] >= 0 && newPos[0] < 1200 && newPos[1] >= 0 && newPos[1] < 600) {
					float speed = m.getTravelSpeed((float)newPos[0], (float)newPos[1]);
					double actCost = this.getDistance(s.pos[0], s.pos[1], newPos[0], newPos[1])/speed;
					State newChild = new State(actCost, s, newPos);
					if(visited.contains(newChild)) {
						State oldChild = visited.floor(newChild);
						if(oldChild != null) if (s.cost + actCost < oldChild.cost) {
							oldChild.cost = s.cost + actCost;
							oldChild.parent = s;
						}
					}
					else {
						newChild.cost += s.cost;
						frontier.add(newChild);
						visited.add(newChild);
					}
				}
			}
		}
		return null;
	}
	
	
	Model m;
	int[] startPos, goalPos, act = new int[] {10,-10, 10,0, 10,10, 0,10, 0,-10, -10,-10, -10,0, -10,10};
	
	Planner(Model m, int destX, int destY) throws IOException {
		this.m = m;
		this.startPos = new int[] {(int)m.getX(), (int)m.getY()};
		this.goalPos = new int[] {destX, destY};
	}
	
//	//A-STAR SEARCh
//	PathAndFrontier aStar(float minSpeed) {
//		//AState Class
//		class AState extends State {
//			double extraCost;
//			AState (State s, double extra) {
//				super(s);
//				this.extraCost = extra;
//			}
//			double getAStarCost() {
//				return cost + extraCost;
//			}
//		}
//		//AStar Comparator
//		class AStarComp implements Comparator<AState> {
//			public int compare(AState a, AState b) {
//				for(int i = 0; i < 2; i++)
//					if(a.getAStarCost() < b.getAStarCost())
//						return -1;
//					else if(a.getAStarCost() > b.getAStarCost())
//						return 1;
//				return 0;
//			}
//		}
//		
//		class PathAndFrontierAState extends PathAndFrontier {
//			PriorityQueue<State> frontier;
//			PathAndFrontierAState(Vector<int[]> path, PriorityQueue<AState> AStFront) {
//				frontier = new PriorityQueue<>();
//				for(AState astate: AStFront)
//					frontier.add((State)astate);
//				this.path = path;
//			}
//		}
//		
//		PriorityQueue<AState> frontier = new PriorityQueue<AState>(new AStarComp()) {{add(new AState(new State(0.0, null, startPos), minSpeed));}};
//		TreeSet<State> visited = new TreeSet<State>(new PosComp()) {{add(new AState(new State(0.0, null, startPos), minSpeed));}};
//		while(!frontier.isEmpty()) {
//			AState as = frontier.poll();
//			if(isWithinTen(as))
//				return new PathAndFrontierAState(state2moves(as), frontier);
//			for(int i = 0; i < 16; i+=2) {
//				int[] newPos = new int[] {as.pos[0] + act[i], as.pos[1] + act[i+1]};
//				if(newPos[0] >= 0 && newPos[0] < 1200 && newPos[1] >= 0 && newPos[1] < 600) {
//					float speed = m.getTravelSpeed((float)newPos[0], (float)newPos[1]);
//					double actCost = this.getDistance(as.pos[0], as.pos[1], newPos[0], newPos[1])/speed;
//					AState newChild = new AState(new State(actCost, as, newPos), minSpeed*getDistance(as.pos[0], as.pos[1], goalPos[0], goalPos[1]));
//					if(visited.contains(newChild)) {
//						AState oldChild = (AState)visited.floor(newChild);
//						if(oldChild != null) if (as.cost + actCost + < oldChild.getAStarCost()) {
//							oldChild.cost = as.cost + actCost;
//							oldChild.parent = as;
//						}
//					}
//					else {
//						newChild.cost += as.cost;
//						frontier.add(newChild);
//						visited.add(newChild);
//					}
//				}
//			}
//		}
//		return null;
//	}


	
	boolean isWithinTen(State s) {
		return Math.abs(s.pos[0] - goalPos[0]) < 10 && Math.abs(s.pos[1] - goalPos[1]) < 10;
	}

	public Vector<int[]> state2moves(State s) {
		Vector<int[]> moves = new Vector<>();
		if(s != null)
			while(s.parent != null) {
				moves.add(new int[]{s.pos[0], s.pos[1]});
				s = s.parent;
			}
		return moves;
	}
	
	public class PathFrontCombo {
		Vector<int[]> path;
		PriorityQueue<State> frontier;
		PathFrontCombo(Vector<int[]> path, PriorityQueue<State> frontier) {
			this.path = path; this.frontier = frontier;
		}
		PathFrontCombo() {
			path = null;
			frontier = new PriorityQueue<>();
		}
	}
	
	public float getDistance(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt(Math.pow(x2-x1, 2) + (float)Math.pow(y2-y1, 2));
	}
	
	class CostComp implements Comparator<State> {
		public int compare(State a, State b) {
			return Double.compare(a.cost, b.cost);
		}
	}
	class PosComp implements Comparator<State> {
		public int compare(State a, State b) {
			for(int i = 0; i < 2; i++)
				if(a.pos[i] < b.pos[i])
					return -1;
				else if(a.pos[i] > b.pos[i])
					return 1;
			return 0;
		}
	}
	
}