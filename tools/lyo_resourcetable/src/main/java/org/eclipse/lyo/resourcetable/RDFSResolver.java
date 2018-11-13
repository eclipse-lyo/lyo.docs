/*******************************************************************************
* Copyright (c) 2014 IBM Corporation.
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
* Anamitra Bhattacharyya - initial API and implementation
*******************************************************************************/

package org.eclipse.lyo.resourcetable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFSResolver 
{
	private static Map<String,Model> rdfsRespository = new HashMap<String,Model>();
	
	private Model model = null;
	private String rdfsDocURI = null;
	private String requestResourceURI = null;
	public RDFSResolver(String uri)
	{
		requestResourceURI = uri;
		if(requestResourceURI.endsWith("/") || requestResourceURI.endsWith("#"))
		{
			rdfsDocURI = requestResourceURI;
		}
		else
		{
			rdfsDocURI = ResourceFactory.createProperty(requestResourceURI).getNameSpace();
		}
		resolveDoc();
	}
	
	private void resolveDoc()
	{
		model = rdfsRespository.get(rdfsDocURI);
		if(model == null)
		{
			try
			{
				URL u = new URL(rdfsDocURI);
				HttpURLConnection httpConn = (HttpURLConnection)u.openConnection();
				httpConn.setRequestMethod("GET");
				httpConn.setRequestProperty("accept","application/rdf+xml");
				//Model vocabulary = ReadFileIntoNewModel.read(infilename,inModelLanguage,fp.isFileNameURL());
				int resCode = httpConn.getResponseCode();
				String resLine = httpConn.getResponseMessage();
				InputStream inStream = null;
				if (resCode >= 400) 
				{
					System.out.println("error while opening http stream for uri "+rdfsDocURI);
					System.out.println("Failure Reason:: "+resLine);
				} 
				else
				{
					inStream = httpConn.getInputStream();
				}
				Model m = ModelFactory.createDefaultModel();
				model = m.read(inStream, null, "RDF/XML");
				rdfsRespository.put(rdfsDocURI, model);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getRDFSComment()
	{
		if(model==null) return null;
		Resource res = model.getResource(requestResourceURI);
		Statement stLabel = res.getProperty(com.hp.hpl.jena.vocabulary.RDFS.comment);
		if(stLabel != null)
		{
			String s = stLabel.getString();
			String[] strs = s.split("(\\r|\\n)");
			StringBuffer strb = new StringBuffer();
			for(String str : strs)
			{
				strb.append(str.trim()+" ");
			}
			return strb.toString();
		}
		return null;
	}
	
	public String getRDFSLabel()
	{
		if(model==null) return null;
		Resource res = model.getResource(requestResourceURI);
		Statement stLabel = res.getProperty(com.hp.hpl.jena.vocabulary.RDFS.label);
		if(stLabel != null)
		{
			String s = stLabel.getString();
			String[] strs = s.split("(\\r|\\n)");
			StringBuffer strb = new StringBuffer();
			for(String str : strs)
			{
				strb.append(str.trim()+" ");
			}
			return strb.toString();
		}
		return null;
	}

}
