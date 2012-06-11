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
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.lyo.oslc4j.bugzilla.Constants;
import org.eclipse.lyo.oslc4j.bugzilla.jbugzx.rpc.GetLegalValues;
import org.eclipse.lyo.oslc4j.bugzilla.resources.BugzillaChangeRequest;
import org.eclipse.lyo.oslc4j.bugzilla.resources.ChangeRequest;
import org.eclipse.lyo.oslc4j.bugzilla.servlet.ServiceProviderCatalogSingleton;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;



import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.BugSearch;


@OslcService(Constants.CHANGE_MANAGEMENT_DOMAIN)
@Path("{productId}/changeRequests")
public class BugzillaChangeRequestService
	
{

	@Context private HttpServletRequest httpServletRequest;
	@Context private HttpServletResponse httpServletResponse;
	@Context private UriInfo uriInfo;
	
    public BugzillaChangeRequestService()
    {
        super();
    }

    /**
     * RDF/XML, XML and JSON representation of a change request collection
     * 
     * TODO:  add query support
     * 
     * @param productId
     * @param where
     * @param pageString
     * @return
     * @throws IOException
     * @throws ServletException
     */
    
  @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Change Request Selection Dialog",
             label = "Change Request Selection Dialog",
             uri = "/{productId}/changeRequests/selector",
             hintWidth = "525px",
             hintHeight = "325px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        )

    })   
    @OslcQueryCapability
    (
        title = "Change Request Query Capability",
        label = "Change Request Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST,
        resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    ) 
      
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public BugzillaChangeRequest[] getChangeRequests(@PathParam("productId")   final String productId,
    		                                 		 @QueryParam("oslc.where") final String where,
    		                                 		 @QueryParam("page")       final String pageString) throws IOException, ServletException 
    {
	    /* LAB 2 - Uncomment this code which returns an RDF/XML, XML or JSON representation of a collection of BugzillaChangeRequests

	  
    	int page=0;  
    	int limit=999;
        

        final List<Bug> bugList = BugzillaManager.getBugsByProduct(httpServletRequest, productId, page, limit);      
        final List<BugzillaChangeRequest> results = changeRequestsFromBugList(httpServletRequest, bugList, productId);

        return results.toArray(new BugzillaChangeRequest[results.size()]);
        
        */
	    return null; //LAB 2 - remove this line
    }
    
    /**
     * HTML representation of change request collection
     * 
     * Forwards to changerequest_collection_html.jsp to build the html page
     * 
     * @param productId
     * @param changeRequestId
     * @param pageString
     * @return
     * @throws ServletException
     * @throws IOException
     */
    
    /*LAB 2 - Uncomment this collection which returns an HTML collection of BugzillaChangeRequests

	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getHtmlCollection(@PathParam("productId")       final String productId,
			                          @PathParam("changeRequestId") final String changeRequestId,
			                          @QueryParam("page")           final String pageString) throws ServletException, IOException
	{
		int page=0;
		int limit=20;
		
		if (null != pageString) {
			page = Integer.parseInt(pageString);
		}
		
		List<Bug> bugList = BugzillaManager.getBugsByProduct(httpServletRequest, productId, page, limit);
		final List<BugzillaChangeRequest> results = changeRequestsFromBugList(httpServletRequest, bugList, productId);

        if (bugList != null) {
        	final String bugzillaUri = BugzillaManager.getBugzillaUri().toString();
        	httpServletRequest.setAttribute("results", results);
        	httpServletRequest.setAttribute("bugzillaUri", bugzillaUri);

        	httpServletRequest.setAttribute("queryUri", 
                    uriInfo.getAbsolutePath().toString() + "?oslc.paging=true");
        	if (results.size() > limit) {
        		results.remove(results.size() - 1);
        		httpServletRequest.setAttribute("nextPageUri", 
        				uriInfo.getAbsolutePath().toString() + "?oslc.paging=true&amp;page=" + (page + 1));
        	}
        	
        	ServiceProvider serviceProvider = ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId);
        	httpServletRequest.setAttribute("serviceProvider", serviceProvider);

        	RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_collection_html.jsp");
        	rd.forward(httpServletRequest,httpServletResponse);
        }
		
		throw new WebApplicationException(Status.NOT_FOUND);	
	}

	*/
    
	/**
	 * RDF/XML, XML and JSON representation of a single change request
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws URISyntaxException
	 */
   
  /* LAB 2  - Uncomment this method which returns an RDF/XML, XML or JSON representation of a BugzillaChangeRequest

  @GET
  @Path("{changeRequestId}")
  @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
  public BugzillaChangeRequest getChangeRequest(@PathParam("productId")       final String productId,
                                                @PathParam("changeRequestId") final String changeRequestId) throws IOException, ServletException, URISyntaxException
  {
      final Bug bug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);

      if (bug != null) {
      	BugzillaChangeRequest changeRequest = null;

      	changeRequest = BugzillaChangeRequest.fromBug(bug);
     	
      	changeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId).getAbout());
      	changeRequest.setAbout(getAboutURI(productId + "/changeRequests/" + changeRequest.getIdentifier()));
      	setETagHeader(getETagFromChangeRequest(changeRequest), httpServletResponse);

          return changeRequest;
      }

      throw new WebApplicationException(Status.NOT_FOUND);
  }
	*/


  
  /**
   * 
   * HTML representation for a single change request  - redirect the request directly to Bugzilla
   * 
   * @param productId
   * @param changeRequestId
   * @throws ServletException
   * @throws IOException
   * @throws URISyntaxException
   */
  
   /* LAB 2  - Uncomment this method which returns an RDF/XML, XML or JSON representation of a BugzillaChangeRequest

	@GET
	@Path("{changeRequestId}")
	@Produces({ MediaType.TEXT_HTML })
	public Response getHtmlChangeRequest(@PathParam("productId")       final String productId,
			                         @PathParam("changeRequestId") final String changeRequestId) throws ServletException, IOException, URISyntaxException
	{	
    String forwardUri = BugzillaManager.getBugzillaUri() + "show_bug.cgi?id=" + changeRequestId;
	  httpServletResponse.sendRedirect(forwardUri);
    return Response.seeOther(new URI(forwardUri)).build();			
	}
	*/
	
	/**
	 * OSLC delegated selection dialog for change requests
	 * 
	 * If called without a "terms" parameter, forwards to changerequest_selector.jsp to 
	 * build the html for the IFrame
	 * 
	 * If called with a "terms" parameter, sends a Bug search to Bugzilla and then 
	 * forwards to changerequest_filtered_json.jsp to build a JSON response
	 * 
	 * 
	 * @param terms
	 * @param productId
	 * @throws ServletException
	 * @throws IOException
	 */
	
	/* LAB 3 - Uncomment this method which provides a service for an OSLC delegated selection dialog

	@GET
	@Path("selector")
	@Consumes({ MediaType.TEXT_HTML, MediaType.WILDCARD })
	public void changeRequestSelector(@QueryParam("terms")     final String terms,
						              @PathParam("productId")  final String productId) throws ServletException, IOException
	{
		int productIdNum = Integer.parseInt(productId);
		httpServletRequest.setAttribute("productId", productIdNum);
		httpServletRequest.setAttribute("bugzillaUri", BugzillaManager.getBugzillaUri());
		httpServletRequest.setAttribute("selectionUri",uriInfo.getAbsolutePath().toString());

		if (terms != null ) {
			httpServletRequest.setAttribute("terms", terms);
			sendFilteredBugsReponse(httpServletRequest, productId, terms);

		} else {
			try {	
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_selector.jsp"); 
	    		rd.forward(httpServletRequest, httpServletResponse);
				
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
				
	}
	
	*/
    
    
    /**
     * OSLC delegated creation dialog for a single change request
     * 
     * Forwards to changerequest_creator.jsp to build the html form
     * 
     * @param productId
     * @throws IOException
     * @throws ServletException
     */
	
	/* LAB 3 - Uncomment this method which provides an OSLC delegated creation dialog
    @GET
    @Path("creator") 
    @Consumes({MediaType.WILDCARD})
    public void changeRequestCreator(@PathParam("productId") final String productId) throws IOException, ServletException
    {
    	try {				

			BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			Product product = BugzillaManager.getProduct(httpServletRequest, productId);
			
			httpServletRequest.setAttribute("product", product);
			
			GetLegalValues getComponentValues = 
				new GetLegalValues("component", product.getID());
			bc.executeMethod(getComponentValues);
			List<String> components = Arrays.asList(getComponentValues.getValues());
			httpServletRequest.setAttribute("components", components);
			
			GetLegalValues getOsValues = new GetLegalValues("op_sys", -1);
			bc.executeMethod(getOsValues);
			List<String> operatingSystems = Arrays.asList(getOsValues.getValues());
			httpServletRequest.setAttribute("operatingSystems", operatingSystems);
			
			GetLegalValues getPlatformValues = new GetLegalValues("platform", -1);
			bc.executeMethod(getPlatformValues);
			List<String> platforms = Arrays.asList(getPlatformValues.getValues());
			httpServletRequest.setAttribute("platforms", platforms);
			
			GetLegalValues getVersionValues = new GetLegalValues("version", product.getID());
			bc.executeMethod(getVersionValues);
			List<String> versions = Arrays.asList(getVersionValues.getValues());
			httpServletRequest.setAttribute("versions", versions);
			
			httpServletRequest.setAttribute("creatorUri", uriInfo.getAbsolutePath().toString());
	        httpServletRequest.setAttribute("bugzillaUri", BugzillaManager.getBugzillaUri());

	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_creator.jsp");
    		rd.forward(httpServletRequest, httpServletResponse);
			
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
    	
    }
    */
 

    /**
     * Backend creator for the OSLC delegated creation dialog. 
     * 
     * Accepts the input in FormParams and returns a small JSON response
     * 
     * @param productId
     * @param component
     * @param version
     * @param summary
     * @param op_sys
     * @param platform
     * @param description
     */
    
    /* LAB 3 - Uncomment this method which provides a service to create a new BugzillaChangeRequest from a POSTed form

    @POST
    @Path("creator") 
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
    public void createHtmlChangeRequest(    @PathParam("productId")   final String productId,     
                						    @FormParam("component")   final String component,
                						    @FormParam("version")     final String version,
                						    @FormParam("summary")     final String summary,
                						    @FormParam("op_sys")      final String op_sys,
                						    @FormParam("platform")    final String platform,
                						    @FormParam("description") final String description)
    {
    	
    	
    	try {
    		BugzillaChangeRequest changeRequest = new BugzillaChangeRequest();
    		changeRequest.setComponent(component);
    		changeRequest.setVersion(version);
    		changeRequest.setTitle(summary);
    		changeRequest.setOperatingSystem(op_sys);
    		changeRequest.setPlatform(platform);
    		changeRequest.setDescription(description);
    	
    		final String newBugId = BugzillaManager.createBug(httpServletRequest, changeRequest, productId);
    		
         
    		final Bug newBug = BugzillaManager.getBugById(httpServletRequest, newBugId);
    		final BugzillaChangeRequest newChangeRequest = BugzillaChangeRequest.fromBug(newBug);
    		URI about = getAboutURI(productId + "/changeRequests/" + newBugId);
            newChangeRequest.setAbout(about);
    		
    		httpServletRequest.setAttribute("changeRequest", newChangeRequest);
    		httpServletRequest.setAttribute("changeRequestUri", newChangeRequest.getAbout().toString());
    		
    		// Send back to the form a small JSON response
    		httpServletResponse.setContentType("application/json");
    		httpServletResponse.setStatus(Status.CREATED.getStatusCode());
    		httpServletResponse.addHeader("Location", newChangeRequest.getAbout().toString());
    		PrintWriter out = httpServletResponse.getWriter();
    		out.print("{\"title\": \"" + getChangeRequestLinkLabel(newBug.getID(), summary) + "\"," +
    				"\"resource\" : \"" + about + "\"}");
    		out.close();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new WebApplicationException(e);
    	}

    }
    
    */
	
    /**
     * OSLC Compact representation of a single change request
     * 
     * Contains a reference to the smallPreview method in this class for the preview document
     * 
     * @param productId
     * @param changeRequestId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ServletException
     */
    
    /* LAB 4 - Uncomment this method which provides an OSLC compact XML representation of a BugzillaChangeRequest

    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML})
    public Compact getCompact(@PathParam("productId") final String productId,
                              @PathParam("changeRequestId") final String changeRequestId)
           throws URISyntaxException, IOException, ServletException
    {
    	final Bug bug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);
        

        if (bug != null) {      	
            final Compact compact = new Compact();
            
        	BugzillaChangeRequest changeRequest = null;

       		changeRequest = BugzillaChangeRequest.fromBug(bug);
 	
            compact.setAbout(getAboutURI(productId + "/changeRequests/" + changeRequest.getIdentifier()));
            compact.setTitle(changeRequest.getTitle());
            
            String iconUri = BugzillaManager.getBugzillaUri() + "/images/favicon.ico";
            compact.setIcon(new URI(iconUri));
 
            //Create and set attributes for OSLC preview resource
            final Preview smallPreview = new Preview();
            smallPreview.setHintHeight("11em");
            smallPreview.setHintWidth("45em");
            smallPreview.setDocument(new URI(compact.getAbout().toString() + "/smallPreview"));
            compact.setSmallPreview(smallPreview);

            return compact;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }
    
    */
	
	/**
	 * OSLC small preview for a single change request
	 * 
	 * Forwards to changerequest_preview_small.jsp to build the html
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	
	/* LAB 4 - Uncomment this method which provides an OSLC small preview of a BugzillaChangeRequest

	@GET
	@Path("{changeRequestId}/smallPreview")
	@Produces({ MediaType.TEXT_HTML })
	public void smallPreview(@PathParam("productId")       final String productId,
			                 @PathParam("changeRequestId") final String changeRequestId) throws ServletException, IOException, URISyntaxException
	{
		
        final Bug bug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);
        
        if (bug != null) {
        	
        	BugzillaChangeRequest changeRequest = BugzillaChangeRequest.fromBug(bug);
        	changeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId).getAbout());
        	changeRequest.setAbout(getAboutURI(productId + "/changeRequests/" + changeRequest.getIdentifier()));
        	
        	final String bugzillaUri = BugzillaManager.getBugzillaUri().toString();
        	httpServletRequest.setAttribute("changeRequest", changeRequest);
        	httpServletRequest.setAttribute("bugzillaUri", bugzillaUri);
        	
        	RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_preview_small.jsp");
            rd.forward(httpServletRequest,httpServletResponse);
        	return;
        }
		
        throw new WebApplicationException(Status.NOT_FOUND);
			
	}
    */
	/**
	 * OSLC large preview for a single change request
	 * 
	 * Forwards to changerequest_preview_large.jsp to build the html
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */

	/* LAB 4 - Uncomment this method which provides an OSLC large preview of a BugzillaChangeRequest

	@GET
	@Path("{changeRequestId}/largePreview")
	@Produces({ MediaType.TEXT_HTML })
	public void getLargePreview(@PathParam("productId")       final String productId,
			                         @PathParam("changeRequestId") final String changeRequestId) throws ServletException, IOException, URISyntaxException
	{	
		final Bug bug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);

    if (bug != null) {
    	BugzillaChangeRequest changeRequest = null;

    	changeRequest = BugzillaChangeRequest.fromBug(bug);           
    	changeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId).getAbout());
    	changeRequest.setAbout(getAboutURI(productId + "/changeRequests/" + changeRequest.getIdentifier()));

    	final String bugzillaUri = BugzillaManager.getBugzillaUri().toString();
    	
    	httpServletRequest.setAttribute("changeRequest", changeRequest);
    	httpServletRequest.setAttribute("bugzillaUri", bugzillaUri);
    	httpServletRequest.setAttribute("bugUri", bugzillaUri + "/show_bug.cgi?id=" + Integer.toString(bug.getID()));
    	
    	RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_preview_large.jsp");
    	rd.forward(httpServletRequest,httpServletResponse);
    }
		
		throw new WebApplicationException(Status.NOT_FOUND);
		
	}
	
	*/
	
	
	/**
	 * Create a single BugzillaChangeRequest via RDF/XML, XML or JSON POST
	 * @param productId
	 * @param changeRequest
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */

    @OslcDialog
    (
         title = "Change Request Creation Dialog",
         label = "Change Request Creation Dialog",
         uri = "/{productId}/changeRequests/creator",
         hintWidth = "600px",
         hintHeight = "375px",
         resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcCreationFactory
    (
         title = "Change Request Creation Factory",
         label = "Change Request Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST},
         resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addChangeRequest(@PathParam("productId") final String productId,
                                                             final BugzillaChangeRequest changeRequest) throws IOException, ServletException

    {
    	
    	/* LAB 5 - Uncomment this code which creates a BugzillaChangeRequest from a POSTed RDF/XML, XML or JSON representation

    	//Create a new Bug from the incoming change request, retrieve the bug and then convert to a BugzillaChangeRequest
        final String newBugId = BugzillaManager.createBug(httpServletRequest, changeRequest, productId);     
        final Bug newBug = BugzillaManager.getBugById(httpServletRequest, newBugId);
        
        BugzillaChangeRequest newChangeRequest;
        
		try {
			newChangeRequest = BugzillaChangeRequest.fromBug(newBug);
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
        URI about = getAboutURI(productId + "/changeRequests/" + newChangeRequest.getIdentifier());
        newChangeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId).getAbout());
    	newChangeRequest.setAbout(about);
        setETagHeader(getETagFromChangeRequest(newChangeRequest), httpServletResponse);

        return Response.created(about).entity(changeRequest).build();
        
        */
    	return null; //LAB 5 - remove this line
    }

    /**
     * Updates a single change request via RDF/XML, XML or JSON PUT
     * 
     * Currently, update only supports adding OSLC CM link attributes to a
     * Bug in the Bug comments
     * 
     * @param eTagHeader
     * @param changeRequestId
     * @param changeRequest
     * @return
     * @throws IOException
     * @throws ServletException
     */
    
    /* LAB 5 - Uncomment this method which allows the update of a BugzillaChangeRequest with links
     
    @PUT
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Path("{changeRequestId}")
    public Response updateChangeRequest(@HeaderParam("If-Match")      final String              eTagHeader,
                                        @PathParam("changeRequestId") final String              changeRequestId,
    		                                                          final BugzillaChangeRequest       changeRequest) throws IOException, ServletException
    {
    	
    	//Only adding links is supported right now
    	
        final Bug originalBug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);
        
        if (originalBug != null)  {
        	try {
        		final BugzillaChangeRequest originalChangeRequest = BugzillaChangeRequest.fromBug(originalBug);
        		final String originalETag = getETagFromChangeRequest(originalChangeRequest);
        		
                if ((eTagHeader == null) || (originalETag.equals(eTagHeader))) {
                	
                	BugzillaManager.updateBug(httpServletRequest,changeRequest);
        	        final Bug updatedBug = BugzillaManager.getBugById(httpServletRequest, changeRequestId);
        	        final BugzillaChangeRequest updatedChangeRequest = BugzillaChangeRequest.fromBug(updatedBug);
        	        
        	        setETagHeader(getETagFromChangeRequest(updatedChangeRequest),httpServletResponse);
                } else {
                	throw new WebApplicationException(Status.PRECONDITION_FAILED);
                }
        		
        	} catch (Exception e) {
        		throw new WebApplicationException(e);
        	}
        } else {
        	throw new WebApplicationException(Status.NOT_FOUND);
        }

        return Response.ok().build();
    }
    */



    private static void setETagHeader(final String              eTagFromChangeRequest,
                                      final HttpServletResponse httpServletResponse)
    {
    	httpServletResponse.setHeader("ETag", eTagFromChangeRequest);
	}
    

    private static String getETagFromChangeRequest(final ChangeRequest changeRequest)
	{
    	Long eTag = null;
    	
    	if (changeRequest.getModified() != null) {
    		eTag = changeRequest.getModified().getTime();
    	} else if (changeRequest.getCreated() != null) {
    		eTag = changeRequest.getCreated().getTime();
    	} else {
    		eTag = new Long(0);
    	}
    	
		return eTag.toString();
	}
    
    
    /**
     * Convert a list of Bugzilla Bugs to a list of BugzillaChangeRequests
     * 
     * @param httpServletRequest
     * @param bugList
     * @param productId
     * @return
     */
    protected List<BugzillaChangeRequest> changeRequestsFromBugList(final HttpServletRequest httpServletRequest, final List<Bug> bugList, final String productId)
    {
    	List<BugzillaChangeRequest> results = new ArrayList<BugzillaChangeRequest>();
    	
        for (Bug bug : bugList) {
        	BugzillaChangeRequest changeRequest = null;
        	try {
        		changeRequest = BugzillaChangeRequest.fromBug(bug);
        	} catch (Exception e) {
        		throw new WebApplicationException(e);
        	}
        	
        	if (changeRequest != null) {
        		changeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productId).getAbout());  
        		changeRequest.setAbout(getAboutURI(productId + "/changeRequests/"+ changeRequest.getIdentifier()));
        		results.add(changeRequest);
        	}
        }
        return results;
    }
    
	
	
	protected URI getAboutURI(final String fragment)
	{
		URI about;
		try {
			about = new URI(BugzillaManager.getBugzServiceBase() + "/" + fragment);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
		return about;
	}
	
	/**
	 * Create and run a Bugzilla search and return the result.
	 * 
	 * Forwards to changerequest_filtered_json.jsp to create the JSON response
	 * 
	 * @param httpServletRequest
	 * @param productId
	 * @param terms
	 * @throws ServletException
	 * @throws IOException
	 */
	private void sendFilteredBugsReponse(final HttpServletRequest httpServletRequest, final String productId, final String terms ) 
			throws ServletException, IOException 
	{
		try {
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			
			BugSearch bugSearch = createBugSearch(terms);
			bc.executeMethod(bugSearch);
			List<Bug> bugList = bugSearch.getSearchResults();
			List<BugzillaChangeRequest> results = changeRequestsFromBugList(httpServletRequest, bugList, productId);
			httpServletRequest.setAttribute("results", results);

	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cm/changerequest_filtered_json.jsp"); 
	    	rd.forward(httpServletRequest, httpServletResponse);

		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	protected BugSearch createBugSearch(final String summary) 
	{
		BugSearch.SearchQuery summaryQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.SUMMARY, summary);
		BugSearch.SearchQuery limitQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.LIMIT, "50");
			
		BugSearch bugSearch = new BugSearch(summaryQuery, limitQuery);
			
		return bugSearch;
	}
	
	public static String getChangeRequestLinkLabel(int bugId, String summary) {
        return "Bug " + bugId + ": " + summary;
    }
	
	
}
