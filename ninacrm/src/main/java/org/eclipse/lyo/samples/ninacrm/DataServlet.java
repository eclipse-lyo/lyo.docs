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
 *******************************************************************************/
package org.eclipse.lyo.samples.ninacrm;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Accepts post of link data in URL encoded form
 */
@SuppressWarnings("serial")
public class DataServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		
		String linkurl = request.getParameter("linkurl");
		String linkname = request.getParameter("linkname");
        if (linkurl == null || linkname == null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        	return;
        }
        
		@SuppressWarnings("unchecked")
		Map<URL, String> data = (Map<URL, String>)request.getAttribute("data");
		data.put(new URL(linkurl), linkname); 
	}
}
