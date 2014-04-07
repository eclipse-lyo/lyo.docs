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
* Anamitra Bhattacharyya - initial API and implementation
*******************************************************************************/
package org.eclipse.lyo.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class CheckForCommonMistakes {

	public static void check(Model m) {
		checkForUnterminatedNamespaceURIs(m);
		checkForNonHttpResources(m);
	}

	public static void checkForNonHttpResources(Model m) {
		System.out.println("Checking for non-HTTP/HTTPS resource URIs as objects of RDF triples ");
		int errors = 0;
		List<RDFNode> objs = m.listObjects().toList();
		for( RDFNode o : objs ) {
			if ( o.isURIResource()) {
				Resource r = o.asResource();
				String uri = r.getURI();
				String uriUp = uri.toUpperCase();
				if ( uriUp.startsWith("HTTP://")) {}
				else if ( uriUp.startsWith("HTTPS://")) {}
				else {
					if (errors == 0) {
						System.err.println("\nFound Non-HTTP/HTTPS URIs: Common Causes");
						System.err.println("\tRDF/XML files: qualified names in attribute values");
						System.err.println("\tTurtle  files: qualified names inside < > delimiters");
					}
					errors++;
					System.err.println("Non-HTTP/HTTPS URI: " + uri);
				}
			}
		}
		System.out.println("Checked " + objs.size() + " objects of RDF triples ");
	}

	public static void checkForUnterminatedNamespaceURIs(Model m) {
		System.out.println("Checking for unterminated and invalid namespace prefix URIs in prefix mappings");
		Map<String, String> nspm = m.getNsPrefixMap();
		NameSpaceWhiteList nsWhiteLister = new NameSpaceWhiteList();
		int nspCount = 0;
		for (Iterator<Entry<String, String>> nspi = nspm.entrySet().iterator(); nspi
				.hasNext();) {
			Entry<String, String> nsp = (Entry<String, String>) nspi.next();
			String nsuri = nsp.getValue();
			if (nsuri.endsWith("/")) {
			} else if (nsuri.endsWith("#")) {
			} else {
				System.err.println("Namespace URI: " + nsuri);
				System.err.println("\tends with an unusual character");
			}
			if (!nsWhiteLister.checkNameSpace(nsuri) ) {				
				System.err.println("Suspicious namespace URI: " + nsuri);
			}
			nspCount++;
		}
		System.out.println("Checked " + nspCount + " prefix mappings");
	}

}
