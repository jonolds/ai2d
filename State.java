class State {
	double cost, est;
	State parent;
	int[] pos;
	
	State(State s) {
		this.cost = s.cost;
		this.est = s.est;
		this.parent = s.parent;
		this.pos[0] = s.pos[0];
		this.pos[1] = s.pos[1];
	}

	State(double cost, State parent, int[] pos) {
		this.cost = cost;
		this.parent = parent;
		this.pos = pos;
	}
	State(double cost, double est, State parent, int[] pos) {
		this(cost, parent, pos);
		this.est = est;
		
	}

	void printAll() {
		System.out.println("[" + pos[0] + "," + pos[1] + "]  cost: " + cost + "  parent: " + ((parent == null) ? "null" : parent));
	}
	void printPos() {
		System.out.println("[" + pos[0] + "," + pos[1] + "]");
	}
	void printCost() {
		System.out.println(cost);
	}
}