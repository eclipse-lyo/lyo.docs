/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2013. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/
package org.eclipse.lyo.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class CheckForCommonMistakes {

	public static ArrayList<ClassifiedErrorMessage> check(Model m) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = null;
		errorMsgList = checkForUnterminatedNamespaceURIs(m);
		errorMsgList.addAll(checkForNonHttpResources(m));
		return errorMsgList;
	}

	public static ArrayList<ClassifiedErrorMessage> checkForNonHttpResources(Model m) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		OSLCToolLogger.info("Checking for non-HTTP/HTTPS resource URIs as objects of RDF triples ");
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
						errMsg = new ClassifiedErrorMessage(
								ClassifiedErrorMessage.PRIORITY_ERROR, "",
								"\nFound Non-HTTP/HTTPS URIs: Common Causes\n\tRDF/XML files: qualified names in attribute values" +
								"\n\tTurtle	 files: qualified names inside < > delimiters");
						errorMsgList.add(errMsg);
					}
					errors++;
					errMsg = new ClassifiedErrorMessage(
							ClassifiedErrorMessage.PRIORITY_ERROR, "", "Non-HTTP/HTTPS URI: " + uri);
					errorMsgList.add(errMsg);
				}
			}
		}

		for (ClassifiedErrorMessage i: errorMsgList) {
			OSLCToolLogger.error(i.toString());
		}
		OSLCToolLogger.info("Checked " + objs.size() + " objects of RDF triples ");
		return errorMsgList;
	}

	public static ArrayList<ClassifiedErrorMessage>	 checkForUnterminatedNamespaceURIs(Model m) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		OSLCToolLogger.info("Checking for unterminated and invalid namespace prefix URIs in prefix mappings");
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
				errMsg = new ClassifiedErrorMessage(
						ClassifiedErrorMessage.PRIORITY_ERROR, "", "Namespace URI: " +
				nsuri + "\tends with an unusual character");
				errorMsgList.add(errMsg);
			}
			if (!nsWhiteLister.checkNameSpace(nsuri) ) {
				errMsg = new ClassifiedErrorMessage(
						ClassifiedErrorMessage.PRIORITY_ERROR, "", "Suspicious namespace URI: " + nsuri);
				errorMsgList.add(errMsg);

			}
			nspCount++;
		}
		for (ClassifiedErrorMessage i: errorMsgList) {
			OSLCToolLogger.error(i.toString());
		}
		OSLCToolLogger.info("Checked " + nspCount + " prefix mappings");
		return errorMsgList;

	}

}
