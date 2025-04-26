package com.record.collection.test;

import java.util.Calendar;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DateUtilTest {
	public static void main(String[] args) {
		BCryptPasswordEncoder bq = new BCryptPasswordEncoder();
		String st = bq.encode("Marga1Jesus99");
		
		System.out.println(st);
	}
}
