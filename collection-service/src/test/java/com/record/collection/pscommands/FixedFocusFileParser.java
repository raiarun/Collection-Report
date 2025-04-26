package com.record.collection.pscommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FixedFocusFileParser {
	public static void main(String[] args) {
		String _file = "C:\\Users\\Arun Rai\\Documents\\FixedFocus\\FIXED-FOCUS_ALL_USERS.csv";
		
		String f = "C:\\Users\\Arun Rai\\Documents\\FixedFocus\\FIXED-FOCUS_CAC_USERS.csv";
		File _f = new File(f);
		ArrayList<String> cacUsers = new ArrayList<String>();
		
		try {
			BufferedReader rd = new BufferedReader(new FileReader(_f));
			String line;
			while((line = rd.readLine()) != null) {
				cacUsers.add(line.split(",")[0]);
				if(line.contains("TRUE")) {
					if(!line.toLowerCase().contains("swivel")) {
						//System.out.println(line);
					}else {
						//System.out.println(line);
					}
				}
			}
			rd.close();
		}catch(Exception e1) {}
		
		for(String st : cacUsers) {
			//System.out.println(st);
		}
		File file = new File(_file);
		try {
			BufferedReader rd = new BufferedReader(new FileReader(file));
			String line;
			while((line = rd.readLine()) != null) {
				if(line.contains("TRUE") && !cacUsers.contains(line.split(",")[0])) {
					if(!line.toLowerCase().contains("swivel")) {
						//System.out.println(line);
					}else {
						System.out.println(line);
					}
					//System.out.println(line.split(",")[0]);
				}
			}
			
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
