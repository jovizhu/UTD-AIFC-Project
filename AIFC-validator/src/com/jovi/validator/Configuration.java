package com.jovi.validator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.util.Properties;


public class Configuration {

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
			
/*			String policyPath = Configuration.prop.getProperty("policy-path");
			
			File path = new File(policyPath);
			
			//System.out.println("Path is "+path.getPath());
			String[] policyFiles = path.list();

			FilePolicyModule filePolicyModule = new FilePolicyModule();
			for (int i = 0; i < policyFiles.length; i++)
				filePolicyModule.addPolicy(policyPath + "//" + policyFiles[i]);

			// Step 2: Setup the PolicyFinder that this PDP will use
			PolicyFinder policyFinder = new PolicyFinder();
			Set<FilePolicyModule> policyModules = new HashSet<FilePolicyModule>();
			policyModules.add(filePolicyModule);
			policyFinder.setModules(policyModules);

			// create the PDP
			pdp = new PDP(new PDPConfig(null, policyFinder, null));*/
			

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

			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();

			if (classLoader == null) {
				classLoader = Class.class.getClassLoader();
			}

			// InputStream input =
			// classLoader.getResourceAsStream("afic-config.properties");

			File file = new File(configFile);
			FileInputStream input = new FileInputStream(file);

			prop = new Properties();
			prop.load(input);

			if (prop.isEmpty()) {
				System.err.println("Error: ops~~ Config file loaded failed ...");
				return false;
			}

	/*		String policyPath = Configuration.prop.getProperty("policy-path");

			File path = new File(policyPath);

			//System.out.println("Path is " + path.getPath());
			String[] policyFiles = path.list();

			FilePolicyModule filePolicyModule = new FilePolicyModule();
			for (int i = 0; i < policyFiles.length; i++)
				filePolicyModule.addPolicy(policyPath + "//" + policyFiles[i]);

			// Step 2: Setup the PolicyFinder that this PDP will use
			PolicyFinder policyFinder = new PolicyFinder();
			Set<FilePolicyModule> policyModules = new HashSet<FilePolicyModule>();
			policyModules.add(filePolicyModule);
			policyFinder.setModules(policyModules);

			// create the PDP
			pdp = new PDP(new PDPConfig(null, policyFinder, null));*/

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
