package com.jovi.ldap;


import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class LDAPDBConnector {

	

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
		LDAPDBConnector.getConnectionToDB("domain2");
		System.out.println("2nd time");
		LDAPDBConnector.getConnectionToDB("domain2");
		System.out.println("3rd time");
		LDAPDBConnector.getConnectionToDB("domain2");

	}

	public static Connection getConnectionToDB(String domainName) {
		//System.out.println(domainName);
		try {
			java.sql.Connection conn = connList.get(domainName);
			// Ensure the SQL Server driver class is available.
			if(LDAPConfiguration.prop == null) {
				LDAPConfiguration.load();
			}
			
			if(conn == null)
			{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println(LDAPConfiguration.prop.getProperty(domainName+"-database-URL"));
			System.out.println(LDAPConfiguration.prop.getProperty(domainName+"-database-user"));
			System.out.println(LDAPConfiguration.prop.getProperty(domainName+"-database-password"));
			
			conn = DriverManager.getConnection("jdbc:mysql://"
					+ LDAPConfiguration.prop.getProperty(domainName+"-database-URL"),
					LDAPConfiguration.prop.getProperty(domainName+"-database-user"),
					LDAPConfiguration.prop.getProperty(domainName+"-database-password"));
					connList.put(domainName, conn);
			}
			return conn;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

}
