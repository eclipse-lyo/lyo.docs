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
package org.eclipse.lyo.samples.ninacrm.examples;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.RDF;

public class NewDefect {
	private static final String HOSTNAME="oslc";
	
	public static void main(String[] args) {
	    String bug = formNewBug( // (1)
	       "test Java post 1 2 3 4 5 6", "1.0",  "Datastore", "PC", "Windows");    
	    try {
	       URL createURL = new URL( // (2)
	          "http://" + HOSTNAME + ":8080/OSLC4JBugzilla/services/1/changeRequests");         
	       HttpURLConnection conn = (HttpURLConnection)createURL.openConnection();
	       conn.setRequestMethod("POST"); // (3) 
	       conn.setDoOutput(true);
	       conn.setRequestProperty("Content-Type", "application/rdf+xml");  // (4)

	       BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
	       out.write(bug.getBytes("UTF-8")); // (5)
	       out.close();
	       BufferedReader in = new BufferedReader( // (6)
	          new InputStreamReader(conn.getInputStream()));                   
	       String s;
	       while ((s = in.readLine()) != null) {
	           System.out.println(s);
	       }
	       in.close();

	       int rc = conn.getResponseCode(); // (7)
	       System.out.println("Return status: " + rc);
	       System.out.println("Location: " + conn.getHeaderField("Location")); // (8)
	   }
	   catch (IOException e) {
	       e.printStackTrace();
	   }
	}


    // create new RDF model for bug with specified title with URI '/'
    public static String formNewBug(
    	String title, String version, String component, String platform, String opsys) {

    	// create properties to be used
    	Property bugType =
            new PropertyImpl("http://open-services.net/ns/cm#ChangeRequest");

    	Property titleProp =
            new PropertyImpl("http://purl.org/dc/terms/title");

    	Property versionProp = 
    		new PropertyImpl("http://www.bugzilla.org/rdf#version");

    	Property componentProp = 
    		new PropertyImpl("http://www.bugzilla.org/rdf#component");

    	Property platformProp = 
    		new PropertyImpl("http://www.bugzilla.org/rdf#platform");

    	Property opsysProp = 
    		new PropertyImpl("http://www.bugzilla.org/rdf#opsys");

    	// create Jena model with necessary namespace prefixes
    	Model model = ModelFactory.createDefaultModel();
    	model.setNsPrefix("bugz",    "http://www.bugzilla.org/rdf#");
    	model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
    	model.setNsPrefix("oslc_cm", "http://open-services.net/ns/cm#");
    	
    	// create resource, add property values
    	com.hp.hpl.jena.rdf.model.Resource resource = model.createResource("");
    	resource.addProperty(RDF.type,     bugType);
    	resource.addLiteral(titleProp,     title);
    	resource.addLiteral(versionProp,   version);
    	resource.addLiteral(componentProp, component);
    	resource.addLiteral(platformProp,  platform);
    	resource.addLiteral(opsysProp,     opsys);

        StringWriter sw = new StringWriter();
        RDFWriter writer = model.getWriter();
        writer.write(model, sw, "/");
        sw.flush();
        return sw.toString();
    }
}
