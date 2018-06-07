package test;

import java.io.FileReader;
import java.util.Scanner;

public class RegExpTesting {
	private static final String testOutput = "./src/test/testOutput.txt";
	
	public static void main(String[] args) {
		Scanner scanFile = null;
		try {
			scanFile = new Scanner(new FileReader(testOutput));
			String tempString = scanFile.nextLine();
			
			System.out.println(tempString);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}
}