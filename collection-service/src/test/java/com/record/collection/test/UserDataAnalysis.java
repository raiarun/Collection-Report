package com.record.collection.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class UserDataAnalysis {
	public static void main(String[] args) {
		File file = new File("C:\\Users\\Arun Rai\\Documents\\JIRA-Tickets\\FixedFocus\\FIXED-FOCUS_Attr_CAC_USERS.csv");
		File file1 = new File("C:\\Users\\Arun Rai\\Documents\\JIRA-Tickets\\FixedFocus\\FIXED-FOCUS_CAC_USERS.csv");
		
		ArrayList<String> fileUsers = new ArrayList<String>();
		ArrayList<String> file1Users = new ArrayList<String>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line;
			while((line = bf.readLine()) != null) {
				fileUsers.add(line.split(",")[0]);
			}
			
			bf.close();
			
			bf = new BufferedReader(new FileReader(file1));
			while((line = bf.readLine()) != null) {
				file1Users.add(line.split(",")[0]);
			}
			bf.close();
			
			for(String st : fileUsers) {
				if(!file1Users.contains(st)) {
					System.out.println("Does not exist in file1: " + st);
				}
			}
			
			for(String st : file1Users) {
				if(!fileUsers.contains(st)) {
					//System.out.println("Does not exist in file: " + st);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
