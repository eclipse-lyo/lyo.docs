
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * No frills proxy.
 */
@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {
	
	public static final String REALM ="Bugzilla";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
	private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";
	private static final String BASIC_AUTHENTICATION_CHALLENGE = BASIC_AUTHORIZATION_PREFIX
			+ "realm=\"" + REALM + "\"";
	
    public ProxyServlet() {}

	/**
	 * Proxy every request including request and response headers.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		// get requested URI, make sure its an HTTP one
		String uri = request.getParameter("uri");
        URL url = new URL(uri);
		if (!(uri.startsWith("http:") || uri.startsWith("https://"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		

		// setup HTTP connection to requested URI, with same headers as original request
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod(request.getMethod());
        @SuppressWarnings("rawtypes")
		Enumeration en = request.getHeaderNames();
        while (en.hasMoreElements()) {
			String name = (String)en.nextElement();
			String value = request.getHeader(name);
			conn.setRequestProperty(name, value);
        }
        
        // copy response from requested URI back to response we are servicing	    
	    response.setContentType(conn.getContentType());

        int statusCode = conn.getResponseCode();
	    response.setStatus(statusCode);
	    if (statusCode == HttpServletResponse.SC_FORBIDDEN || statusCode == HttpServletResponse.SC_UNAUTHORIZED)
	    {
			response.addHeader(WWW_AUTHENTICATE_HEADER,
					BASIC_AUTHENTICATION_CHALLENGE);
	    }
	    else
	    {	    
	    	response.setContentLength(conn.getContentLength());
	    	response.setCharacterEncoding(conn.getContentEncoding());
	    	Map<String, List<String>> headers = conn.getHeaderFields();
	    	for (String key : headers.keySet()) {
	    		List<String> list = headers.get(key);
	    		if (key != null && list.get(0) != null) { 
	    			response.setHeader(key, list.get(0));
	    		}
	    	}
	    	copyInputToOutput(conn.getInputStream(), response.getOutputStream()); 
	    }
	    response.flushBuffer();
	}
	
    public static void copyInputToOutput(
            InputStream input,
            OutputStream output)
            throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);
        BufferedOutputStream out = new BufferedOutputStream(output);
        byte buffer[] = new byte[8192];
        for (int count = 0; count != -1;) {
            count = in.read(buffer, 0, 8192);
            if (count != -1) out.write(buffer, 0, count);
        }
        try {
            in.close();
            out.close();
        } catch (IOException ex) {
            throw new IOException("Closing file streams, " + ex.getMessage());
        }
    }
}
