import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphEvaluation {
   private static final int VERTEX = 1000001;
   private static final int INF = (int) 1e9;

   Scanner sc = null;

   int[] d = null;
   int[] avgDiameter = null;

   private int maxV = 0;

   public GraphEvaluation() {
      sc = new Scanner(System.in);
   }

   public void printMenu() {
      System.out.println("1. Display All Vertex Diameter Result");
      System.out.println("2. Display Clustering Coefficient Result");
      System.out.println("3. Display Expansion alpha(α) Result");
      System.out.println("0. Program Exit");
      System.out.print("Choose Number >> ");
   }

   private Graph<Vertex, DefaultEdge> createStringGraph(String filePath, ArrayList<ArrayList<Integer>> graph,
                                                Vertex[] vertex) throws Exception {
      Graph<Vertex, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
      BufferedReader br = new BufferedReader(new FileReader(filePath));

      boolean[] isUsed = new boolean[VERTEX];
      String[] V = null;
      String s = null;
      int v1 = -1;
      int v2 = -1;

      System.out.println("Now Loading...");
      System.out.println();

      br.readLine();
      try {
         while (true) {
            s = br.readLine();
            V = s.split(", ");

            v1 = Integer.parseInt(V[1]);
            v2 = Integer.parseInt(V[2]);
            
            if (!isUsed[v1]) {
               isUsed[v1] = true;
               g.addVertex(vertex[v1]);
            }
            if (!isUsed[v2]) {
               isUsed[v2] = true;
               g.addVertex(vertex[v2]);
            }
            if (g.containsEdge(vertex[v1], vertex[v2])) {
               System.out.println(v1 + " " + v2);
            }
            if (!g.containsEdge(vertex[v1], vertex[v2])) {
               g.addEdge(vertex[v1], vertex[v2]);
               graph.get(v1).add(v2);
               graph.get(v2).add(v1);
            }
            if (maxV < v1) {
               maxV = v1;
            }
            if (maxV < v2) {
               maxV = v2;
            }
         }
      } catch (Exception e) { // EOF(End of File)
         System.out.println("Graph Mapping Complete.");
         System.out.println();
      }

      if (br != null) {
         br.close();
      }
         
      d = new int[maxV + 1];
      avgDiameter = new int[maxV + 1];
      
      // add edges to create a circuit
      return g;
   }

   private int diameter(ArrayList<ArrayList<Integer>> g, int start) {
      Arrays.fill(d, INF);

      PriorityQueue<Vertex> pq = new PriorityQueue<>();

      pq.offer(new Vertex(start, 0));
      d[start] = 0;

      while (!pq.isEmpty()) {
         Vertex node = pq.poll();

         int dist = node.getCost(); // 현재 노드까지의 비용
         int now = node.getIdx(); // 현재 노드 번호

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

      // System.out.println(d[d.length-2]);
      return d[d.length-2];
   }

   /* 1번 메뉴 : Diameter Standard deviation(σ) 결과값 도출 */
   private void diameterStandardDeviation(ArrayList<ArrayList<Integer>> g, int[] avgDiameter) {
      double sum = 0.00;
      double avgSum = 0.00;
      double avgDeviation = 0.00;
      int idx = 1;
      System.out.println("Now Loading...");

      for (int i = 1; i <= maxV; i++) {
         avgDiameter[i] = diameter(g, i);
         sum += (double) avgDiameter[i];
         // System.out.println(diameter(i));
         if ((maxV / 10) * idx < i) {
            //System.out.println("Now Loading..." + idx * 10 + "%");
            idx++;
         }
      }

      System.out.println("**Diameter resolved***");
      System.out.println("Now Loading...");

      avgSum = sum / maxV;

      for (int j = 1; j <= maxV; j++) {
         avgDeviation += Math.pow(avgDiameter[j] - avgSum, 2);
         //System.out.println(j + " " + (avgDiameter[j]-avgSum));
         //System.out.println((Math.pow(avgDiameter[j]-avgSum, 2)));
      }

      avgDeviation /= avgSum; // V(X) = 1/n * ∑(xi - m)^2

      /* σ(X) = V(X)^1/2 */
      System.out.println("Graph Diameter Standard Deviation(σ) : " + Math.sqrt(avgDeviation) + " ");
      System.out.println(); 
   }

   /* 2번 메뉴 : Clustering Coefficient 결과값 도출 */
   private void clusteringCoefficient(Graph<Vertex, DefaultEdge> g, ArrayList<ArrayList<Integer>> graph, 
                                 ArrayList<Double>[] coef, Vertex[] vertex) {
      int maxVertex = 0;
      int degree = -1;
      double sum = 0;
      int v1 = -1;
      int v2 = -1;
      int edgesMax = -1;
      int ei = -1;

      boolean isFound = true;
      while (isFound) {
         ++maxVertex;
         isFound = g.containsVertex(vertex[maxVertex]);
         // System.out.println(maxVertex + " : " + isFound);
      }

      System.out.println("***Clustering Coefficent***");
      for (int i = 1; i < maxVertex; i++) {
         degree = g.degreeOf(vertex[i]);
         edgesMax = graph.get(i).size();
         ei = 0;

         if (degree == 1) {
            coef[i].add(0.0);
         } else {
            for (int j = 0; j < edgesMax; j++) {
               for (int k = j + 1; k < edgesMax; k++) {
                  v1 = graph.get(i).get(j);
                  v2 = graph.get(i).get(k);

                  if (g.containsEdge(vertex[v1], vertex[v2])) {
                     ei++;
                  }
               }
            }
         }
         if (edgesMax != 1) {
            coef[i].add((double) 2 * ei / (edgesMax * (edgesMax - 1)));
            //if (clusteringCoef[i].get(0) > 0.0) {
            //   System.out.print("C" + i + " = " + clusteringCoef[i].get(0) + " ");
            //   System.out.println("(" + ei + " / " + (edgesMax * (edgesMax - 1)) / 2 + ")");
            //}
         }

         sum += coef[i].get(0);
         // System.out.println();
      }

      System.out.print("AVERAGE : " + sum / (maxVertex - 1) + " ");
      System.out.println("(" + sum + " / " + (maxVertex - 1) + ")");
      System.out.println();
   }

   /* 3-1. hop level의 vertex를 모두 출력 후, 해당 hop을 부분 그래프로 만들기 위해 제거해야 하는 edge count return
    * 점수(cost) 미구현(cost data가 존재하지 않기 때문에 주석 처리) */
   
   public int printHop(ArrayList<ArrayList<Integer>> g, Vertex[] vertex, int start) {
      boolean[] isVisit = new boolean[maxV + 1];
      int count = 0;
      int readCount = 0;
      int hopLevel = -1;
      int countRemoveEdge = 0;
      Vertex v = null;
      int thisV = -1;
      int cost = 0;

      Queue<Vertex> queue = new LinkedList<Vertex>();
      // System.out.print("Max Hop Level ? >> ");
      hopLevel = 6; // sc.nextInt();
      
      queue.add(vertex[start]);
      isVisit[start] = true;
      count = 1;
      
      int countNum = 0;

      for (int i = 1; i <= hopLevel; i++) {
         readCount = 0;
         countNum += count;
         System.out.print(countNum);
         // System.out.print("Hop Level " + i + " -> ");
         
         while (count != 0) {
            v = queue.poll();
            for (int j = 0; j < g.get(v.getIdx()).size(); j++) {
               thisV = g.get(v.getIdx()).get(j);
               // cost = vertex[thisV].getCost();
               if (!isVisit[thisV]) {
                  // System.out.print(thisV + " ");
                  queue.add(vertex[thisV]);
                  isVisit[thisV] = true;

                  readCount++;
               }
            }
            count--;
         }
         System.out.println();
         count = readCount;
      }

      while (!queue.isEmpty()) { // 제거해야 하는 Edge의 수를 계산
         v = queue.poll();
         // (Max Hop Level-1) 과 이어져 있지 않은 Edge를 모두 Remove
         countRemoveEdge += g.get(v.getIdx()).size() - 1;
      }

      // System.out.println("Hop Vertex Cost Sum >> " + costSum);
      return countRemoveEdge;
   }

      
   public void printHopLevel(ArrayList<ArrayList<Integer>> g, Vertex[] vertex, int start, int goal) {
      boolean[] isVisit = new boolean[maxV+1];
      int[] distance = new int[maxV+1];
      int i;
      int count = 0;
      int readCount = 0;
      int cutCount = 0;
      Vertex v = null;
      int thisV = -1;
      int foundHopLevel = -1;
      boolean isFound = false;

      Queue<Vertex> queue = new LinkedList<Vertex>();
      
      for (i = 0; i <= maxV; i++) {
         distance[i] = -1;
      }
      
      queue.add(vertex[start]);
      isVisit[start] = true;
      count = 1;
      distance[start] = 0;

      i = 1;
      while (!queue.isEmpty()) {
         readCount = 0;
         System.out.print("Hop Level " + i + " -> ");         
         
         while (count != 0) {
            v = queue.poll();
            for (int j = 0; j < g.get(v.getIdx()).size(); j++) {
               thisV = g.get(v.getIdx()).get(j);
               if (!isVisit[thisV]) {
                  System.out.print(thisV + " ");
                  queue.add(vertex[thisV]);
                  isVisit[thisV] = true;

                  readCount++;
               }
               if (thisV == goal) {
                  foundHopLevel = i;
                  isFound = true;
                  // break;
               }
               if (distance[thisV] == -1) {
                  distance[thisV] = v.getIdx();
               }
            }
            count--;
         }
         System.out.println();
         if (isFound) {
            break;
         }
         count = readCount;
         i++;
      }
      
      int k = goal;
      Stack<Integer> stack = new Stack<Integer>();
      
      while (k != start) {
         if (k == -1) {
            break;
         }
         stack.push(k);
         k = distance[k];
      }
      if (k == -1) {
         stack.clear();
         System.out.println("goal hop이 존재하지 않습니다.");
      } else {
         stack.push(k);
         
         // start에서 goal까지의 최단 경로 출력
         thisV = stack.pop();
         System.out.print(thisV + "->");
         cutCount += g.get(thisV).size()-1;
         
         while (!stack.empty()) {
            thisV = stack.pop();
            System.out.print(thisV);
            if (stack.size() != 0) {
               cutCount += g.get(thisV).size()-2;
               System.out.print("->");
            } else {
               cutCount += g.get(thisV).size()-1;
            }
         }
         
         System.out.println("Goal Vertex By Hop Level >> " + foundHopLevel);
         System.out.println("Remove Linked Edge >> " + cutCount);
      }
   }
   
   /* 3번 메뉴 : Expansion alpha(α) 도출 */
   public void printEdgeCount(Graph<Vertex, DefaultEdge> g, ArrayList<ArrayList<Integer>> graph, Vertex[] vertex) {
      int n = -1;
      int startVertex = -1;
      int goalVertex = -1;

      // 수정중
      System.out.println("1. hop level result");
      System.out.println("2. vertex -> what's hop level");
      System.out.println("3. detail Graph -> edgeCut");
      System.out.print("Choose Number >> ");
      n = sc.nextInt();
      
      switch (n) {
         case 1 : {
            System.out.print("Vertex ? >> ");
            startVertex = sc.nextInt();
            System.out.println("Remove Edge Count >> " + printHop(graph, vertex, startVertex));
            break;
         }
         case 2 : {
            System.out.print("Start Vertex >> ");
            startVertex = sc.nextInt();
            System.out.print("Goal Vertex >> ");
            goalVertex = sc.nextInt();
            printHopLevel(graph, vertex, startVertex, goalVertex);
            break;
         }
         /*
         case 3 : {
            graph = hopGraph();
            cutEdgeCount(g, graph);
         }
         */
      }
   }

   // 임시 메서드 - bfs (그래프의 연결 유무 체크)
   public void bfs(ArrayList<ArrayList<Integer>> g) {
      boolean[] isVisited = new boolean[maxV + 1];
      int start = 1;
      Queue<Integer> queue = new LinkedList<Integer>();
      int count = 0;
      int graphCount = 0;
      
      while (count != maxV) {
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
         System.out.print(" <"+count+"> ");
         System.out.println(" ["+graphCount+"] ");

         for (int i = 1; i <= maxV; i++) {
            if (isVisited[i] == false) {
               start = i;
               break;
            }
         }
      }

   }

   private void mainProject() throws Exception {
      File f = null;
      ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
      ArrayList<Double>[] clusteringCoef = new ArrayList[VERTEX];
      Vertex[] vertex = new Vertex[VERTEX];
      
      boolean isFileCheck = false;

      for (int i = 0; i < VERTEX; i++) {
         graph.add(new ArrayList<Integer>());
         clusteringCoef[i] = new ArrayList<Double>();
         vertex[i] = new Vertex(i, 0);
      }
      
      int data = -1;
      int pairA = -1;
      int pairB = -1;
      
      while (true) {
         System.out.print("Data? >> ");
         data = sc.nextInt();
         System.out.print("Pair Data Range >> ");
         pairA = sc.nextInt();
         pairB = sc.nextInt();
         
         for (int i = pairA; i <= pairB; i++) {
            //for (int j = 1; j <= 10; j++) {
               String filePath = "C:\\Users\\스노우707\\Desktop\\InfoLAB\\randomSetTest\\randomSet_" + data + "_" + i + ".txt"; //" (" + j + ").txt";
               f = new File(filePath);
               // f = new File(filePath + "\\" + graphType + "\\" + graphType + "_" + dataCount
               // + "_" + pairCount + ".txt");
               if (f.exists()) {
                  // filePath += "\\" + graphType + "\\" + graphType + "_" + dataCount + "_" +
                  // pairCount + ".txt";
                  isFileCheck = true;

                  Graph<Vertex, DefaultEdge> g = createStringGraph(filePath, graph, vertex);
                  // int num = -1;

                  bfs(graph);

                  System.out.println(filePath);
                  // Random random = new Random();
                  diameterStandardDeviation(graph, avgDiameter);
                  clusteringCoefficient(g, graph, clusteringCoef, vertex);
                  
                  printHop(graph, vertex, 1);
               } else {
                  System.out.println("File is not exist. Please try again");
                  System.out.println();
               }

               graph.clear();
               graph = new ArrayList<ArrayList<Integer>>();
               for (int k = 0; k < VERTEX; k++) {
                  graph.add(new ArrayList<Integer>());
               }
               for (int k = 0; k < VERTEX; k++) {
                  clusteringCoef[k].clear();
               }
               clusteringCoef = new ArrayList[VERTEX];
               for (int k = 0; k < VERTEX; k++) {
                  clusteringCoef[k] = new ArrayList<Double>();
               }
               maxV = 0;
            }
         // }
         System.out.println("System Quit...");
      }


      // System.out.println(g.vertexSet());
      // System.out.println("System Quit...");
   }

   public static void main(String[] args) throws Exception {
      GraphEvaluation ge = new GraphEvaluation();
      ge.mainProject();
   }
}
