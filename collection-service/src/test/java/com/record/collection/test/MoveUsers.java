package com.record.collection.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MoveUsers {

	public static void main(String[] args) {
		String _file = "C:\\Users\\Arun Rai\\Documents\\TRI-SERVICES\\UserToMove.txt";
		File file = new File(_file);
		try {
			BufferedReader rd = new BufferedReader(new FileReader(file));
			String line;
			while((line = rd.readLine()) != null) {
				String elements [] = line.split(",");
				String commandLine = "Get-ADUser -Identity " + elements[0] + " | " + "Move-ADObject -TargetpPath " + "\"" + "OU=Tri-Services,DC=hoplite,DC=local" + "\"";
				System.out.println(commandLine);
			}
			
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
