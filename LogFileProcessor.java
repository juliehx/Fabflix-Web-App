//package project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

public class LogFileProcessor {

	public static void main(String[] args) {
		File file = new File("test");
//		System.out.println(file);
		long tsAverage = 0;
		long tjAverage = 0;
		int numLines = 0;
		try {
			
			Scanner sc = new Scanner(file);
		
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] elapsedList = line.split(" ");
				String[] tsTimeList = elapsedList[0].split(":");
				long tsTime = Long.parseLong(tsTimeList[1]);
				String[] tjTimeList = elapsedList[1].split(":");
				long tjTime = Long.parseLong(tjTimeList[1]);
				tsAverage += tsTime;
				tjAverage += tjTime;
				numLines++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("tsAverage: "+ tsAverage / numLines);
		System.out.println("tjAverage: " + tjAverage / numLines);
		
		
	}

}
