package com.AIFC.test;


import com.jovi.ca.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

public class ClientProg {

	
	public static String applicationDatabaseUrl = null;
	public ClientProg() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String configfilePath = "config.properties";
		String contributorlist = null;
		
		
	
				
		String cred = CADBValidator.loginCA("Person1", "person1","R_D_Director", "DomainB");
		System.out.println("Credential:: " + cred );
		
		String[] creds = cred.split(":");
	
		loadConfigurationFile(configfilePath);

		Service s = new Service();
		
		long stime1 = System.currentTimeMillis();			
		s.create_robot("Robot_new", "DomainB", "RobotRepository","Person1","DomainB", creds[2],contributorlist,cred);
		long etime1 = System.currentTimeMillis();
		System.out.println("Start Time = " + stime1 +" End Time = " + etime1 +" Total Time= " + (etime1 - stime1) );
		
		
		long stime19 = System.currentTimeMillis();
		s.read_robot("Robot_new", "DomainB", "Person1", creds[2],creds[3], cred);
		long etime19 = System.currentTimeMillis();
		System.out.println("Start Time = " + stime19 +" End Time = " + etime19 +" Total Time= " + (etime19 - stime19) +"\n");
	}

	public static boolean loadConfigurationFile(String configFile) {

		try {

			File file = new File(configFile);
			FileInputStream input = new FileInputStream(file);
			Properties prop = null;
			prop = new Properties();
			prop.load(input);

			if (prop.isEmpty()) {
				System.err.println("Error: ops~~ Config file loaded failed ...");
				return false;
			}
			
			CAConfiguration.databaseUser = prop.getProperty("database-user");
			CAConfiguration.databasePassword = prop.getProperty("database-password");
			CAConfiguration.databaseURL = prop.getProperty("aifc-database-URL");
			CAConfiguration.domain = prop.getProperty("current-domain");
			applicationDatabaseUrl = prop.getProperty("application-database-url");
			System.out.print("Config File loaded");

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

}
