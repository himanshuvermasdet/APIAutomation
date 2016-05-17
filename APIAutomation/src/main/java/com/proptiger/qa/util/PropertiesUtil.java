package com.proptiger.qa.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: Himanshu.Verma
 */

public class PropertiesUtil {

	private static Properties constProperties;
	private static Properties envProperties;

	private static Properties load(String fileName) throws IOException{
		Properties prop = new Properties();
		InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
		prop.load(is);
		System.out.println("File Loaded successfully :: " + fileName);
	 
		return prop;

	}
	public static synchronized void loadConstantFile(String fileName) throws IOException{
		if(constProperties == null)
			constProperties = load(fileName);
		else
			System.out.println("Already Loaded File :: " + fileName);

	}

	public static synchronized void loadEnvConfigFile(String fileName) throws IOException {
		if(envProperties == null)
			envProperties = load(fileName);
		else
			System.out.println("Already Loaded File :: " + fileName);

	}

	public static String getConstantProperty(String property){
		if(constProperties == null)
			return null;
		
		return constProperties.getProperty(property).trim();
	}

	public static String getEnvConfigProperty(String property){
		if(envProperties == null)
			return null;
		
		return envProperties.getProperty(property).trim();
	}
}
