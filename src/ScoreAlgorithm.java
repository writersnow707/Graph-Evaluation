package writersnow707.scoreAlgorithm;

import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
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
			score = Math.abs((random.nextInt() % 11) / 5.0);
			bw.write(i + ", " + arr[n] + ", " + score);
			bw.newLine();
			
			arr[n] = arr[max-1];	
			max--;
		} /// 9999 9998 9997 9996 9995
		
		bw.flush();
		bw.close();
	}
	
	private void mainProject() throws IOException {
		String type = null;
		int data = -1;
		System.out.println("CREATE SCORE FILE...");
		
		while (true) {
			type = sc.next();
			data = sc.nextInt();
		
			String filePath = "F://InfoLAB Seminar//scoring//" + type + "_" + data + ".txt";
			createScoreFile(filePath, data);
		}
	}
	
	public static void main(String[] args) throws IOException {
		ScoreAlgorithm sa = new ScoreAlgorithm();
		sa.mainProject();
	}
}