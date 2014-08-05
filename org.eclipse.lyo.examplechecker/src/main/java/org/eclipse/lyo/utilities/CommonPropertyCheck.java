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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import org.eclipse.lyo.vocabularies.oslc.auto.AUTO;
import org.eclipse.lyo.vocabularies.oslc.core.CORE;
import org.eclipse.lyo.vocabularies.oslc.reconciliation.CRTV;


import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.rdf.model.*;


public class CommonPropertyCheck {

	private static HashMap<Object, String> commonPropObjMap;
	private static final Map<String, String> commonPropNameMap;

	static
	{
		//key is common property name, value is the suggested name
		commonPropNameMap = new HashMap<String, String>();
		commonPropNameMap.put("name","oslc:name");
		commonPropNameMap.put("title","dcterms:title; oslc:shortTitle(for shorter version)");
		commonPropNameMap.put("id", "dcterms:identifier; oslc:shortId");
		commonPropNameMap.put("dcterms:type", "rdf:type");
		commonPropNameMap.put("parent", "dcterms:source");
		commonPropNameMap.put("count", "oslc:totalCount");
		commonPropNameMap.put("number", "oslc:totalCount");

		commonPropObjMap = new HashMap<Object, String>();
		DCTerms dctermsObj = new DCTerms();
		RDF rdfObj = new RDF();
		OWL owlObj = new OWL();
		RDFS rdfsObj = new RDFS();
		CORE oslcObj = new CORE();
		CRTV crtvObj =	new CRTV();
		AUTO autoObj = new AUTO();
		commonPropObjMap.put(dctermsObj, DCTerms.getURI());
		commonPropObjMap.put(rdfObj, RDF.getURI());
		commonPropObjMap.put(owlObj, OWL.getURI());
		commonPropObjMap.put(rdfsObj, RDFS.getURI());
		commonPropObjMap.put(oslcObj, CORE.getURI());
		commonPropObjMap.put(crtvObj, CRTV.getURI());
		commonPropObjMap.put(autoObj, AUTO.NS);
	}
	private static final Set<String> propNameBlackList = new HashSet<String> (
				Arrays.asList( new String[]{"dcterms:type", "http://purl.org/dc/terms/type",
						"rdf:rest", "http://www.w3.org/1999/02/22-rdf-syntax-ns#rest",
						"rdf:Bag", "http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag",
						"rdf:List", "http://www.w3.org/1999/02/22-rdf-syntax-ns#List" }));
	final static Resource rdfPropertyR = RDF.Property;
	final static Resource rdfsClassR = RDFS.Class;

	// This method returns two types of error messages,
	// predicateCheck = false: errorMsg is a set of suggested property names
	// predicateCheck = true: errorMsg is a set with one element -
	//							either "\tWarning: unknown predicate," or
	//							"\tError: invalid predicate,"
	public static Set<String> checkCommonProperty(String nameSpace, String propName, Map<String, String> nspm, Resource resType, boolean predicateCheck) {
		String lowerPropName = propName.toLowerCase();
		String lowerNameSpace = nameSpace.toLowerCase();
		String commonResType = "";
		String nsPrefix = NameSpacePrefix.findNameSpacePrefix(nspm,nameSpace);
		Boolean exactMatch = false;
		Boolean inCommonNs = false;
		Boolean inBlackList = false;
		Integer predType = 0; // 0 - unknown, 1 - invalid, 2 - valid
		Set<String> errorMsgs = new HashSet<String>();

		if( propName.length() == 0 ) {
			return errorMsgs;
		}
		if (resType == rdfPropertyR) {
			commonResType = "Property";
		}
		else if (resType == rdfsClassR) {
			commonResType = "Resource";
		}

		//Check for invalid property/resource under common name spaces
		Collection<String> nameSpaceList = commonPropObjMap.values();
		for (String i : nameSpaceList) {
			if (i.toLowerCase().equals(lowerNameSpace)) {
				inCommonNs = true;
				break;
			}
		}

		for (Object obj : commonPropObjMap.keySet()) {
			HashMap<String, String> simpleFields = null;
			try {
				Method m = obj.getClass().getMethod("getSpecialDeclaredFields", null );
				simpleFields = (HashMap<String, String>)m.invoke(obj, null);
			}
			catch (NoSuchMethodException e) {
				//OSLCToolLogger.debug("No getSpecialDeclaredFields defined");
				Field[] fields = obj.getClass().getDeclaredFields();
				simpleFields = new HashMap<String, String>();
				for (Field i: fields) {
					simpleFields.put(i.getName(), i.getType().toString());
					//OSLCToolLogger.debug(i.getType().toString());
				}
			}
			catch (Exception e) {
				OSLCToolLogger.debug("Invocation failed" + e.getLocalizedMessage());
			}

			for (Iterator<Entry<String, String>> fieldi = simpleFields.entrySet().iterator();
					fieldi.hasNext();) {
				Entry<String, String> field = (Entry<String, String>) fieldi.next();
				String fieldName = field.getKey();
				if (!predicateCheck) {
					if (field.getValue().toString().endsWith("."+commonResType) &&
						fieldName.toLowerCase().equals(lowerPropName)) {
						if (!lowerNameSpace.equals(commonPropObjMap.get(obj).toLowerCase()) ||
								!fieldName.equals(propName)) {
							String prefix = NameSpacePrefix.findNameSpacePrefix(nspm, commonPropObjMap.get(obj));
							String suggestedName = prefix+ fieldName;
							if (!propNameBlackList.contains(suggestedName) && ! inCommonNs) {
								errorMsgs.add("\tSuggestion:\t" + suggestedName);
							}
						}
						else //exact match, no need to do any check against commonPropNameMap
						{
							exactMatch = true;
							if (propNameBlackList.contains(nsPrefix + lowerPropName)) {
								inBlackList = true;
							}
						}
					}
				}
				else {
					if (field.getValue().toString().endsWith("."+commonResType) &&
							fieldName.equals(propName)) {
						predType = 2;
					}
					else if (fieldName.equals(propName)) {
						predType = 1;
					}
				}
			}
		}
		if (!predicateCheck) {
			if (inCommonNs && !exactMatch ) {
				// Get the name of the corresponding Object in commonPropObjMap for a given name space
				Object apiObj = null;
				String apiClassName = "";
				for (Iterator<Entry<Object, String>> cpi = commonPropObjMap.entrySet().iterator();
						cpi.hasNext();) {
					Entry<Object, String> nsp = (Entry<Object, String>) cpi.next();
					String ns = nsp.getValue();
					if(ns.equals(nameSpace)) {
						apiObj = nsp.getKey();
						break;
					}
				}
				if (apiObj != null) {
					apiClassName = apiObj.getClass().getName();
				}
				errorMsgs.add( "\tError: the term \"" + nsPrefix + propName + "\" as a " + commonResType + " does not exist in the cached copy of the URI \"" + nameSpace
								+ "\". If it does exist in the URI, please notify us to update the API \"" + apiClassName + "\"");
			}
			if (exactMatch && !inBlackList) {
				return errorMsgs;
			}
			else {
				Iterator<Entry<String, String>> it = commonPropNameMap.entrySet().iterator();

				while (it.hasNext()) {
					Entry<String, String> propNameMap = (Entry<String, String>) it.next();
					if (resType == rdfPropertyR && (lowerPropName.endsWith(propNameMap.getKey())
							|| lowerPropName.startsWith(propNameMap.getKey())
							|| (nsPrefix + lowerPropName).equals(propNameMap.getKey()))) {
						errorMsgs.add( "\tSuggestion:\t" + propNameMap.getValue() );
					}
				}
				return errorMsgs;
			}
		}
		else {
			if (predType == 0) {
				errorMsgs.add("\tWarning: unknown predicate,");
			}
			if (predType == 1) {
				errorMsgs.add("\tError: invalid predicate,");
			}

			return errorMsgs;
		}
	}

	public static ArrayList<String> getPublicNameSpaces() {
		ArrayList<String> nsList = new ArrayList<String>();
		Iterator<Entry<Object, String>> it = commonPropObjMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Object, String> propNameMap = (Entry<Object, String>) it.next();
			nsList.add(propNameMap.getValue());
		}
		return nsList;
	}
}
