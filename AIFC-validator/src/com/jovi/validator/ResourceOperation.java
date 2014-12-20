package com.jovi.validator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceOperation {
	
	public static java.sql.Connection con = null;

	public static int getResourceId(String rescoureName, String accessDomain) {

		int resoureId = -1;
				con = DBConnector.getConnectionToDB(accessDomain);
		

		String selectQuery = "select Resource_Id from resource where Resource_Name = ?";
		java.sql.PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(selectQuery);
			preparedStmt.setString(1, rescoureName);

			//System.out.println(preparedStmt.toString());
			ResultSet rs = preparedStmt.executeQuery();
			while (rs.next()) {

				resoureId = rs.getInt("Resource_Id");
			}// end while
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resoureId;
	}
	
	public static String getResourceHierarcy(String resourceName, String accessDomain) {

		String resoureHierarchy = null;

				con = DBConnector.getConnectionToDB(accessDomain);
	

		String selectQuery = "select Resource_Hierarchy from resource where Resource_Name = ?";
		java.sql.PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(selectQuery);
			preparedStmt.setString(1, resourceName);

			//System.out.println(preparedStmt.toString());
			ResultSet rs = preparedStmt.executeQuery();
			while (rs.next()) {

				resoureHierarchy = rs.getString("Resource_Hierarchy");
			}// end while
			
			if(resoureHierarchy == null)
			{
				resoureHierarchy = resourceName;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Resource Hier get "+resoureHierarchy);
		return resoureHierarchy;
	}
	public static int getResourceVersion(String rescoureName, String accessDomain) {

		int resoureNo = -1;
		try {
			// get connection to domain DB
			if (con == null) {
				con = DBConnector.getConnectionToDB(accessDomain);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String selectQuery = "select Resource_Version from resource where Resource_Name = ?";
		java.sql.PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(selectQuery);
			preparedStmt.setString(1, rescoureName);

			//System.out.println(preparedStmt.toString());
			ResultSet rs = preparedStmt.executeQuery();
			while (rs.next()) {
				resoureNo = rs.getInt("Resource_Version");
			}// end while
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resoureNo;
	}
	
	public static boolean UpdateResouceDB(RequestRecord req) {
		boolean result = false;
		try {
			// get connection to domain DB
		
				con = DBConnector.getConnectionToDB(req.getAccessDomain());
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (req.getDoAction().equals("CREATE")) {
			int insertResult = 0;
			String insertQuery = "insert into resource (Resource_Name, Domain_Name, Resource_Version, Resource_Hierarchy) VALUES (?,?,?,?)";
			java.sql.PreparedStatement preparedStmt;
			try {
				preparedStmt = con.prepareStatement(insertQuery);
				preparedStmt.setString(1, req.getAccessResource());
				preparedStmt.setString(2, req.getAccessDomain());
				preparedStmt.setInt(3, 1);
				preparedStmt.setString(4, req.getResourceHierarchy());
				

				//System.out.println(preparedStmt.toString());
				insertResult = preparedStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(insertResult == 0)
			{
				result = false;
			}
			else
			{
				result = true;
			}

			return result;
		}else if (req.getDoAction().equals("UPDATE")) {
			int updateResult = 0;
			String insertQuery = "update resource set Resource_Version =? where Resource_Name = ?";
			java.sql.PreparedStatement preparedStmt;
			try {
				preparedStmt = con.prepareStatement(insertQuery);
				
				int newVersion = 1+ ResourceOperation.getResourceVersion(req.getAccessResource(), req.getAccessDomain());
				//System.out.println("New Version is "+ newVersion);
				preparedStmt.setInt(1, newVersion);
				preparedStmt.setString(2, req.getAccessResource());
				
				//System.out.println(preparedStmt.toString());
				updateResult = preparedStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(updateResult == 0)
			{
				result = false;
			}
			else
			{
				result = true;
			}

			return result;
		
			
		}else if (req.getDoAction().equals("DELETE")) {

			int delResult = 0;
			
			try {
				String delQuery = "delete from resource where Resource_Name = ?";
				java.sql.PreparedStatement preparedStmt;
				preparedStmt = con.prepareStatement(delQuery);
				preparedStmt.setString(1, req.getAccessResource());
				//System.out.println(preparedStmt.toString());
				delResult = preparedStmt.executeUpdate();
				
				
				String delQuery2 = "delete from resource_operation_contributor where Resource_Name = ?";
				java.sql.PreparedStatement preparedStmt2;
				preparedStmt2 = con.prepareStatement(delQuery2);
				preparedStmt2.setString(1, req.getAccessResource());
				//System.out.println(preparedStmt.toString());
				preparedStmt2.executeUpdate();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(delResult == 0)
			{
				result = false;
			}
			else
			{
				result = true;
			}

			return result;
		
			
		}else if (req.getDoAction().equals("READ")) {
			
		}
		else
		{
			System.out.println("Non supported operation for resource");
		}
		
		return result;
	}

}
