import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScoreAlgorithm {
	Scanner sc = null;
	
	public ScoreAlgorithm() {
		sc = new Scanner(System.in);
	}
	
	private void createScoreFile(String filePath, int data) throws IOException {
		int[] arr = new int[data];
		int n = -1;
		int max = data;
		double score = 0.0;
		
		for (int i = 0; i < data; i++) {
			arr[i] = i+1;
		}
		
		Random random = new Random();
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write("id, vertex, score");
		bw.newLine();
		
		for (int i = 1; i <= data/10; i++) {
			n = Math.abs(random.nextInt() % max);
			score = Math.abs((random.nextInt() % 50) / 10.0) + 5.0;
			bw.write(i + ", " + arr[n] + ", " + score);
			bw.newLine();
			
			arr[n] = arr[max-1];	
			max--;
		} /// 9999 9998 9997 9996 9995
		
		bw.flush();
		bw.close();
	}

	private void changeGraphFile(String type, int data, int pair) throws IOException {
		String filePath1 = null;
		String filePath2 = null;
		String s = null;
		int v1 = -1;
		int v2 = -1;
		double score = -1;
		int index = -1;
		// File file1 = new File(filePath1);

		Random rand = new Random();

		for (int i = 2; i <= pair; i++) {
			for (int j = 1; j <= 10; j++) {
				index = 1;
				filePath1 = "F://InfoLAB Seminar//" + type + "//" + type + "_" + data + "_" + i + " (" + j + ").txt";
				filePath2 = "F://InfoLAB Seminar//WeightedGraph//" + type + "//" + type + "_" + data + "_" + i + " ("
						+ j + ").txt";

				File file = new File(filePath2);

				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedReader br = new BufferedReader(new FileReader(filePath1));
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.write("id, data1, data2");
				bw.newLine();

				br.readLine();
				try {
					while (true) {
						s = br.readLine();

						v1 = Integer.parseInt(s.split(", ")[1]);
						v2 = Integer.parseInt(s.split(", ")[2]);
						score = Math.abs((rand.nextInt() % 40) / 10.0) + 1.0;

						bw.write(index + ", " + v1 + ", " + v2 + ", " + score);
						bw.newLine();

						index++;
					}
				} catch (NullPointerException e) {
					System.out.println("CREATE SCORE FILE");
				} finally {
					System.out.println("File Directory -> " + filePath2);
					br.close();
					bw.close();
				}
			}
		}
	}
	
	private void mainProject() throws IOException {
		String type = null;
		int data = -1;
		int pair = -1;
		System.out.println("CREATE SCORE FILE...");
		
		while (true) {
			System.out.print("type / data / pair >> ");
			type = sc.next();
			data = sc.nextInt();
			pair = sc.nextInt();
		
			String filePath = "F://InfoLAB Seminar//scoring//" + type + "_" + data + ".txt";
			// createScoreFile(filePath, data);
			changeGraphFile(type, data, pair);
		}
	}
	
	public static void main(String[] args) throws IOException {
		ScoreAlgorithm sa = new ScoreAlgorithm();
		sa.mainProject();
	}
}
