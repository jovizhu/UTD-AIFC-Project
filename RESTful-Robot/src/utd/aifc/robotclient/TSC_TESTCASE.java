package utd.aifc.robotclient;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

import utd.aifc.credauth.CAConfiguration;
import utd.aifc.credauth.CADBValidator;

public class TSC_TESTCASE {

	/**
	 * 
	 */
	public static String applicationDatabaseUrl = null;
	
	public TSC_TESTCASE() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		String configfilePath = "config.properties";
		String contributorlist = null;
		
		loadConfigurationFile(configfilePath);

/*			String cred = CADBValidator.loginCA("Bio2", "Bio2", "Biologist","DomainB");
		//String cred = CAValidator.loginCA("Person1", "person1", "DomainB");
		System.out.println("Credential from Jovi:: " + cred );
		
		String[] creds = cred.split(":");


*/
		Service s = new Service();

		long stime01 = System.currentTimeMillis();
		s.receive_robot("Promoter", "DomainB", "Bio2","Biologist","DomainB");
		long etime01 = System.currentTimeMillis();
		System.out.println("Start Time = " + stime01 +" End Time = " + etime01 +" Total Time= " + (etime01 - stime01) );
		
		
		
		
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
