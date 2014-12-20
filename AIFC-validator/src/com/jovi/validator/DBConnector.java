package com.jovi.validator;


import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class DBConnector {

	

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
		DBConnector.getConnectionToDB("domain2");
		System.out.println("2nd time");
		DBConnector.getConnectionToDB("domain2");
		System.out.println("3rd time");
		DBConnector.getConnectionToDB("domain2");

	}

	public static Connection getConnectionToDB(String domainName) {
		//System.out.println(domainName);
		try {
			java.sql.Connection conn = connList.get(domainName);
			// Ensure the SQL Server driver class is available.
			if(Configuration.prop == null) Configuration.load();
			
			if(conn == null)
			{
			Class.forName("com.mysql.jdbc.Driver");
/*			System.out.println(Configuration.prop.getProperty(domainName+"-database-URL"));
			System.out.println(Configuration.prop.getProperty(domainName+"-database-user"));
			System.out.println(Configuration.prop.getProperty(domainName+"-database-password"));*/
			
			conn = DriverManager.getConnection("jdbc:mysql://"
					+ Configuration.prop.getProperty(domainName+"-database-URL"),
					Configuration.prop.getProperty(domainName+"-database-user"),
					Configuration.prop.getProperty(domainName+"-database-password"));
			connList.put(domainName, conn);
			}
			return conn;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

}
