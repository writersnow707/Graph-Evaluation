/*
 * Class Name : GraphEvaluation.java
 * 
 * Version Info : -
 * 
 * Date : 2022-08-17
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
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphEvaluation {
	private static final int INF = (int) 1e9;

	Scanner sc = null;

	public GraphEvaluation() {
		sc = new Scanner(System.in);
	}

	private int numberException() {
		int num = -1;

		try {
			num = sc.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("ERROR");
			sc.next();
			return -1;
		}
		return num;
	}
	
	public void printMenu() {
		System.out.println("1. Display All Vertex Diameter Result");
		System.out.println("2. Display Clustering Coefficient Result");
		System.out.println("3. Display Expansion alpha(α) Result");
		System.out.println("0. Program Exit");
		System.out.print("Choose Number >> ");
	}

	/* 그래프의 타입을 선택 후, 해당 타입 경로를 반환 */
	private String printGraphTypeMenu() {
		int n = -1;
		while (n == -1) {
			System.out.println("1. Random Graph");
			System.out.println("2. Chain Random Graph");
			System.out.println("3. Ring Random Graph");
			System.out.print("Select >> ");
			
			n = numberException();
		}
		switch (n) {
		case 1: {
			return "randomSet\\randomSet_";
		}
		case 2: {
			return "chainRandomSet\\chainRandomSet_";
		}
		case 3: {
			return "ringRandomSet\\ringRandomSet_";
		}
		default: {
			System.out.println("System Error");
			return null;		// 잘못된 번호를 선택
		}
		}
	}

	/* 그래프 생성 (그래프의 생성만을 목적으로 함) */
	private Graph<Integer, DefaultEdge> createGraph(String filePath) throws Exception {
		Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		String s = null;
		int v1 = -1;
		int v2 = -1;
		int idx = 1;

		System.out.println("Now Loading...");
		System.out.println();

		br.readLine();
		try {
			while (true) {
				s = br.readLine();

				v1 = Integer.parseInt(s.split(", ")[1]);
				v2 = Integer.parseInt(s.split(", ")[2]);		// File의 Vertex Number를 Integer로 변환

				if (!g.containsVertex(v1)) {
					g.addVertex(v1);
				}
				if (!g.containsVertex(v2)) {
					g.addVertex(v2);
				}
				if (g.containsEdge(v1, v2)) {		// 기존에 연결되어 있는 경로가 또 나올 경우, File index와 해당 Vertex들을 출력
					System.out.println("***OVERLAP LINE " + idx + "***");
					System.out.println("EDGE : (" + v1 + ", " + v2 + ")");
				}
				if (!g.containsEdge(v1, v2)) {
					g.addEdge(v1, v2);
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

	// 임시 메서드 - bfs (그래프의 연결 유무 체크)
	public void bfs(ArrayList<ArrayList<Integer>> g) {
		Queue<Integer> queue = new LinkedList<Integer>();
		boolean[] isVisited = new boolean[g.size()];
		int start = g.get(1).get(0);
		int count = 0;
		int graphCount = 0;

		while (count != g.size() - 1) {
			queue.add(start);
			isVisited[start] = true;
			// System.out.print(start + " ");
			count++;

			while (!queue.isEmpty()) {
				int v = queue.poll();

				for (int i = 0; i < g.get(v).size(); i++) {
					int thisV = g.get(v).get(i);
					if (!isVisited[thisV]) {
						queue.add(thisV);
						// System.out.print(thisV + " ");
						isVisited[thisV] = true;
						count++;
					}
				}
			}

			graphCount++;
			System.out.print(" <" + count + "> ");
			System.out.println(" [" + graphCount + "] ");

			for (int i = 1; i < g.size(); i++) {
				if (isVisited[i] == false) {
					start = i;
					break;
				}
			}
		}
	}
	
	private int diameter(ArrayList<ArrayList<Integer>> g, int start) {
		int[] d = new int[g.size()];
		Arrays.fill(d, INF);

		PriorityQueue<Vertex> pq = new PriorityQueue<>();
		pq.offer(new Vertex(start, 0));
		
		d[start] = 0;
		int count = 0;
		
		while (!pq.isEmpty()) {
			Vertex next = pq.poll();

			int dist = next.getCost(); // 현재 노드까지의 비용
			int now = next.getIdx(); // 현재 노드 번호

			if (d[now] < dist) {
				continue;
			}

			for (int i = 0; i < g.get(now).size(); i++) {
				// int cost = d[now] + graph.get(now).get(i).getCost();
				int cost = d[now] + 1;

				if (cost < d[g.get(now).get(i)]) {
					d[g.get(now).get(i)] = cost;
					pq.offer(new Vertex(g.get(now).get(i), cost));
				}
			}
		}

		Arrays.sort(d);
		
		return (d[d.length-2] != INF) ? (d[d.length-2]) : 0;
	}
	
	// 1번 메뉴 : Diameter Standard Deviation 결과값 도출
	private void diameterStandardDeviation(ArrayList<ArrayList<Integer>> g) {
		int[] avgDiameter = new int[g.size()];
		double sum = 0.00;
		double avgSum = 0.00;
		double avgDeviation = 0.00;
		int idx = 1;
		System.out.println("Now Loading...");

		for (int i = 1; i <= g.size()-1; i++) {
			avgDiameter[i] = diameter(g, i);
			sum += (double) avgDiameter[i];
			// System.out.println(diameter(g, i));
		}

		System.out.println("**Diameter resolved***");
		System.out.println("Now Loading...");

		avgSum = sum / (g.size()-1);


		for (int i = 1; i <= g.size()-1; i++) {
			avgDeviation += Math.pow(avgDiameter[i] - avgSum, 2);
		}
		
		avgDeviation /= avgSum; // V(X) = 1/n * ∑(xi - m)^2

		/* σ(X) = V(X)^1/2 */
		System.out.println("Graph Diameter Standard Deviation(σ) : " + Math.sqrt(avgDeviation) + " ");
		System.out.println();
	}
	
	/* 2번 메뉴 : Clustering Coefficient 결과값 도출 */
	private void clusteringCoefficient(Graph<Integer, DefaultEdge> graph, ArrayList<ArrayList<Integer>> g) {
		double[] clusteringCoef = new double[g.size()];
		int degree = -1;
		double sum = 0.0;
		int v1 = -1;
		int v2 = -1;
		int ei = -1;

		System.out.println("***Clustering Coefficent***");
		for (int i = 1; i <= g.size()-1; i++) {
			degree = g.get(i).size();
			ei = 0;

			if (degree == 1) {
				clusteringCoef[i] = 0.0;
			} else {
				for (int j = 0; j < degree; j++) {
					for (int k = j+1; k < degree; k++) {
						v1 = g.get(i).get(j);
						v2 = g.get(i).get(k);

						if (graph.containsEdge(v1, v2)) {
							ei++;
						}
					}
				}
				clusteringCoef[i] = (double)(2*ei / (degree*(degree-1)));
			}
		}

		System.out.print("AVERAGE : " + sum / (g.size()-1) + " ");
		System.out.println("(" + sum + " / " + (g.size()-1) + ")");
		System.out.println();
	}
	
	public int edgeCut(ArrayList<ArrayList <Integer>> g, int start, int level) {
		boolean[] isVisit = new boolean[g.size()];
		int i, j, k;
		int count = 0;
		int readCount = 0;
		int edgeCutCount = 0;
		int allEdges = -1;
		int cutEdges = -1;
		int v = -1;
		int thisV = -1;
		boolean isFound = false;

		ArrayList<ArrayList<Integer>> lists = new ArrayList<ArrayList<Integer>>();

		for (i = 0; i <= g.size()-1; i++) {
			lists.add(new ArrayList<Integer>());
		}

		Queue<Integer> queue = new LinkedList<Integer>();

		queue.add(start);
		isVisit[start] = true;
		count = 1;

		i = 1;
		while (i <= level) {
			readCount = 0;

			while (count != 0) {
				v = queue.poll();
				for (j = 0; j < g.get(v).size(); j++) {
					thisV = g.get(v).get(j);
					if (!isVisit[thisV]) {
						lists.get(v).add(thisV);
						lists.get(thisV).add(v);
						queue.add(thisV);
						isVisit[thisV] = true;

						readCount++;
					}
				}
				count--;
			}
			if (i == level) {
				break;
			}
			count = readCount;
			i++;
		}

		for (j = 1; j <= g.size()-1; j++) {
			if (lists.get(j).size() > 0 && j != start) {
				lists.get(j).clear();
				lists.get(j).add(0);
			} else {
				lists.get(j).add(-1);
			}
		}

		while (!queue.isEmpty()) {
			v = queue.poll();
			for (j = 0; j < g.get(v).size(); j++) {
				thisV = g.get(v).get(j);
				if (lists.get(thisV).get(0) == 0) {
					isFound = false;
					for (k = 0; k < lists.get(thisV).size(); k++) {
						if (lists.get(thisV).get(k) == v) {
							isFound = true;
							break;
						}
					}
					if (!isFound) {
						lists.get(thisV).add(v);
					}
				}
			}
			allEdges = g.get(v).size();
			cutEdges = lists.get(v).size();
			edgeCutCount += (allEdges - cutEdges);
		}
		
		System.out.println();
		lists.clear();
		return edgeCutCount;
	}

	/* hop의 level 만큼 노드를 부분 그래프로 분리하기 위해 제거해야 하는 Edge Count 반환*/
	public void printEdgeCount(ArrayList<ArrayList <Integer>> g) {
		int cutCount = 0;
		int level = 2;
		
		for (int j = 1; j <= g.size()-1; j++) {
			cutCount += edgeCut(g, j, level);
		}
		System.out.println((double) cutCount / (g.size()-1));
	}

	private void graphSelectMenu(Graph<Integer, DefaultEdge> graph, ArrayList<ArrayList<Integer>> g, int num) {
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

	private void inputGraphData(ArrayList<ArrayList<Integer>> lists, String str, int idx) {
		int v1 = Integer.parseInt(str.substring(0, str.indexOf(":") - 1));
		int v2 = Integer.parseInt(str.substring(str.indexOf(":") + 2, str.length()));
		if (v1 != idx) {
			lists.get(v2).add(v1);
		} else {
			lists.get(v1).add(v2);
		}
	}

	private ArrayList<ArrayList<Integer>> createAdjList(Graph<Integer, DefaultEdge> g) {
		ArrayList<ArrayList<Integer>> lists = new ArrayList<ArrayList<Integer>>();
		String s = null;
		String str = null;
		int idx = -1;

		for (int i = 0; i <= g.vertexSet().size(); i++) {
			lists.add(new ArrayList<Integer>()); // Graph의 크기만큼 graph (List)를 생성
		}

		for (int i = 1; i < lists.size(); i++) {
			for (int j = 0; j < g.outgoingEdgesOf(i).size(); j++) {
				s = (j == 0) ? (g.outgoingEdgesOf(i).toString()) : (s.substring(idx + 3, s.length()));
				idx = s.indexOf(")");
				str = (j == 0) ? s.substring(2, idx) : s.substring(1, idx);
				inputGraphData(lists, str, i);
			}
		}

		System.out.println(lists.size()); // 그래프의 사이즈를 체크

		return lists;
	}

	private void mainProject() throws Exception {
		ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
		File f = null;
		String graphType = null;
		String filePath = null;
		int data = -1;
		int compare = -1;
		int n = -1;

		while (true) {
			filePath = "F:\\InfoLAB Seminar\\";
			graphType = null;
			data = -1;
			compare = -1;
			n = -1;
			
			while (graphType == null) {
				graphType = printGraphTypeMenu();
			}
			
			filePath += graphType;
			while (!((data > 0) && (compare > 0))) {
				System.out.println("filePath : " + filePath);
				System.out.print("Data / Compare >> ");
				data = numberException();
				compare = numberException();
			}

			filePath += data + "_" + compare + " (1).txt";		// 임시 주소
			f = new File(filePath);
			
			System.out.println(filePath);		
			if (f.exists()) {	 // file checking 유무 검사
				Graph<Integer, DefaultEdge> g = createGraph(filePath); // 그래프의 생성
				graph = createAdjList(g);
				bfs(graph);
				
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
		}
	}

	public static void main(String[] args) throws Exception {
		GraphEvaluation ge = new GraphEvaluation();
		ge.mainProject();
	}
}
