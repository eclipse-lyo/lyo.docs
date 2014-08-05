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
* Zhenni Yan - initial API and implementation
*******************************************************************************/
package org.eclipse.lyo.utilities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.lyo.resourceshapes.oslc.core.CoreCommon;



import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

public class PredicateTypeCheck {

	private static Model reference = null;
	private static HashMap<String, String>	predicatetypeSet = new HashMap<String, String>();
	private static HashMap<String, String>	resourceShapeSet = null;
	private final static String coreNS = org.eclipse.lyo.vocabularies.oslc.core.CORE.NS;
	private final static String toolsNS = "http://open-services.net/ns/authoring/tools#";
	private final static Set<String> whiteList = new HashSet<String> (
			Arrays.asList( new String[]{XSD.xstring.toString()}));
	private final static ArrayList<String> resourceTypeList = new ArrayList<String> (
			Arrays.asList( new String[]{coreNS + "Resource"}));
	private final static Set<String> dateTimeTypeList = new HashSet<String> (
			Arrays.asList( new String[] { XSD.dateTime.toString(),
										  XSD.getURI()+"dateTimeStamp"} ));
	private final static String predicateTypeReference = "local_reference/PredicateType.ttl";

	public PredicateTypeCheck() {
		InputStream in = this.getClass().getResourceAsStream(predicateTypeReference);

		loadReference(in);

		OSLCToolLogger.info( "Loading resource shapes...");
		CoreCommon rsCore = new CoreCommon();
		resourceShapeSet = rsCore.loadResourceShapes();
	}


	private static void loadReference(InputStream  in) {
		OSLCToolLogger.info( "Loading predicate type reference...");
		Model m = ModelFactory.createDefaultModel();
		reference = m.read(in,null, FileUtils.langTurtle);
		if ( reference == null ) System.exit(1);

		Resource predicateType = reference.getResource( toolsNS + "TypeAndPredicate" );
		ResIterator predicatesets = reference.listResourcesWithProperty( RDF.type, predicateType );
		while ( predicatesets.hasNext() ) {
			Resource dataset = predicatesets.next();
			StmtIterator stmts	= dataset.listProperties();
			String key = null;
			String value = null;
			while ( stmts.hasNext() ) {
				Statement tmpStat = stmts.next();
				if( tmpStat.getPredicate().toString().equals(toolsNS + "predicate")) {
					key = tmpStat.getObject().toString();
				}
				if( tmpStat.getPredicate().toString().equals(toolsNS + "dataType")) {
					value = tmpStat.getObject().toString();
				}
				if( key != null && value != null ) {
					predicatetypeSet.put(key, value);
				}
			}
		}

		/*
		Iterator<Entry<String, String>> it = predicatetypeSet.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> propNameMap = (Entry<String, String>) it.next();
			OSLCToolLogger.debug(propNameMap.toString());
		}
		*/

	}

	public void checkDataType(Statement s, Model example, Map<String, Set<String>> localMsgs ) {
		Property pred = s.getPredicate();
		String predicateStr = pred.toString();
		RDFNode obj = s.getObject();
		String errorMsg = "";
		Set<String> errorList = new HashSet<String>();
		//Look for untyped literals

		if ( obj.isLiteral()) {
			Literal tmpLiteral = obj.asLiteral();
			// Flag string values that are HTTP/S URIs
			String objStr = tmpLiteral.getString().toLowerCase();
			if (objStr.startsWith("http://") || objStr.startsWith("https://") ) {
				errorList.add("\tWarning: string value should not contain URI: \"" + objStr + "\"");
			}
			else{
				// Check list of "must be reference" predicates
				if (resourceShapeSet.containsKey(predicateStr)){
					int found = resourceTypeList.indexOf(resourceShapeSet.get(predicateStr));
					if ( found >= 0 ) {
						errorList.add( "\tError: object should be a resource of type \"" + resourceTypeList.get(found) + "\", NOT a literal - \"" + objStr + "\"");
					}
				}
				else if (predicatetypeSet.containsKey(predicateStr) ){
					//System.out.println("====== containKey" + predicateStr );
					if( tmpLiteral.getDatatypeURI() == null ) {
						if (!whiteList.contains(predicatetypeSet.get(predicateStr))) {

							errorList.add("\tSuggestion:\tadd rdf:datatype=\"" + predicatetypeSet.get(predicateStr) + "\"");
						}
					}
					else{
						if ( !tmpLiteral.getDatatypeURI().equals(predicatetypeSet.get(predicateStr))) {
							errorList.add("\tSuggestion:\tchange the value of rdf:datatype to \"" + predicatetypeSet.get(predicateStr));
						}
						else {
							// Look for objects typed as dateTime (or dateTimeStamp) that lack time zone
							if (dateTimeTypeList.contains(tmpLiteral.getDatatypeURI())) {
								if (!validateTimeStamp(tmpLiteral.getString())) {
									errorList.add("\tError: Invalid date time string: " + tmpLiteral.getString());
								}
							}

						}

					}
				}
				else {
					if( tmpLiteral.getDatatypeURI() == null ) {
						errorList.add("\tWarning: untyped literal");

					}
				}
			}
		}
		if (!errorList.isEmpty()) {
			String key = example.getNsURIPrefix(pred.getNameSpace()) + ":" + pred.getLocalName();
			if (localMsgs.containsKey(key)) {
				Set<String> tmp = localMsgs.get(key);
				tmp.addAll(errorList);
				localMsgs.put(key, tmp);
			}
			else {
				localMsgs.put( key, errorList );
			}

		}
	}

	private boolean validateTimeStamp( String timestampStr ) {
		// Expected format of dateTime and dateTimeStamp: YYYY-MM-dd'T'HH:mm:ss.SSSZ. Example:
		// 2004-04-12T13:20:00Z
		// 2004-04-12T13:20:00-05:00
		// 2004-04-12T13:20:00+05:00
		// 2004-04-12T13:20:00.333-05:00
		String datePattern = "([0-9]{4})-([0-9]{2})-([0-9]{2})T[0-9]{2}:[0-9]{2}:[0-9]{2}(Z|(\\+|-)[0-9][0-9]:[0-9][0-9])";
		String datePattern2 = "([0-9]{4})-([0-9]{2})-([0-9]{2})T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{1,}(Z|(\\+|-)[0-9][0-9]:[0-9][0-9])";
		boolean valid = false;
		valid = timestampStr.matches(datePattern);
		if (!valid) {
			valid = timestampStr.matches(datePattern2);
		}
		return valid;
	}

}
