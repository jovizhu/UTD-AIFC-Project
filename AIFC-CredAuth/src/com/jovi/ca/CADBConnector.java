package com.jovi.ca;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class CADBConnector {

	

	public static Map<String, java.sql.Connection> connList = new HashMap <String, java.sql.Connection>();

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException {
		// TODO Auto-generated method stub
		// Connection conn = null;
		//Configuration.load(new URL("./config.properties"));
		CADBConnector.getConnectionToDB("domain2");
		System.out.println("2nd time");
		CADBConnector.getConnectionToDB("domain2");
		System.out.println("3rd time");
		CADBConnector.getConnectionToDB("domain2");

	}

	public static Connection getConnectionToDB(String domainName) {
		// System.out.println(domainName);
		try {
			java.sql.Connection conn = connList.get(domainName);

			if (conn == null) {
				Class.forName("com.mysql.jdbc.Driver");
				//System.out.println(CAConfiguration.databaseURL);
				//System.out.println(CAConfiguration.databaseUser);
				//System.out.println(CAConfiguration.databasePassword);

				conn = DriverManager.getConnection("jdbc:mysql://"
						+ CAConfiguration.databaseURL,
						CAConfiguration.databaseUser,
						CAConfiguration.databasePassword);
				connList.put(domainName, conn);
			}
			return conn;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

}
