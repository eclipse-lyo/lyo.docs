/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *     Michael Fiedler      - implementation for Bugzilla adapter
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.services;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Constants;
import org.eclipse.lyo.oslc4j.bugzilla.servlet.ServiceProviderCatalogSingleton;

@OslcService(OslcConstants.OSLC_CORE_DOMAIN)
@Path("catalog")
public class ServiceProviderCatalogService
{
	@Context private HttpServletRequest httpServletRequest;
	@Context private HttpServletResponse httpServletResponse;
	@Context private UriInfo uriInfo;
    @OslcDialog
    (
         title = "Service Provider Catalog Selection Dialog",
         label = "Service Provider Catalog Selection Dialog",
         uri = "/catalog",
         hintWidth = "1000px",
         hintHeight = "600px",
         resourceTypes = {OslcConstants.TYPE_SERVICE_PROVIDER_CATALOG},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcQueryCapability
    (
        title = "Service Provider Catalog Query Capability",
        label = "Service Provider Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + OslcConstants.PATH_SERVICE_PROVIDER_CATALOG,
        resourceTypes = {OslcConstants.TYPE_SERVICE_PROVIDER_CATALOG},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    
    /**
     * Redirect requests to /catalog to /catalog/singleton
     * 
     * By default, OSLC4J returns an OSLC query response for /catalog.  We really just
     * want the catalog itself which lives at /catalog/{serviceProviderCatalogId}
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @GET
    public Response getServiceProviderCatalogs() throws IOException, URISyntaxException
    {
    	String forwardUri = uriInfo.getAbsolutePath() + "/singleton";
    	httpServletResponse.sendRedirect(forwardUri);
        return Response.seeOther(new URI(forwardUri)).build();
    }


    /**
     * Return the OSLC service provider catalog as RDF/XML, XML or JSON
     * 
     * @return
     */
    
    /* LAB 1 - Uncomment this method which returns an RDF/XML, XML or JSON representation of an OSLC service provider catalog
     
    @GET
    @Path("{serviceProviderCatalogId}") // Required to distinguish from array result.  But, ignored.
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ServiceProviderCatalog getServiceProviderCatalog()
    {
        ServiceProviderCatalog catalog =  ServiceProviderCatalogSingleton.getServiceProviderCatalog(httpServletRequest);
        
        if (catalog != null) {

        	httpServletResponse.addHeader(Constants.HDR_OSLC_VERSION,"2.0");
        	return catalog;
        }
        
        throw new WebApplicationException(Status.NOT_FOUND);
    }
    */
    
    /**
     * Return the catalog singleton as HTML
     * 
     * Forwards to serviceprovidercatalog_html.jsp to build the html
     * 
     * @param serviceProviderId
     */
    

    @GET
    @Path("{serviceProviderId}")
    @Produces(MediaType.TEXT_HTML)
    public void getHtmlServiceProvider(@PathParam("serviceProviderId") final String serviceProviderId)
    {
    	ServiceProviderCatalog catalog = ServiceProviderCatalogSingleton.getServiceProviderCatalog(httpServletRequest);
    	

    	if (catalog !=null )
    	{
	        httpServletRequest.setAttribute("bugzillaUri", BugzillaManager.getBugzillaUri());
	        httpServletRequest.setAttribute("catalog",catalog);

	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/serviceprovidercatalog_html.jsp");
    		try {
				rd.forward(httpServletRequest, httpServletResponse);
			} catch (Exception e) {				
				e.printStackTrace();
				throw new WebApplicationException(e);
			} 
    	}
    }



    

}