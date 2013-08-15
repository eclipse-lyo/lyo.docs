/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * * Contributors:
 * 
 *    Susumu Fukuda - Initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.services;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.trs.ChangeLog;
import org.eclipse.lyo.core.trs.EmptyChangeLog;
import org.eclipse.lyo.core.trs.Page;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.core.trs.TrackedResourceSet;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.trs.ChangeBugzillaHistories;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

/*
 * Added in Lab 1.1, Modified in Lab 1.3.
 */
@Path("/trs")
public class TrackedResourceSetService {

	public TrackedResourceSetService() {
	}

	@Context private HttpServletRequest httpServletRequest;
	@Context private HttpServletResponse httpServletResponse;
	@Context private UriInfo uriInfo;
	
	/*
	 * Added in Lab 1.1
	 */
	@GET
	@Produces({"text/plain", "text/html"})
	public String getTrackedResourceSetText() {
		return "Hello TRS service.";
	}
	
	/*
	 * Added in Lab 1.3
	 */
	@GET
    @Produces({OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public TrackedResourceSet getTrackedResourceSet() throws URISyntaxException {
		
		TrackedResourceSet result = new TrackedResourceSet();
		result.setAbout(URI.create(BugzillaManager.getBugzServiceBase() + "/trs"));//$NON-NLS-1$
		result.setBase(URI.create(BugzillaManager.getBugzServiceBase() + "/trs/"+TRSConstants.TRS_TERM_BASE));//$NON-NLS-1$
		
		ChangeBugzillaHistories.buildBaseResourcesAndChangeLogs(httpServletRequest);
		AbstractChangeLog changeLog = (AbstractChangeLog)ChangeBugzillaHistories.getChangeLog("1", httpServletRequest);
		if (changeLog == null) {
			changeLog = new EmptyChangeLog();
		}
		result.setChangeLog(changeLog);
		return result;
	}
	
	/*
	 * Added in Lab 1.3
	 */
	@Path(TRSConstants.TRS_TERM_BASE)
	@GET
    @Produces({OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public Page getBase() {
        URI requestURI = uriInfo.getRequestUri();
        boolean endsWithSlash = requestURI.getPath().endsWith("/");
        String redirectLocation = requestURI.toString() + (endsWithSlash ? "1" : "/1");
        try {
			throw new WebApplicationException(Response.temporaryRedirect(new URI(redirectLocation)).build());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	/*
	 * Added in Lab 1.3
	 */
	@GET
	@Path(TRSConstants.TRS_TERM_BASE+"/{page}")
    @Produces({OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public Page getBasePage(
		@PathParam("page") String pagenum ) {
		Base base;
		try {
			base = ChangeBugzillaHistories.getBaseResource(pagenum, httpServletRequest);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		if (base == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		Page nextPage = base.getNextPage();
		if (nextPage == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		// Due to OSLC4J limitation, not Base but NextPage will be returned.
		// See org.eclipse.lyo.rio.trs.resources.BaseResource.getBasePage(Long)
		return nextPage;
	}

	/*
	 * Added in Lab 1.3
	 */
	@Path(TRSConstants.TRS_TERM_CHANGE_LOG)
	@GET
    @Produces({OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public ChangeLog getChangeLog() {
        URI requestURI = uriInfo.getRequestUri();
        boolean endsWithSlash = requestURI.getPath().endsWith("/");
        String redirectLocation = requestURI.toString() + (endsWithSlash ? "1" : "/1");
        try {
			throw new WebApplicationException(Response.temporaryRedirect(new URI(redirectLocation)).build());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	/*
	 * Added in Lab 1.3
	 */
	@GET
	@Path(TRSConstants.TRS_TERM_CHANGE_LOG+"/{page}")
    @Produces({OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public ChangeLog getChangeLogPage(
		@PathParam("page") String pagenum ) {
		try {
			return ChangeBugzillaHistories.getChangeLog(pagenum, httpServletRequest);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
