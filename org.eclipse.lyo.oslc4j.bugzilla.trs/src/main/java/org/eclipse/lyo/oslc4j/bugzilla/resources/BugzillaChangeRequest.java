/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
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
 *	   Sam Padgett	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J
 *     
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Constants;
import org.eclipse.lyo.oslc4j.bugzilla.jbugzx.rpc.GetAccessibleProducts;
import org.eclipse.lyo.oslc4j.bugzilla.trs.GetProduct;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;

//OSLC4J should give an rdf:type of oslc_cm:ChangeRequest
@OslcNamespace(Constants.CHANGE_MANAGEMENT_NAMESPACE)
@OslcName(Constants.CHANGE_REQUEST) 
@OslcResourceShape(title = "Change Request Resource Shape", describes = Constants.TYPE_CHANGE_REQUEST)
public final class BugzillaChangeRequest
       extends ChangeRequest
{
	private static boolean enable_url_as_cr = false;
    //Bugzilla adapter properties from bugz.properties 
    static {
        Properties props = new Properties();
        try {
            props.load(BugzillaManager.class.getResourceAsStream("/bugz.properties"));
            enable_url_as_cr = Boolean.parseBoolean(props.getProperty("enable_url_as_cr", "true"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public BugzillaChangeRequest() throws URISyntaxException {
		super();

	}
	public BugzillaChangeRequest(URI about) throws URISyntaxException {
		super(about);

	}

	//Bugzilla extended attributes beyond OSLC base ChangeRequest
	private String product = null;	
	private String component = null;
	private String version = null;
	private String priority = null;
	private String platform = null;
	private String operatingSystem = null;

	
	@OslcDescription("The Bugzilla product definition for this change request.")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "component")
    @OslcTitle("Component")
	public String getComponent() {
		return component;
	}
	
	public void setComponent(String component) {
		this.component = component;
	}
	
	@OslcDescription("The Bugzilla version for this change request.")
    @OslcOccurs(Occurs.ZeroOrOne)
	@OslcReadOnly
    @OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "version")
    @OslcTitle("Version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@OslcDescription("The Bugzilla priority for this change request.")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "priority")
    @OslcTitle("Priority")
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@OslcDescription("The Bugzilla platform for this change request.")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "platform")
    @OslcTitle("Platform")
	public String getPlatform() {
		return platform;
	}
	
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@OslcDescription("The Bugzilla operating system for this change request.")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "operatingSystem")
    @OslcTitle("Operating System")
	public String getOperatingSystem() {
		return operatingSystem;
	}
	
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
	
	/**
	 * Converts a {@link Bug} to an OSLC-CM BugzillaChangeRequest.
	 * 
	 * @param bug
	 *            the bug
	 * @return the ChangeRequest to be serialized
	 * @throws URISyntaxException
	 *             on errors setting the bug URI
	 * @throws ServletException 
	 * @throws IOException 
	 */
	public static BugzillaChangeRequest fromBug(Bug bug, HttpServletRequest httpServletRequest)
			throws URISyntaxException, IOException, ServletException {
		BugzillaChangeRequest changeRequest = new BugzillaChangeRequest();
		changeRequest.setIdentifier(bug.getID());
		changeRequest.setTitle(bug.getSummary());
		changeRequest.setStatus(bug.getStatus());

		//Map contributor to the person this bug is assigned to
		Object assignedTo = bug.getParameterMap().get("assigned_to");
		if (assignedTo != null) {
			String email = assignedTo.toString();
			Person contributor = new Person();
			contributor.setUri(new URI(BugzillaManager.getServletBase() + "/person?mbox=" + URLEncoder.encode(email, "UTF-8")));
			contributor.setMbox(email);
			contributor.setAbout(contributor.getUri());
			ArrayList<Person> contributors = new ArrayList<Person>();
			contributors.add(contributor);
			changeRequest.setContributors(contributors);
		}
		
		Date createdDate = (Date) bug.getParameterMap().get("creation_time");
		changeRequest.setCreated(createdDate);
		
		Date modifiedDate = (Date) bug.getParameterMap().get("last_change_time");
		changeRequest.setModified(modifiedDate);
		
		changeRequest.setProduct(bug.getProduct());
		changeRequest.setComponent(bug.getComponent());
		
		// Work around a bug in j2bugzilla. Bug.getVersion() results in a class cast exception.
		Object version = bug.getParameterMap().get("version");
		if (version != null) {
			changeRequest.setVersion(version.toString());
		}
		
		changeRequest.setPriority(bug.getPriority());
		
		Map<?, ?> internals = bug.getParameterMap();
		changeRequest.setPlatform((String) internals.get("platform"));
		changeRequest.setOperatingSystem((String) internals.get("op_sys"));
		
		/* "see also" support */
		Object[] seealsos = (Object[]) bug.getParameterMap().get("see_also"); //$NON-NLS-1$
		if ((seealsos != null) && (seealsos.length > 0)) {
			for(Object o : seealsos) {
				String uri = (String)o;
				String cgiuri = BugzillaManager.getBugzillaUri();
				if (cgiuri.endsWith("/")) {
					cgiuri += "show_bug.cgi?id=";
				} else {
					cgiuri += "/show_bug.cgi?id=";
				}
				if (uri.startsWith(cgiuri)) {
					// Bugid
					String bugId = uri.substring(cgiuri.length());
					Bug relatedBug = BugzillaManager.getBugById(httpServletRequest, bugId);
					String productName = relatedBug.getProduct();
					BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
					if (bc != null) {
						// get products
						GetAccessibleProducts getProductIds = new GetAccessibleProducts();
						try {
							bc.executeMethod(getProductIds);
						} catch (BugzillaException e) {
							e.printStackTrace();
							throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
						}
						Integer[] ids = getProductIds.getIds();
						GetProduct getProduct= new GetProduct(ids);
						try {
							bc.executeMethod(getProduct);
						} catch (BugzillaException e) {
							e.printStackTrace();
							throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
						}
						String productId = getProduct.getProductID(productName);
						URI oslcUri = new URI(BugzillaManager.getBugzServiceBase() + "/" + productId + "/changeRequests/" + bugId);
						changeRequest.addRelatedChangeRequest(new Link(oslcUri));
					}
				}
			}
		}
		// "URL" Experimental support
		if (enable_url_as_cr) {
			String url = (String) bug.getParameterMap().get("url"); //$NON-NLS-1$
			if (url!= null) {
				String trimmed_url = url.trim();
				if (trimmed_url.length() > 0) {
					changeRequest.addRelatedChangeRequest(new Link(new URI(trimmed_url)));
				}
			}
		}
		return changeRequest;
	}
	
	/**
	 * Creates a {@link Bug} from an OSLC-CM ChangeRequest.
	 * 
	 * @param bug the bug
	 * @return the ChangeRequest to be serialized
	 * @throws BugzillaException 
	 * @throws ConnectionException 
	 * @throws InvalidDescriptionException 
	 * @throws URISyntaxException on errors setting the bug URI
	 */
	public Bug toBug() throws ConnectionException, BugzillaException {
		BugFactory factory = new BugFactory().newBug();
		if (product != null) {
			factory.setProduct(product);
		}
		if (this.getTitle() != null) {
			factory.setSummary(this.getTitle());
		}
		if (this.getDescription() != null) {
			factory.setDescription(this.getDescription());
		}
		if (version != null) {
			factory.setVersion(version);
		}
		if (component != null) {
			factory.setComponent(component);
		}
		if (platform != null) {
			factory.setPlatform(platform);
		}
		if (operatingSystem != null) {
			factory.setOperatingSystem(operatingSystem);
		}
		
		return factory.createBug();
	}
	
	public void setIdentifier(int identifier) throws URISyntaxException {
		setIdentifier(Integer.toString(identifier));
	}
	
	public String getProduct() {
		return product;
	}
	
	public void setProduct(String product) {
		this.product = product;
	}
}
