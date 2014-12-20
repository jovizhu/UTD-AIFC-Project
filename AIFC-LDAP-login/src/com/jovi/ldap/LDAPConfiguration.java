package com.jovi.ldap;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.util.Properties;


public class LDAPConfiguration {

	//public static PDP pdp = null;

	public static Properties prop = null;
	 



	public static boolean load() {
		try {
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			if (classLoader == null) {
			    classLoader = Class.class.getClassLoader();
			}

			InputStream input = classLoader.getResourceAsStream("afic-config.properties");
			

			prop = new Properties();
			prop.load(input);
			
			if (prop.isEmpty()) {
				System.err.println("Error: ops~~ Config file loaded failed ...");
				return false;
			}
			

		} catch (Exception ex) {
			System.err.println("Error: ops~ loading config file."+ex.getMessage());
			ex.printStackTrace();
			return false;
		}

		System.out.println("****************************************\n"
				+ "Config file loaded successfully...\n"
				+ "****************************************");
		return true;
	}

	
	public static boolean loadfortesting(String configFile) {

		try {

			File file = new File(configFile);
			FileInputStream input = new FileInputStream(file);

			prop = new Properties();
			prop.load(input);

			if (prop.isEmpty()) {
				System.err.println("Error: ops~~ Config file loaded failed ...");
				return false;
			}


		} catch (Exception ex) {
			System.err.println("Error loading config file." + ex.getMessage());
			ex.printStackTrace();
			return false;
		}

		System.out.println("****************************************\n"
				+ "Config file loaded successfully...\n"
				+ "******************************");
		return true;

	}
	
	public static void main(String [] args) throws FileNotFoundException, MalformedURLException
	{
		//Configuration.load(new URL("./config.properties"));
	}
}
