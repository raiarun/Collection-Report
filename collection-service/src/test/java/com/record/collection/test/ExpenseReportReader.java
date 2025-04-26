package com.record.collection.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ExpenseReportReader {

	public static void main(String[] args) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader("C:\\Users\\Arun Rai\\Documents\\UNF\\YearEnd_Reports\\expenses-21.csv"));
			String line;
			ArrayList<String> list = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<String[]> items = new ArrayList<String []>();
			while((line = bf.readLine()) != null) {
				String [] splits = line.split(",");
				items.add(splits);
			}
			
			Collections.sort(items, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					return arg0[0].compareTo(arg1[0]);
				}
			});
			
			bf.close();

			String s1 = "Arun";
			String s2 = "Arpan";
			//System.out.println(s2.compareTo(s1));
			for(String[] item : items) {
				System.out.println(Arrays.asList(item).toString().replace("[", "").replace("]", ""));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
