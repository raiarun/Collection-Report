package com.record.collection.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileParseUtil {

	public static void main(String[] args) {
		File file;
		if(args != null && args.length > 0) {
			file = new File(args[0]);
		}else {
			System.out.println("No input file found.");
			return;
		}
		
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line, bodyContent = "";
			while((line = bf.readLine()) != null) {
				if(!line.contains("<html>") 
						&& !line.contains("</html>")
						&& !line.contains("<body>")
						&& !line.contains("</body>")
						&& !line.contains("<head>")
						&& !line.contains("</head>")){
					bodyContent += line.trim();
				}
			}
			
			bf.close();
			System.out.println(bodyContent.trim());
			
			String finalFile = args[0].split("\\.")[0] + "-Final." + args[0].split("\\.")[1];
			file = new File(finalFile);
			
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(bodyContent.trim());
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
