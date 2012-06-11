/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *	   Sam Padgett	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J
 *******************************************************************************/

package org.eclipse.lyo.oslc4j.bugzilla;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.bugzilla.resources.BugzillaChangeRequest;
import org.eclipse.lyo.oslc4j.bugzilla.servlet.CredentialsFilter;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.model.Link;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.BugSearch;
import com.j2bugzilla.rpc.CommentBug;
import com.j2bugzilla.rpc.GetBug;
import com.j2bugzilla.rpc.GetProduct;
import com.j2bugzilla.rpc.ReportBug;


public class BugzillaManager implements ServletContextListener  {

	public final static String REALM = "Bugzilla";
	
    private static String bugzillaUri = null;
	private static String admin = null;
	
	private static String servletBase = null;
	private static String bugzServiceBase = null;
	
	private static final String BUGZ_SERVICE_PATH = "/services";
	private static final String PROPERTY_SCHEME = BugzillaManager.class.getPackage().getName() + ".scheme";
    private static final String PROPERTY_PORT   = BugzillaManager.class.getPackage().getName() + ".port";
    private static final String SYSTEM_PROPERTY_NAME_REGISTRY_URI = ServiceProviderRegistryURIs.class.getPackage().getName() + ".registryuri";


    private static final String HOST = getHost();
	
    //Bugzilla adapter properties from bugz.properties 
    static {
        Properties props = new Properties();
        try {
            props.load(BugzillaManager.class.getResourceAsStream("/bugz.properties"));
            bugzillaUri = props.getProperty("bugzilla_uri");
            admin = props.getProperty("admin");
            System.out.println("bugzilla_uri: " + bugzillaUri);
            System.out.println("admin: " + admin);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) 
	{
		//No need to de-register - catalog will go away with the web app		
	}

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent)
    {
    	//Get the servlet base URI and set some paths to the REST services and the catalog
    	String basePath=generateBasePath(servletContextEvent);
    	servletBase = basePath;
    	bugzServiceBase = basePath + BUGZ_SERVICE_PATH;
    	System.setProperty(SYSTEM_PROPERTY_NAME_REGISTRY_URI, basePath + BUGZ_SERVICE_PATH + "/catalog/singleton");
    }

    /**
     * get BugzillaConnector from the HttpSession
     * 
     * The connector should be placed in the session by the CredentialsFilter servlet filter
     * 
     * @param request
     * @return connector 
     */
	public static BugzillaConnector getBugzillaConnector(HttpServletRequest request) 
	{	
		//connector should never be null if CredentialsFilter is doing its job
		BugzillaConnector connector = (BugzillaConnector) request.getSession().getAttribute(CredentialsFilter.CONNECTOR_ATTRIBUTE);	
		return connector;
	}
	
	
    private static String generateBasePath(final ServletContextEvent servletContextEvent)
    {
        final ServletContext servletContext = servletContextEvent.getServletContext();

        String scheme = System.getProperty(PROPERTY_SCHEME);
        if (scheme == null)
        {
            scheme = servletContext.getInitParameter(PROPERTY_SCHEME);
        }

        String port = System.getProperty(PROPERTY_PORT);
        if (port == null)
        {
            port = servletContext.getInitParameter(PROPERTY_PORT);
        }

        return scheme + "://" + HOST + ":" + port + servletContext.getContextPath();
    }

    private static String getHost()
    {
        try
        {
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (final UnknownHostException exception)
        {
            return "localhost";
        }
    }
	

    public static String getBugzillaUri() {
        return bugzillaUri;
    }

    public static void setBugzillaUri(String bugzillaUri) {
        BugzillaManager.bugzillaUri = bugzillaUri;
    }

	public static String getServletBase() {
		return servletBase;
	}

	public static String getBugzServiceBase() {
		return bugzServiceBase;
	}
	
	public static String getAdmin() {
		return admin;
	}
	
	//The following are static utility methods are useful for getting, creating and updating Bugzilla bugs.  Primarily used by BugzillaChangeRequestService
	
	/**
	 * Create a list of Bugs for a product ID using paging
	 * 
	 * @param httpServletRequest
	 * @param productIdString
	 * @param page
	 * @param limit
	 * @return The list of bugs, paged if necessary
	 * @throws IOException
	 * @throws ServletException
	 */
	public static List<Bug> getBugsByProduct(final HttpServletRequest httpServletRequest, final String productIdString, int page, int limit) throws IOException, ServletException 
    {
    	List<Bug> results=null;
		
		

		try {
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			final String pageString = httpServletRequest.getParameter("page");
			
			if (null != pageString) {
				page = Integer.parseInt(pageString);
			}
			int productId = Integer.parseInt(productIdString);
			
			final GetProduct getProducts = new GetProduct(productId); 
			bc.executeMethod(getProducts);
			final Product product = getProducts.getProduct();
		
			final BugSearch bugSearch = createBugSearch(page, limit, product);			
			bc.executeMethod(bugSearch);
			results = bugSearch.getSearchResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
    	
    	return results;
    }
	
	
	protected static BugSearch createBugSearch(int page, int limit, Product product) {
		BugSearch.SearchQuery productQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.PRODUCT, product.getName());
		BugSearch.SearchQuery limitQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.LIMIT, (limit + 1) + "");
		BugSearch.SearchQuery offsetQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.OFFSET, (page * limit) + "");
		
		return new BugSearch(productQuery, limitQuery, offsetQuery);
	}
	
	/**
	 * Get a Bugzilla Bug by id
	 * @param request
	 * @param bugIdString
	 * @return Bug
	 * @throws IOException
	 * @throws ServletException
	 */
	public static Bug getBugById(final HttpServletRequest request, final String bugIdString) throws IOException, ServletException
	{
		int bugId = -1;
		Bug bug = null;
		
		try {
			bugId = Integer.parseInt(bugIdString);
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(request);			
			final GetBug getBug = new GetBug(bugId);
			bc.executeMethod(getBug);
			bug = getBug.getBug();	
			if (bug == null) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}  
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
		
		return bug;
	}
	
	
	/**
	 * Create a new bug from a BugzillaChangeRequest
	 * 
	 * @param httpServletRequest
	 * @param changeRequest
	 * @param productIdString
	 * @return id of the new Bug
	 * @throws IOException
	 * @throws ServletException
	 */
	public static String createBug(HttpServletRequest httpServletRequest,
			                       final BugzillaChangeRequest changeRequest,
			                       final String productIdString) throws IOException, ServletException {
		String newBugId = null;

		try {
			/* LAB 3 - Uncomment to allow creation of Bugs from BugzillaChangeRequests

			final int productId = Integer.parseInt(productIdString);

			final BugzillaConnector bc = BugzillaManager
					.getBugzillaConnector(httpServletRequest);

			GetProduct getProducts = new GetProduct(productId);
			bc.executeMethod(getProducts);

			final Product product = getProducts.getProduct();

			String summary = changeRequest.getTitle();
			String component = changeRequest.getComponent();
			String version = changeRequest.getVersion();
			String operatingSystem = changeRequest.getOperatingSystem();
			String platform = changeRequest.getPlatform();
			String description = changeRequest.getDescription();

			BugFactory factory = new BugFactory().newBug().setProduct(product.getName());
			
			if (summary != null) {
				factory.setSummary(summary);
			}
			if (version != null) {
				factory.setVersion(version);
			}
			if (component != null) {
				factory.setComponent(component);
			}
			if (platform != null) {
				factory.setPlatform(platform);
			} else
				factory.setPlatform("Other");

			if (operatingSystem != null) {
				factory.setOperatingSystem(operatingSystem);
			} else
				factory.setOperatingSystem("Other");

			if (description != null) {
				factory.setDescription(description);
			}

			Bug bug = factory.createBug();
			ReportBug reportBug = new ReportBug(bug);
			bc.executeMethod(reportBug);
			newBugId = Integer.toString(reportBug.getID());
			*/

		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
		return newBugId;
	}

	

	public static Product getProduct(final HttpServletRequest httpServletRequest, final String productIdString)
	{
		int productId = Integer.parseInt(productIdString);
		Product product = null;
		
		try {
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);				
			GetProduct getProducts = new GetProduct(productId); 
			bc.executeMethod(getProducts);
			product = getProducts.getProduct();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
		return product;
	}
	
	/**
	 * Add OSLC links to the comment field of a Bug
	 * 
	 * @param request
	 * @param cr - BugzillaChangeRequest containing the links
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void updateBug(final HttpServletRequest request, final BugzillaChangeRequest cr) throws  ServletException, IOException
	{
		try
		{
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(request);
			
			// No built in field to hold external links. Just add the new link as a comment for now.
			String comment = getLinksComment(cr);
			if (comment.length() != 0) {
				CommentBug bugzillaMethod = new CommentBug(Integer.parseInt(cr.getIdentifier()), comment);
				bc.executeMethod(bugzillaMethod);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
	}
	
	protected static void addLinkComment(final StringBuffer buffer, final String linkType, final Link [] links) {
		if (links != null && (links.length !=0)) {
			buffer.append(linkType);
			buffer.append(":\n\n");
			for (Link link : links) {
				buffer.append(link.getValue().toString());
				buffer.append("\n");
			}
		}
	}

	protected static String getLinksComment(final BugzillaChangeRequest cr) {
		final StringBuffer b = new StringBuffer();
		
		addLinkComment(b, "Affected by Defect", cr.getAffectedByDefects());
		addLinkComment(b, "Affects Plan Item", cr.getAffectsPlanItems());
		addLinkComment(b, "Affects Requirement", cr.getAffectsRequirements());
		addLinkComment(b, "Affects Test Result", cr.getAffectsTestResults());
		addLinkComment(b, "Blocks Test Execution Record", cr.getBlocksTestExecutionRecords());
		addLinkComment(b, "Implements Requirement", cr.getImplementsRequirements());
		addLinkComment(b, "Related Change Request", cr.getRelatedChangeRequests());
		addLinkComment(b, "Related Test Execution Record", cr.getRelatedTestExecutionRecords());
		addLinkComment(b, "Related Test Plane", cr.getRelatedTestPlans());
		addLinkComment(b, "Related Test Script", cr.getRelatedTestScripts());
		addLinkComment(b, "Tested by Test Case", cr.getTestedByTestCases());
		addLinkComment(b, "Tracks Change Set", cr.getTracksChangeSets());
		addLinkComment(b, "Tracks Requirement", cr.getTracksRequirements());
		
		return b.toString();
	}
	
	

}
