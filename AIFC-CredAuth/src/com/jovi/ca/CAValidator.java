package com.jovi.ca;

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

public class CAValidator {
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String passWord = "person1";
		String userName = "Person1";
		String domainName = "DomainB";

		String credential = CAValidator.loginCA(userName, passWord, domainName);
		System.out.println("credential is " + credential);
		
		credential = "10-24-2014:300:"+"Developer:DomainC";
		String newcredential = CAValidator.validate_credential (credential);
		System.out.println("new credential is " + newcredential);
		
	}
	
	public static String extractRole(String credential){
		String [] credlist = credential.split(":");
		return credlist[2+1];
	}
	
	public static String extractDomain(String credential){
		String [] credlist = credential.split(":");
		return credlist[3+1];
	}
	
	
	public static String validate_credential (String credential) throws SQLException{
		
		String role = null;
		String mapcredential = null;
		String currentDomain = CAConfiguration.domain;
		
		String crossDomain = extractDomain(credential);
		String crossRole = extractRole(credential);
		
		
		if(extractDomain(credential).equals(currentDomain)){
			// credential is in the same domain
			role =  extractRole(credential);
			return "true:"+role;
		}else{
			// not in the same domain
			role = getDomainRole(crossDomain,crossRole, currentDomain);
			
						
			
			
			
			
			
			
			
			
			return "true:"+extractRole(credential)+":10-24-2014:300:"+role+":"+currentDomain;
		}
		
	}
	
	public static String getCredential(String userName, String passWord) throws SQLException{
		String credential = CAValidator.loginCA(userName,  passWord, CAConfiguration.domain);
		return credential;
	};
	
	
	
	public static String loginCA(String userName, String passWord,
			String domainName) throws SQLException {

		String base = /* "ou=People," + */"dc=" + domainName + ",dc=com";

		String dn = "cn=" + userName + "," + base;

		String serverAddress = getLDAPServerAddress(domainName);
		String credential = null;

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
				return null;
			}

			else {

				while (namingEnum.hasMore()) {
					SearchResult result = (SearchResult) namingEnum.next();
					Attributes attrs = result.getAttributes();

					if (domainName.equals(CAConfiguration.domain)) {
						credential = (String) attrs.get("Title").get();
						
					} else {
						System.err
								.println("\033[33m opps~~ The user from a different domain");
						return null;
						/*role = getRoleMapping(domainName,
								(String) attrs.get("Title").get());*/
					}
				}
			}

			namingEnum.close();
		} catch (AuthenticationException authEx) {

			System.err.println("Authentication failed!\n");
			authEx.printStackTrace();
			return null;

		} catch (NamingException namEx) {

			System.err.println("Naming Exception!\n");
			namEx.printStackTrace();
			return null;
		}

		return credential;
	}

	public static String getLDAPServerAddress(String domainName) {

		String str = "";
		java.sql.Connection conn;

		conn = CADBConnector.getConnectionToDB(domainName);

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

	public static String getDomainRole(String crossDomain,
			String crossDomainRole, String currentDomain) {
		System.out.println("getDomainRole( "+ crossDomain + crossDomainRole+currentDomain +")");
		ResultSet rs = null;
		String sqlStatement = "";
		Statement s;
		String accessDomainRole = null;
		
		
		try {

			java.sql.Connection con = CADBConnector.getConnectionToDB(currentDomain);

			if (con == null) {
				System.err
						.println("Error: ops~~ Access Control Role mapping: could not connect to DB");
				return null;
			}

			s = con.createStatement();

			sqlStatement = "SELECT M.Parent_Domain_Role FROM Cross_Domain_Role_Mapping as M "
					+ " WHERE M.Cross_Domain_Name ='"
					+ crossDomain
					+ "' AND M.Cross_Domain_Role = '" + crossDomainRole + "'";

			System.out.println("SQL statement "+ sqlStatement);
			
			s.execute(sqlStatement);
			rs = s.getResultSet();

			if (rs != null && rs.next()) {
				accessDomainRole = rs.getString(1);
				//System.out.println("Match in role mapping for current request role is "+ accessDomainRole);
			} else {
				System.err
						.println("No match in role mapping for current request role: "
								+ crossDomainRole
								+ " from domain: "
								+ crossDomain);
				return null;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("CrossDomain Match updated for "+ accessDomainRole);
		return accessDomainRole;
	}

}
