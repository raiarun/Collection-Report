package com.record.collection.pscommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PSCommandGenerator {

	public static void main(String[] args) {
		String _file = "C:\\Users\\Arun Rai\\Documents\\TRI-SERVICES\\UpdateAttributes-SUN-14083.txt";
		File file = new File(_file);
		try {
			BufferedReader rd = new BufferedReader(new FileReader(file));
			String line;
			while((line = rd.readLine()) != null) {
				String elements [] = line.split(",");
				String commandLine = "Set-ADUser -Identity " + elements[0] + " -Replace @{extensionAttribute14=\"" + elements[1] + "\";extensionAttribute13=\""+ elements[2] + "\"}";
				System.out.println(commandLine);
			}
			
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
