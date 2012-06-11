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
 *	   Dave Johnson	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J Workshop
 *******************************************************************************/
package org.eclipse.lyo.samples.ninacrm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Loads sample data for Nina CRM
 */
public class DataLoader implements Filter {
	
	private static final String HOSTNAME="oslc";
	
	ServletContext context = null;
		
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setAttribute("data", context.getAttribute("data"));
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		context = filterConfig.getServletContext();
		Map<URL, String> data = new HashMap<URL, String>();
		try {
			data.put(new URL("http://" + HOSTNAME + ":8080/OSLC4JBugzilla/services/1/changeRequests/1"), "Bug #1");
			data.put(new URL("http://" + HOSTNAME + ":8080/OSLC4JBugzilla/services/1/changeRequests/2"), "Bug #2");
			data.put(new URL("http://" + HOSTNAME + ":8080/OSLC4JBugzilla/services/1/changeRequests/8"), "Bug #8");
		} catch (MalformedURLException ignored) {
			ignored.printStackTrace();
		}
		context.setAttribute("data", data); 
	}
	
	public void destroy() {
	}
}
