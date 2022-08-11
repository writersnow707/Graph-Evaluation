public class Vertex implements Comparable<Vertex> {
	private int idx;
	private int cost;
	
	public Vertex(int idx, int cost) {
		this.idx = idx;
		this.cost = cost;
	}
	
	public int getIdx() {
		return this.idx;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	@Override
	public int compareTo(Vertex other) {
		if (this.cost < other.cost) {
			return -1;
		}
		return 1;
	}
}
