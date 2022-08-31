public class Vertex implements Comparable<Vertex> {
	private int idx;
	private double score;
	private double cost;
	private double weight;

	public Vertex(int idx, double cost) {
		this.idx = idx;
		this.cost = cost;
		this.weight = 0.0;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getIdx() {
		return this.idx;
	}
	
	public double getScore() {
		return this.score;
	}

	public double getCost() {
		return this.cost;
	}
	
	public double getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(Vertex other) {
		if (this.cost < other.cost) {
			return -1;
		}
		return 1;
	}
	
	@Override
	public String toString() {
		return idx + "";
	}
}
