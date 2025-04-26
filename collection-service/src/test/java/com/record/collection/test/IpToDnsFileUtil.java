package com.record.collection.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IpToDnsFileUtil {

	private static class UrlProperties
	{
		String url = "";
		String project = "";
		String PM = "";
		String IP = "";
		String port = "";
		String Enclave = "";
		String active = "";
		String DNS = "";
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\Arun Rai\\Documents\\JIRAIssues\\RemoveDNSList.csv");
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String line;
		int counter = 0;
		ArrayList<UrlProperties> arrayList = new ArrayList<UrlProperties>();
		ArrayList<String> arr = new ArrayList<String>();
		while((line = bf.readLine()) != null) {
			if(counter > 0) {
				
				String [] splits = line.split(",");
				//System.out.println(Arrays.asList(splits) + " :SIZE " + splits.length);
				//List<String> list = Arrays.asList(splits);
				UrlProperties prop = new UrlProperties();
				prop.url = splits[0];
				prop.project = splits[1];
				prop.PM = splits[2];
				prop.IP = splits[3].trim().length() > 1 ? splits[3].split(":")[0] : "";
				prop.port = splits[3].trim().length() > 1 ? splits[3].split(":")[1] : "";
				prop.Enclave = splits[4];
				prop.active = splits[5];
				if(prop.IP.length() > 0 && !arr.contains(prop.IP+ "," + prop.Enclave)) {
					arr.add(prop.IP+ "," + prop.Enclave);
				}
				
				if(splits.length > 6) {
					prop.DNS = splits[6];
				}
				
				if(splits.length > 7 && "NO".equalsIgnoreCase(splits[7]))
					continue;
				arrayList.add(prop);
			}
			
			counter++;
		}
		
		bf.close();
		
		Collections.sort(arr, new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		        return o1.compareTo(o2);
		    }
		});
		
		for(String st : arr) {
			System.out.println(st);
		}
		
		Collections.sort(arrayList, new Comparator<UrlProperties>() {
		    @Override
		    public int compare(UrlProperties o1, UrlProperties o2) {
		        return o1.IP.compareTo(o2.IP);
		    }
		});
		
		File newFile = new File("C:\\Users\\Arun Rai\\Documents\\JIRAIssues\\RemoveDNSList-Final.csv");
		if(!newFile.exists())
			newFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		String _line = "URL,Project,PM,IP,Port,Enclave,Active,DNS Entry";
		writer.write(_line);
		writer.newLine();
		for(UrlProperties prop : arrayList)
		{
			_line = prop.url + "," + prop.project + "," + prop.PM + "," + prop.IP + "," + prop.port + "," + prop.Enclave + "," + prop.active + "," + prop.DNS;
			writer.write(_line);
			writer.newLine();
		}
		
		for(String st : arr) {
			writer.write(st);
			writer.newLine();
		}
		
		writer.flush();
		writer.close();
	}

}
