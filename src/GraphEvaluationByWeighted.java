
/*
 * Class Name : GraphEvaluation.java
 * 
 * Version Info : 1.0
 * 
 * Date : 2022-08-26
 * 
 * Copyright : writersnow707
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

import org.jgrapht.Graph;
import org.jgrapht.graph.*;

public class GraphEvaluationByWeighted extends GraphEvaluation {
	private static final int INF = (int) 1e9;

	Scanner sc = null;

	public GraphEvaluationByWeighted() {
		sc = new Scanner(System.in);
	}

	public void printMenu() {
		System.out.println("1. Display All Vertex Diameter Result");
		System.out.println("2. Display Clustering Coefficient Result");
		System.out.println("3. Display Expansion alpha(α) Result");
		System.out.println("4. Distance to Change Weight");
		System.out.println("0. Program Exit");
		System.out.print("Choose Number >> ");
	}

	/* 그래프의 타입을 선택 후, 해당 타입 경로를 반환 */

	private ArrayList<Double> createScoreVertex(String type, int data) throws Exception {
		ArrayList<Double> arr = new ArrayList<>();
		BufferedReader br = null;
		String s = null;
		int v = -1;
		double score = -1;

		String filePath = "F:\\InfoLAB Seminar\\scoring" + type + "_" + data + ".txt";
		for (int i = 0; i <= data; i++) {
			arr.add(0.0);
		}

		br = new BufferedReader(new FileReader(filePath));
		br.readLine();
		try {
			while (true) {
				s = br.readLine();

				v = Integer.parseInt(s.split(", ")[1]);
				score = Double.parseDouble(s.split(", ")[2]);

				arr.set(v, score);
			}
		} catch (NullPointerException e) {
			System.out.println("Vertex Scoring Complete.");
		} finally {
			br.close();
		}
		return arr;
	}

	/* 그래프 생성 (그래프의 생성만을 목적으로 함) */
	private SimpleWeightedGraph<Integer, DefaultWeightedEdge> createGraph(Vertex[] vertex, ArrayList<Double> score,
			String filePath, int data) throws Exception {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		DefaultWeightedEdge e1;

		BufferedReader br = new BufferedReader(new FileReader(filePath));

		String s = null;
		int v1 = -1;
		int v2 = -1;
		double weight = -1;
		int idx = 1;

		vertex[0] = null;
		for (int i = 1; i <= data; i++) {
			vertex[i] = new Vertex(i, 0.0);
		}

		System.out.println("Now Loading...");
		System.out.println();

		br.readLine();
		try {
			while (true) {
				s = br.readLine();

				v1 = Integer.parseInt(s.split(", ")[1]);
				v2 = Integer.parseInt(s.split(", ")[2]); // File의 Vertex Number를 Integer로 변환
				weight = Double.parseDouble(s.split(", ")[3]);

				if (!g.containsVertex(v1)) {
					vertex[v1].setScore(score.get(v1));
					g.addVertex(v1);
				}
				if (!g.containsVertex(v2)) {
					vertex[v2].setScore(score.get(v2));
					g.addVertex(v2);
				}
				if (g.containsEdge(v1, v2)) { // 기존에 연결되어 있는 경로가 또 나올 경우, File index와 해당 Vertex들을 출력
					System.out.println("***OVERLAP LINE " + idx + "***");
					System.out.println("EDGE : (" + v1 + ", " + v2 + ")");
				}
				if (!g.containsEdge(v1, v2)) {
					e1 = g.addEdge(v1, v2);
					g.setEdgeWeight(e1, weight);
				}

				idx++;
			}
		} catch (NullPointerException e) {
			System.out.println("Graph Mapping Complete.");
			System.out.println();
		} finally {
			br.close();
		}

		// add edges to create a circuit
		return g;
	}

	/* 2번 메뉴 Override : Clustering Coefficient 결과값 도출 */
	private void clusteringCoefficient(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph,
			ArrayList<ArrayList<Vertex>> g) {
		double[] clusteringCoef = new double[g.size()];
		int degree = -1;
		double sum = 0.0;
		int v1 = -1;
		int v2 = -1;
		int ei = -1;

		System.out.println("***Clustering Coefficent***");
		for (int i = 1; i <= g.size() - 1; i++) {
			degree = g.get(i).size();
			ei = 0;

			if (degree == 1) {
				clusteringCoef[i] = 0.0;
			} else {
				for (int j = 0; j < degree; j++) {
					for (int k = j + 1; k < degree; k++) {
						v1 = g.get(i).get(j).getIdx();
						v2 = g.get(i).get(k).getIdx();

						if (graph.containsEdge(v1, v2)) {
							ei++;
						}
					}
				}
				clusteringCoef[i] = (double) (2 * ei / (degree * (degree - 1)));
			}
		}

		System.out.print("AVERAGE : " + sum / (g.size() - 1) + " ");
		System.out.println("(" + sum + " / " + (g.size() - 1) + ")");
		System.out.println();
	}

	/* Distance -> Weight 변환 */
	private void distanceToWeight(ArrayList<ArrayList<Vertex>> g) {
		double sum = 0.0;
		double weight = 0.0;

		for (int i = 1; i <= g.size() - 1; i++) {
			sum = 0.0;
			for (int j = 0; j < g.get(i).size(); j++) {
				sum += g.get(i).get(j).getCost();
			}
			for (int j = 0; j < g.get(i).size(); j++) {
				weight = (g.get(i).get(j).getCost() / sum);
				g.get(i).get(j).setWeight(weight);
				System.out.print("<" + i + " -> " + g.get(i).get(j).getIdx() + "> Distance / Weight -> ");
				System.out.println(g.get(i).get(j).getCost() + " / " + g.get(i).get(j).getWeight());
			}
		}
	}

	/* Spanning Tree와 weight를 이용해 빈 Vertex의 Score 생성 */
	private void weightToScore(ArrayList<ArrayList<Vertex>> g, Vertex[] vertex) {

	}

	private void graphSelectMenu(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph,
			ArrayList<ArrayList<Vertex>> g, int num) {
		switch (num) {
		case 1: {
			diameterStandardDeviation(g);
			break;
		}
		case 2: {
			clusteringCoefficient(graph, g);
			break;
		}
		case 3: {
			printEdgeCount(g);
			break;
		}
		case 0: {
			System.out.println("System Quit...");
		}
		default: {
			System.out.println("System Error");
			break;
		}
		}
	}

	private void inputGraphData(SimpleGraph<Integer, DefaultWeightedEdge> g, ArrayList<ArrayList<Vertex>> lists,
			String str, int idx) {
		// System.out.println(str);
		int v1 = Integer.parseInt(str.substring(0, str.indexOf(":") - 1));
		int v2 = Integer.parseInt(str.substring(str.indexOf(":") + 2, str.length()));
		DefaultWeightedEdge e = g.getEdge(v1, v2);
		if (v1 != idx) {
			lists.get(v2).add(new Vertex(v1, g.getEdgeWeight(e)));
		} else {
			lists.get(v1).add(new Vertex(v2, g.getEdgeWeight(e)));
		}
	}

	public ArrayList<ArrayList<Vertex>> createAdjList(SimpleWeightedGraph<Integer, DefaultWeightedEdge> g) {
		ArrayList<ArrayList<Vertex>> lists = new ArrayList<>();
		String s = null;
		String str = null;
		int idx = -1;

		for (int i = 0; i <= g.vertexSet().size(); i++) {
			lists.add(new ArrayList<Vertex>()); // Graph의 크기만큼 graph (List)를 생성
		}

		for (int i = 1; i < lists.size(); i++) {
			for (int j = 0; j < g.outgoingEdgesOf(i).size(); j++) {
				s = (j == 0) ? (g.outgoingEdgesOf(i).toString()) : (s.substring(idx + 3, s.length()));
				idx = s.indexOf(")");
				str = (j == 0) ? s.substring(2, idx) : s.substring(1, idx);
				inputGraphData(g, lists, str, i);
			}
		}
		System.out.println(lists.size()); // 그래프의 사이즈를 체크

		return lists;
	}

	public void mainProject() throws Exception {
		ArrayList<ArrayList<Vertex>> graph = new ArrayList<>();
		ArrayList<Double> score = new ArrayList<>();

		Vertex[] vertex = null;
		File f = null;
		String graphType = null;
		String filePath = null;
		int data = -1;
		int compare = -1;
		int n = -1;

		while (true) {
			filePath = "F:\\InfoLAB Seminar\\WeightedGraph\\";
			graphType = null;
			data = -1;
			compare = -1;
			n = -1;

			while (graphType == null) {
				graphType = printGraphTypeMenu();
			}

			filePath += graphType + graphType + "_";
			while (!((data > 0) && (compare > 0))) {
				System.out.println("filePath : " + filePath);
				System.out.print("Data / Compare >> ");
				data = numberException();
				compare = numberException();
			}

			filePath += data + "_" + compare + " (1).txt"; // 임시 주소
			f = new File(filePath);

			System.out.println(filePath);
			if (f.exists()) { // file checking 유무 검사
				vertex = new Vertex[data + 1];
				score = createScoreVertex(graphType, data);
				SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createGraph(vertex, score, filePath, data); // 그래프의
																													// 생성
				graph = createAdjList(g);
				distanceToWeight(graph);
				weightToScore(graph, vertex);
				// bfs(graph);

				/*
				 * for (int i = 1; i <= graph.size()-1; i++) { System.out.println("<" + i +
				 * "> SCORE = " + vertex[i].getScore()); for (int j = 0; j <
				 * graph.get(i).size(); j++) { System.out.println("-> <" +
				 * graph.get(i).get(j).getIdx() + "> Distance [" + graph.get(i).get(j).getCost()
				 * + "] Weight (" + graph.get(i).get(j).getWeight() + ")"); } }
				 */

				while (n != 0) {
					while (n == -1) {
						printMenu();
						n = numberException();
					}
					graphSelectMenu(g, graph, n);
					n = -1;
				}

			} else {
				System.out.println("File is not exist. Please try again");
				System.out.println();
			}
			System.out.println("System Quit...");
			graph.clear();
			score.clear();
		}
	}

	public static void main(String[] args) throws Exception {
		GraphEvaluationByWeighted ge = new GraphEvaluationByWeighted();
		ge.mainProject();
	}
}
