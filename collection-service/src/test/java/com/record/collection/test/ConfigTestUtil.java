package com.record.collection.test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

public class ConfigTestUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String Key_Separator = "<$ks$>";
		String value = "key1 : key2 | value1,morevalue : key3 : key4 : key5 | value2 : key6 | something unique value";
		LinkedHashMap<String, String> configValues = new LinkedHashMap<String, String>();
		
		if(!StringUtils.isEmpty(value)) {
			String configValue = "";
			for(String _value : value.split(":")) {
				if(_value.contains("|")) {
					configValue += _value + ":";
				}else {
					if(configValue.length() < 1)
						configValue = _value + Key_Separator;
					else
						configValue += _value + Key_Separator;
				}
			}
			

			for(String _value : configValue.split(":")) {
				configValues.put(_value.split("\\|")[0].trim().replace(Key_Separator, ":"), _value.split("\\|")[1].trim());
			}
			
			System.out.println(configValues.toString());
		}
		
	}

}
