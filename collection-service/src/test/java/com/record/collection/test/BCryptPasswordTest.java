package com.record.collection.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String pass = "$2a$10$PoDI5MprGZ2SMAYMlyju6uArxWTCVMtsAX09mzTFsMoSibmVxL59i";
		
		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		System.out.println(bc.encode("Marga1Jesus99"));
	}

}
