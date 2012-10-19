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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.lyo.core.query.ComparisonTerm;
import org.eclipse.lyo.core.query.CompoundTerm;
import org.eclipse.lyo.core.query.InTerm;
import org.eclipse.lyo.core.query.OrderByClause;
import org.eclipse.lyo.core.query.PName;
import org.eclipse.lyo.core.query.ParseException;
import org.eclipse.lyo.core.query.QueryUtils;
import org.eclipse.lyo.core.query.ScopedSortTerm;
import org.eclipse.lyo.core.query.SimpleSortTerm;
import org.eclipse.lyo.core.query.SimpleTerm;
import org.eclipse.lyo.core.query.SortTerm;
import org.eclipse.lyo.core.query.SortTerms;
import org.eclipse.lyo.core.query.Value;
import org.eclipse.lyo.core.query.WhereClause;
import org.eclipse.lyo.oslc4j.bugzilla.resources.BugzillaChangeRequest;
import org.eclipse.lyo.oslc4j.bugzilla.resources.Person;
import org.eclipse.lyo.oslc4j.bugzilla.servlet.CredentialsFilter;
import org.eclipse.lyo.oslc4j.bugzilla.servlet.ServiceProviderCatalogSingleton;
import org.eclipse.lyo.oslc4j.bugzilla.utils.BugzillaHttpClient;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.SingletonWildcardProperties;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.Product;
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
    private static final String QUERY_PREFIX =
        "buglist.cgi?query_format=advanced&ctype=rdf&columnlist=" +
            "short_desc," +
            "bug_status," +
            "assigned_to," +
            "product," +
            "component," +
            "version," +
            "priority," +
            "rep_platform," +
            "op_sys";
    private static final String BUGZ_NS = "http://www.bugzilla.org/rdf#";
    private static final String BUGZ_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private static final String HOST = getHost();
    
    private static QName OSLC_SCORE = new QName(OslcConstants.OSLC_CORE_NAMESPACE, "score");
	
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
        	OSLC4JUtils.setPublicURI("http://mffiedler.raleigh.ibm.com:8080/OSLC4JBugzilla");
        	OSLC4JUtils.setHostResolutionDisabled(true);
        	return("mffiedler.raleigh.ibm.com");
            //return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (final Exception exception)
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
	 * @param oslcWhere
	 * @param prefixMap
	 * @param propMap
	 * @param orderBy
	 * @param searchTerms
	 * 
	 * @return The list of change requests, paged if necessary
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public static List<BugzillaChangeRequest>
	getBugsByProduct(final HttpServletRequest httpServletRequest,
	                 final String productIdString,
	                 int page,
	                 int limit,
	                 String oslcWhere,
	                 Map<String, String> prefixMap,
	                 Map<String, Object> propMap,
	                 String orderBy,
	                 String searchTerms) throws IOException, ServletException 
    {
    	List<BugzillaChangeRequest> results=new ArrayList<BugzillaChangeRequest>();		

		try {
		    
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			
			if (bc != null) {
				
	            final ServiceProvider serviceProvider =
	                ServiceProviderCatalogSingleton.getServiceProvider(
	                        httpServletRequest, productIdString);
	            StringBuffer buffer = new StringBuffer();
	            
                boolean fulltextSearch = searchTerms != null &&
                searchTerms.length() != 0;
            
                buffer.append(QUERY_PREFIX);
                
                if (fulltextSearch) {
                    
                    buffer.append(",relevance&content=");
                    
                    boolean first = true;
                    
                    for (String searchTerm : QueryUtils.parseSearchTerms(searchTerms)) {
                        
                        if (first) {
                            first = false;
                        } else {
                            buffer.append('+');
                        }
                        
                        buffer.append(URLEncoder.encode('"' + searchTerm + '"', "UTF-8"));
                    }
                }
                
				createBugSearch(page, limit, serviceProvider, oslcWhere, prefixMap, buffer);
				
				if (orderBy != null && orderBy.length() != 0) {
				    
				    OrderByClause orderByClause =
				        QueryUtils.parseOrderBy(orderBy, prefixMap);
				    
				    buffer.append("&order=");
				    
				    if (fulltextSearch) {
				        buffer.append("relevance+DESC");
				    }
				    
				    addSort(buffer, orderByClause, toplevelQueryProperties, ! fulltextSearch);
				    
				} else if (fulltextSearch) {
				    
				    buffer.append("&order=relevance+DESC");
				    
				} else {
				    
				    // Always order at least by bug id, so we get
				    // non-random returns across pages
				    buffer.append("&order=bug_id");
				}
				
				Credentials credentials = (Credentials)httpServletRequest.getSession().getAttribute(CredentialsFilter.CREDENTIALS_ATTRIBUTE);
				
				BugzillaHttpClient client = new BugzillaHttpClient(getBugzillaUri(), credentials);
				
				InputStream response = client.httpGet(buffer.toString());
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(response);		        
		        Element root = document.getDocumentElement();
		        NodeList list = root.getElementsByTagNameNS(BUGZ_NS, "bug");
		        
                factory.setNamespaceAware(false);
                
                builder = factory.newDocumentBuilder();
                
                int length = list.getLength();
                
                if (length > limit) {
                    httpServletRequest.setAttribute(Constants.NEXT_PAGE, page + 1);
                    length--;
                }
                
		        for (int idx = 0; idx < length; idx++) {
		            
                    Element p = (Element)list.item(idx);
                    
                    BugzillaChangeRequest changeRequest =
                        createChangeRequest(p, fulltextSearch);                   

                    if (propMap instanceof SingletonWildcardProperties ||
                        propMap.get(OslcConstants.DCTERMS_NAMESPACE + "created") != null ||
                        propMap.get(OslcConstants.DCTERMS_NAMESPACE + "modified") != null) {
                        
                        buffer = new StringBuffer();
                        
                        buffer.append("show_bug.cgi?id=");
                        buffer.append(changeRequest.getIdentifier());
                        buffer.append("&ctype=xml&field=creation_ts&field=delta_ts");
                        
                        response = client.httpGet(buffer.toString());
                        
                        document = builder.parse(response);
                        root = document.getDocumentElement();
                        
                        NodeList innerList = root.getElementsByTagName("creation_ts");
                        
                        if (innerList.getLength() == 1) {
                            changeRequest.setCreated(convertBugzillaDate(innerList.item(0).getTextContent()));
                        }
                        
                        innerList = root.getElementsByTagName("delta_ts");
                        
                        if (innerList.getLength() == 1) {
                            changeRequest.setModified(convertBugzillaDate(innerList.item(0).getTextContent()));
                        }
                    }
                    
                    changeRequest.setServiceProvider(ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, productIdString).getAbout());
                    
                    URI about;
                    
                    try {
                        about = new URI(getBugzServiceBase() + "/" + productIdString + "/changeRequests/"+ changeRequest.getIdentifier());
                    } catch (URISyntaxException e) {
                        throw new WebApplicationException(e);
                    }
                     
                    changeRequest.setAbout(about);
                    
                    results.add(changeRequest);
                }
		        
			} else {
				System.err.println("Bugzilla Connector not initialized - check bugz.properties");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ((e instanceof WebApplicationException) ?
			    (WebApplicationException)e :
			    new WebApplicationException(e));
		}
    	
    	return results;
    }
	
	private static Date convertBugzillaDate(String dateString) throws IOException
	{
	    if (dateString == null) {
	        return null;
	    }
	    
	    DateFormat format = new SimpleDateFormat(BUGZ_DATE_FORMAT);
	    
	    try {
            return format.parse(dateString);
        } catch (java.text.ParseException e) {
            throw new IOException(e);
        }
	}
	
	private static BugzillaChangeRequest createChangeRequest(Element bug, boolean fulltextSearch) throws IOException, URISyntaxException
	{
        BugzillaChangeRequest changeRequest = new BugzillaChangeRequest();
        
        changeRequest.setIdentifier(elementText(bug, "id"));
        changeRequest.setTitle(elementText(bug, "short_desc"));
        changeRequest.setStatus(elementText(bug, "bug_status"));

        //Map contributor to the person this bug is assigned to
        String email = elementText(bug, "assigned_to");
        if (email != null) {
            Person contributor = new Person();
            contributor.setUri(new URI(BugzillaManager.getServletBase() + "/person?mbox=" + URLEncoder.encode(email, "UTF-8")));
            contributor.setMbox(email);
            contributor.setAbout(contributor.getUri());
            ArrayList<Person> contributors = new ArrayList<Person>();
            contributors.add(contributor);
            changeRequest.setContributors(contributors);
        }
        
        changeRequest.setProduct(elementText(bug, "product"));
        changeRequest.setComponent(elementText(bug, "component"));        
        changeRequest.setVersion(elementText(bug, "version"));        
        changeRequest.setPriority(elementText(bug, "priority"));        
        changeRequest.setPlatform(elementText(bug, "rep_platform"));
        changeRequest.setOperatingSystem(elementText(bug, "op_sys"));
        
        if (fulltextSearch) {
            
            String relevance = elementText(bug, "relevance");
            
            if (relevance != null) {
                
                Map<QName, Object> extProps = new HashMap<QName, Object>(1);
                
                extProps.put(OSLC_SCORE, Float.valueOf(relevance));
                
                changeRequest.setExtendedProperties(extProps);
            }
        }
        
        return changeRequest;
	}
	
	private static String elementText(Element parent, String localName)
	{
	    NodeList list = parent.getElementsByTagNameNS(BUGZ_NS, localName);
	    
	    if (list.getLength() > 1) {
	        throw new IllegalStateException("More than one " + BUGZ_NS +
	                localName + " child");
	    }
	    
	    return list.getLength() == 0 ? null : list.item(0).getTextContent();
	}
	
	private static void createBugSearch(int page, int limit, final ServiceProvider serviceProvider , String oslcWhere,
	                                    Map<String, String> prefixMap, final StringBuffer buffer) throws ParseException
	{
	    buffer.append("&limit=");
	    buffer.append(limit + 1);
	    
	    buffer.append("&offset=");
	    buffer.append(page * limit);
	    
	    addSearchTerm(0, "product", "equals", serviceProvider.getTitle(), buffer);
	    
		if (oslcWhere != null) {
		    
    		WhereClause whereClause = QueryUtils.parseWhere(oslcWhere, prefixMap);
    		
    		createSearchQueries(buffer, 1, whereClause, toplevelQueryProperties);
		}
	}
	
	private static void
	addSearchTerm(int index, String field, String operator, String value,
	              final StringBuffer buffer)
	{
	    buffer.append("&field");
	    buffer.append(index);
	    buffer.append("-0-0=");
	    buffer.append(field);
	    
        buffer.append("&type");
        buffer.append(index);
        buffer.append("-0-0=");
        buffer.append(operator);
        
        buffer.append("&value");
        buffer.append(index);
        buffer.append("-0-0=");
        
        try {
            buffer.append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // XXX - can't happen
        }
	}	
	
	private static int createSearchQueries(final StringBuffer buffer, int index,
	                                       CompoundTerm compoundTerm,
	                                       Map<String, Object> queryProperties)
	{
	    for (SimpleTerm term : compoundTerm.children()) {
	        
	        switch (term.type()) {
	        case COMPARISON:
	            break;
	        case NESTED:
	            PName property = term.property();
	            Object field = queryProperties.get(property.namespace + property.local);
	            
	            if (field == null || field instanceof String) {
	                throw new WebApplicationException(
	                        new UnsupportedOperationException("Unsupported oslc.where nested term property term: " + term),
	                        Status.BAD_REQUEST);
	            }
	            
	            @SuppressWarnings("unchecked")
                Map<String, Object> nestedQueryProperties = (Map<String, Object>)field;
	            
                index = createSearchQueries(buffer, index, (CompoundTerm)term, nestedQueryProperties);
                
	            continue;
	            
	        default:
	            createInQuery(buffer, index++, (InTerm)term, queryProperties);
	            continue;
	        }
	        
	        ComparisonTerm comparison = (ComparisonTerm)term;
	        String operator;
	        
	        switch (comparison.operator()) {
	        case EQUALS:
	            operator = "equals";
	            break;
	        case NOT_EQUALS:
                operator = "notequals";
                break;
            case LESS_THAN:
                operator = "lessthan";
                break;
            case LESS_EQUALS:
                operator = "lessthaneq";
                break;
            case GREATER_THAN:
                operator = "greaterthan";
                break;
            default:
            case GREATER_EQUALS:
                operator = "greaterhaneq";
                break;
	        }
	        
	        PName property = comparison.property();	        
	        Object field =
	            queryProperties.get(property.namespace + property.local);
	        
	        if (field == null || ! (field instanceof String)) {
                throw new WebApplicationException(
                        new UnsupportedOperationException("Unsupported oslc.where comparison property: " + term),
                        Status.BAD_REQUEST);                
	        }
	        
	        Value operand = comparison.operand();
	        String value = operand.toString();
	        
	        switch (operand.type()) {
	        case STRING:
	        case URI_REF:
	            value = value.substring(1, value.length() - 1);
	            break;
	        case BOOLEAN:
	        case DECIMAL:
	            break;
	        default:
                throw new WebApplicationException(
                        new UnsupportedOperationException("Unsupported oslc.where comparison operand: " + value),
                        Status.BAD_REQUEST);                
	        }
	        
	        addSearchTerm(index++, (String)field, operator, value, buffer);
	    }
	    
	    return index;
	}
	
    private static void addSort(final StringBuffer buffer,
                                final SortTerms orderByClause,
                                final Map<String, Object> queryProperties,
                                boolean first)
    {
        for (SortTerm term : orderByClause.children()) {
            
            switch (term.type())
            {
            case SIMPLE:
                break;
            case SCOPED:
                PName property = term.identifier();
                Object field = queryProperties.get(property.namespace
                        + property.local);

                if (field == null || field instanceof String) {
                    throw new WebApplicationException(
                            new UnsupportedOperationException(
                                    "Unsupported oslc.orderBy scoped term sort term: "
                                            + term), Status.BAD_REQUEST);
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> nestedQueryProperties = (Map<String, Object>) field;

                addSort(buffer, ((ScopedSortTerm) term).sortTerms(),
                        nestedQueryProperties, first);
                
                first = false;

                continue;
            }

            PName property = term.identifier();
            Object field = queryProperties.get(property.namespace
                    + property.local);

            if (field == null || !(field instanceof String)) {
                throw new WebApplicationException(
                        new UnsupportedOperationException(
                                "Unsupported oslc.orderBy property: "
                                        + term), Status.BAD_REQUEST);
            }

            if (first) {
                first = false;
            } else {
                buffer.append(',');
            }

            buffer.append((String)field);
            
            if (! ((SimpleSortTerm)term).ascending()) {
                buffer.append("+DESC");
            }
        }
    }

	private static void createInQuery(final StringBuffer buffer, int index,
                                      InTerm inTerm,
                                      Map<String, Object> queryProperties)
	{
        PName property = inTerm.property();         
        Object field = queryProperties.get(property.namespace + property.local);
        
        if (field == null || ! (field instanceof String)) {
            throw new WebApplicationException(
                    new UnsupportedOperationException("Unsupported oslc.where comparison property: " + inTerm),
                    Status.BAD_REQUEST);                
        }
        
        int subIndex = 0;
        
        for (Value operand : inTerm.values()) {
            
            String value = operand.toString();
            
            switch (operand.type()) {
            case STRING:
            case URI_REF:
                value = value.substring(1, value.length() - 1);
                break;
            case BOOLEAN:
            case DECIMAL:
                break;
            default:
                throw new WebApplicationException(
                        new UnsupportedOperationException("Unsupported oslc.where comparison operand: " + value),
                        Status.BAD_REQUEST);                
            }
            
            
            buffer.append("&field");
            buffer.append(index);
            buffer.append("-0-");
            buffer.append(subIndex);
            buffer.append('=');
            buffer.append(field);
            
            buffer.append("&type");
            buffer.append(index);
            buffer.append("-0-");
            buffer.append(subIndex);
            buffer.append("=equals");
            
            buffer.append("&value");
            buffer.append(index);
            buffer.append("-0-");
            buffer.append(subIndex);
            buffer.append('=');
            
            try {
                buffer.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // XXX - can't happen
            }
            
            subIndex++;
        }
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

			BugFactory factory = new BugFactory().newBug().setProduct(
					product.getName());
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
	
	static final Map<String, Object> toplevelQueryProperties =
	    new HashMap<String, Object>();
	
	static {
	    
        toplevelQueryProperties.put(OslcConstants.DCTERMS_NAMESPACE + "identifier",
                                    "bug_id");
        toplevelQueryProperties.put(OslcConstants.DCTERMS_NAMESPACE + "title",
                                    "short_desc");
        toplevelQueryProperties.put(Constants.CHANGE_MANAGEMENT_NAMESPACE + "status",
                                    "bug_status");
        
	    Map<String, Object> nestedQueryProperties =
	        new HashMap<String, Object>(1);
	    
	    nestedQueryProperties.put(Constants.FOAF_NAMESPACE + "mbox",
	                              "assigned_to");
	    
        toplevelQueryProperties.put(OslcConstants.DCTERMS_NAMESPACE + "contributor",
                                    nestedQueryProperties);
	    
        toplevelQueryProperties.put(OslcConstants.DCTERMS_NAMESPACE + "created",
                                    "creation_ts");
        toplevelQueryProperties.put(OslcConstants.DCTERMS_NAMESPACE + "modified",
                                    "delta_ts");
	    toplevelQueryProperties.put(Constants.BUGZILLA_NAMESPACE + "component",
	                                "component");
        toplevelQueryProperties.put(Constants.BUGZILLA_NAMESPACE + "version",
                                    "version");
        toplevelQueryProperties.put(Constants.BUGZILLA_NAMESPACE + "priority",
                                    "priority");
        toplevelQueryProperties.put(Constants.BUGZILLA_NAMESPACE + "platform",
                                    "rep_platform");
        toplevelQueryProperties.put(Constants.BUGZILLA_NAMESPACE + "operatingSystem",
                                    "op_sys");
	}

}
