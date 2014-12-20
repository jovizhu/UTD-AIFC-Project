package com.jovi.validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

;

public class LDAPConnector {
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String passWord = "person1";
		String userName = "Person1";
		String domainName = "domain1";

		Configuration.loadfortesting("afic-config.properties");
		String role = LDAPConnector.getRole(userName, passWord, domainName);
		System.out.println("role is " + role);
	}

	public static String getRole(String userName, String passWord,
			String domainName) throws SQLException {

		String base = /* "ou=People," + */"dc=" + domainName + ",dc=com";

		String dn = "cn=" + userName + "," + base;

		String serverAddress = getLDAPServerAddress(domainName);
		String role = null;

		try {

			Hashtable<String, String> env = new Hashtable<String, String>(11);
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, serverAddress);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, dn);
			env.put(Context.SECURITY_CREDENTIALS, passWord);

			// Create initial context
			DirContext dirctx = new InitialDirContext(env);
			System.out.println("Login-In authenticated");
			dirctx.close();

			// Can be included if needed. this is to fetch the records

			// Object obj = new Object();
			LdapContext ctx = new InitialLdapContext(env, null);

			ctx.setRequestControls(null);
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> namingEnum = ctx.search("dc=" + domainName
					+ ",dc=com", "cn=" + userName, controls);

			if (!namingEnum.hasMore()) {
				System.out.println("No match");
				return "Error: Invalid User";
			}

			else {

				while (namingEnum.hasMore()) {
					SearchResult result = (SearchResult) namingEnum.next();
					Attributes attrs = result.getAttributes();

					if (domainName.equals(Configuration.prop
							.getProperty("current-domain"))) {
						role = (String) attrs.get("Title").get();
					} else {
						System.out
								.println("\033[33m opps~~ The user from a different domain");
						role = getRoleMapping(domainName,
								(String) attrs.get("Title").get());
					}
				}
			}

			namingEnum.close();
		} catch (AuthenticationException authEx) {

			System.err.println("Authentication failed!\n");
			authEx.printStackTrace();
			role = null;

		} catch (NamingException namEx) {

			System.err.println("Naming Exception!\n");
			role = null;
			namEx.printStackTrace();
		}

		return role;
	}

	public static String getLDAPServerAddress(String domainName) {

		String str = "";
		java.sql.Connection conn;

		conn = DBConnector.getConnectionToDB(domainName);

		ResultSet rs = null;
		String sqlStatement = "";
		Statement s;
		try {
			s = conn.createStatement();

			// sqlStatement="SELECT URL,Port_No FROM LDAP_Server WHERE Domain_Name ='"+Domain+"'";
			// there should only be one ldap server for each domain---nto
			// changing for now but will be changed in future
			sqlStatement = "SELECT URL,Port_No FROM LDAP_Server WHERE Domain_Name ='"
					+ domainName + "'";

			s.execute(sqlStatement);
			rs = s.getResultSet();

			while ((rs != null) && (rs.next())) // iterates for all the
												// permission a
												// role has
			{
				str = "ldap://" + rs.getString(1) + ":" + rs.getString(2);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;
	}

	public static String getRoleMapping(String domainName, String role) {
		String userRole = null;

		java.sql.Connection conn;
		conn = DBConnector.getConnectionToDB(domainName);
		String sqlStatement = "";
		Statement s = null;
		ResultSet rs = null;

		try {
			s = conn.createStatement();
			sqlStatement = "SELECT M.Parent_Domain_Role FROM Cross_Domain_Role_Mapping as M JOIN LDAP_Server as L ON  M.Cross_Domain_Name = L.Domain_Name"
					+ " WHERE L.Domain_Name ='"
					+ domainName
					+ "' AND M.Cross_Domain_Role = '" + role + "'";

			s.execute(sqlStatement);
			rs = s.getResultSet();
			if (rs != null && rs.next()) {
				userRole = rs.getString(1);
				System.out.println("The match in our domain is " + userRole);

			} else {
				userRole = null;
				System.out.println("Error: No valid role");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userRole;

	}

	/*
	 * private static SearchControls getSimpleSearchControls() { SearchControls
	 * searchControls = new SearchControls();
	 * searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	 * searchControls.setTimeLimit(30000); // String[] attrIDs = {"objectGUID"};
	 * // searchControls.setReturningAttributes(attrIDs); return searchControls;
	 * }
	 */

}
