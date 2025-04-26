package com.record.collection.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CollectionRecordControllerTest {

	public static void main(String[] args) {
		ArrayList<String> contributors = new ArrayList<String>();
		contributors.add("john");
		contributors.add("peter");
		contributors.add("sam");
		contributors.add("bradley");
		
		//System.out.println(contributors.toString());
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String timeStamp = stamp.toString().replace(":", "").replace(".", "").replace(" ", "-");
		
		//System.out.println(timeStamp);
	}

}
