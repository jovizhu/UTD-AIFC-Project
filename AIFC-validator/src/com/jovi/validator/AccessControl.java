/*********************** AIFC-Validator **************************************
 (C) Copyright 2014 Wei Zhu and Nidhi Solanki
 
 All rights reserved. Use of this software is permitted for non-commercial
 research purposes, and it may be copied only for that use.  All copies must
 include this copyright message.  This software is made available AS IS, and
 neither the authors nor CMU make any warranty about the software or its
 performance.
 *************************************************************************/


package com.jovi.validator;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.sun.xacml.ctx.RequestCtx;


public class AccessControl {
	
	
	private static String getDomainRole(String accessDomain,
			String crossDomainRole, String crossDomain) {

		ResultSet rs = null;
		String sqlStatement = "";
		Statement s;
		String accessDomainRole = null;
		try {

			java.sql.Connection con = DBConnector.getConnectionToDB(accessDomain);

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

		return accessDomainRole;
	}

	// receive read request
	/*
	 * (String sourceDomain, String sourceRole, String doAction, Integer
	 * accessResource, String sourceUserId, String sourceSessionId)
	 */
	public static boolean validateRequest(RequestRecord req) {
		
		long stime = System.currentTimeMillis();
		boolean decision = false;
		boolean provenanceDecision = true;
		String currentDomain = null;
		String domainRole = null;

		try {
			if(Configuration.prop == null) {	
				 Configuration.load();
			}
				
			currentDomain = Configuration.prop.getProperty("current-domain");

			if (!req.getAccessDomain().equals(currentDomain)) {
				domainRole = getDomainRole(req.getAccessDomain(),req.getSourceRole(), currentDomain);
				System.out.println("request domain role has been updated to current domain role "+domainRole);
			} else {
				domainRole = req.getSourceRole();
			}
			
			RequestCtx request = null; // this request will be filled and newed

			if (req.getResourceHierarchy() == null) {
				req.setResourceHierarchy(ResourceOperation.getResourceHierarcy(
						req.getAccessResource(), req.getAccessDomain()));
			}			
				
			request = Request.generateRequest(domainRole, req.getResourceHierarchy(), req.getDoAction());

			long s = System.currentTimeMillis();
			long e = s;
			if (PolicyDecisionPoint.validateRequest(request, req.getAccessDomain()) == 0) {
				e = System.currentTimeMillis();
				decision = true;
			}

			// Step3.5: insert into the request into resource database
			if (decision
					&& !Configuration.prop.getProperty(
							"provenance-accesscontrol").equals("true") ) {
				//System.out.println("step 4.5");
				ResourceOperation.UpdateResouceDB(req);
			}
/*			else if (decision
					&& Configuration.prop.getProperty(
							"accesscontrol-provenance").equals("true")
					&& !Configuration.prop.getProperty("provenance-record")
							.equals("true")) {



				// Step4: if provenance is on: if decision is permit then call
				// provenance to fetch contributors and check their access
				// result
				System.out.println("step 4");
				// delete this note: later create a factory to give all the
				ArrayList<ContributorRecord> cons = ContributorOperations
						.fetchContributors(req.getAccessResource(), req.getAccessDomain());
				if (cons != null)
					provenanceDecision = ProvenanceOperations
							.validateProvenanceBasedAccess(cons, req);
			
				if(provenanceDecision)
				{
					System.out.println("step 4.5");
					ResourceOperation.UpdateResouceDB(req);
					
					System.out.println("step 5");
					ProvenanceOperations.RecordProvenance(req);
				}

			}*/
			else if (decision
					&& Configuration.prop.getProperty(
							"provenance-accesscontrol").equals("true")) {

				ArrayList<ContributorRecord> cons = ContributorOperations
						.fetchContributors(req.getAccessResource(), req.getAccessDomain());
				if (cons.size() > 0)
					provenanceDecision = ProvenanceOperations
							.validateProvenanceBasedAccess(cons, req);

				if (provenanceDecision) {

					ResourceOperation.UpdateResouceDB(req);
					int provenanceRef = ProvenanceOperations.RecordProvenance(req);
					
					if ( (req.getDoAction().equals("CREATE")
							|| req.getDoAction().equals("CREATE") ) && (req.getContributor() != null )) {
						ContributorRecord conRec = new ContributorRecord();
						conRec.setResourceName(req.getAccessResource());
						conRec.setResourceDomain(req.getAccessDomain());
						conRec.setResourceVersion(ResourceOperation
								.getResourceVersion(req.getAccessResource(),
										req.getAccessDomain()));
						conRec.setProvenanceRef(provenanceRef);
						conRec.setPredResourceName(req.getContributor().getResourceName());
						conRec.setPredDomainName(req.getContributor()
								.getResourceDomain());
						conRec.setPredVersion(req.getContributor()
								.getResourceNo());
						ContributorOperations.saveContributor(conRec);
					}
				}

			}

			long etime = System.currentTimeMillis();
			System.out.println("Request Validation "+decision+ " time: " + (e - s));
			System.out.println("Provenance Validation " + (decision && provenanceDecision) + " time: " + (etime - stime));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decision && provenanceDecision;
	}

	public static boolean validateRequestRecurive(RequestRecord req)
	{
		boolean decision = false;
		boolean provenanceDecision = true;
		String currentDomain = null;
		String domainRole = null;

		try {
			if(Configuration.prop == null)
			{	
				 Configuration.load();
			}
				
			currentDomain = Configuration.prop.getProperty("current-domain");

			if (!req.getAccessDomain().equals(currentDomain)) {
				domainRole = getDomainRole(req.getAccessDomain(),req.getSourceRole(), currentDomain);
				System.out.println("request domain role has been updated to current domain role "+domainRole);
			} else {
				domainRole = req.getSourceRole();
			}

			// Step2: generate request
			//System.out.println("step 2");
			RequestCtx request = null; // this request will be filled and newew

			request = Request.generateRequest(domainRole, req.getResourceHierarchy(), req.getDoAction());


			if (PolicyDecisionPoint.validateRequest(request, req.getAccessDomain()) == 0) {
				decision = true;
			}

		
			if (decision) {
				ArrayList<ContributorRecord> cons = ContributorOperations
						.fetchContributors(req.getAccessResource(), req.getAccessDomain());
				if (cons.size() > 0)
					provenanceDecision = ProvenanceOperations
							.validateProvenanceBasedAccess(cons, req);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//System.out.println("res: " + decision + " for " + req.getAccessResource()+" with hier "+req.getResourceHierarchy()+" "+req.getDoAction());
		return decision && provenanceDecision;
	
	}
}
