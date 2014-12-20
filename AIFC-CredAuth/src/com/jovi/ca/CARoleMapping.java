/**
 * 
 */
package com.jovi.ca;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author DomainA
 *
 */
public class CARoleMapping {

	/**
	 * 
	 */
	public CARoleMapping() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		getDomainRole("DomainC", "Developer", "DomainB");
	}
	public static String getDomainRole(String crossDomain,
			String crossDomainRole, String domainname) {
//		Logger.printLog("getDomainRole( "+ crossDomain + crossDomainRole+currentDomain +")", 3);
		ResultSet rs = null;
		String sqlStatement = "";
		Statement s;
		String accessDomainRole = null;
		java.sql.Connection con;
		
		try {

			con = CADBConnector.getConnectionToDB(domainname);

			//java.sql.Connection con = DBConnector.getConnectionToDB(currentDomain);

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

	//		Logger.printLog("SQL statement "+ sqlStatement, 4);
			
			s.execute(sqlStatement);
			rs = s.getResultSet();

			if (rs != null && rs.next()) {
				accessDomainRole = rs.getString(1);
				System.out.println("Match in role mapping for current request role is "+ accessDomainRole);
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

		//Logger.printLog("CrossDomain Match updated for "+ accessDomainRole, 2);
		return accessDomainRole;
	}


}
