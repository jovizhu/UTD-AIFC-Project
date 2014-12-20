package com.jovi.RESTvalidator;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.jovi.validator.AccessControl;
import com.jovi.validator.RequestRecord;

public class RESTfulAccessControl {


	// @GET here defines, this method will method will process HTTP GET
	// requests.
	@GET
	// @Path here defines method level path. Identifies the URI path that a
	// resource class method will serve requests for.
	@Path("validateRequest")
	// @Produces here defines the media type(s) that the methods
	// of a resource class can produce.
	@Produces(MediaType.TEXT_XML)
	// @PathParam injects the value of URI parameter that defined in @Path
	// expression, into the method.
	public static boolean validateRequest(@QueryParam("accessResource") String accessResource,@QueryParam("accessDomain") String accessDomain,@QueryParam("resourceHierarchy") String resourceHierarchy,@QueryParam("sourceUserName") String sourceUserName,@QueryParam("sourceDomain") String sourceDomain,@QueryParam("sourceRole") String sourceRole,@QueryParam("doAction") String doAction) {
		RequestRecord req = new RequestRecord();

		req.setAccessResource(accessResource);
		req.setAccessDomain(accessDomain);
		req.setResourceHierarchy(resourceHierarchy);
		req.setSourceUserName(sourceUserName);
		req.setSourceDomain(sourceDomain);
		req.setSourceRole(sourceRole);
		req.setDoAction(doAction);
		

		boolean b = AccessControl.validateRequest(req);

		return b;
	}
	
}
