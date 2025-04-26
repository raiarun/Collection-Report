package com.record.collection.test.serialization;

import java.io.File;

public class SerializeDemo implements java.io.Serializable {
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	   @Override
	public String toString() {
		return "SerializeDemo [name=" + name + ", address=" + address + ", number=" + number + "]";
	}

	public String address;
	   public transient int SSN;
	   public int number;
	   public File file;
	   public void mailCheck() {
	      System.out.println("Mailing a check to " + name + " " + address);
	   }

}
