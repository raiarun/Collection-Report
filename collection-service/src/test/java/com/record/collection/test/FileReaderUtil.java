package com.record.collection.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileReaderUtil {

	public static void main(String[] args) {
		
		try {
			BufferedReader bf = new BufferedReader(new FileReader("C:\\Users\\Arun Rai\\Documents\\UNF\\YearEnd_Reports\\stmt.csv"));
			String line;
			ArrayList<String> list = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			double  total = 0.0, collected = 0.0, zoom = 0.0;
			while((line = bf.readLine()) != null) {
				String [] splits = line.split(",");
				if(splits.length > 4) {
					double value = Double.valueOf(splits[4]);
					
					if(value < 0) {
						//System.out.println(line);
						total += value;
						System.out.println(splits[0]+ ", " + splits[4]  + ", " + splits[1] );
					}
					if(Double.valueOf(splits[5]) > 0){
						collected += Double.valueOf(splits[5]); 
					}
					
					if(line.contains("ZOOM")) {
						zoom += Double.valueOf(splits[4]);
					}
				}
				
			}
			
			System.out.println("total expense : " + total + ", collected: " + collected + ", zoom: " + zoom);

			bf.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
