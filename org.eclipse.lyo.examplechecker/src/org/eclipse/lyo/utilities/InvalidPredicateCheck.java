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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.lyo.resourceshapes.oslc.core.CoreCommon;
import org.eclipse.lyo.vocabularies.oslc.auto.AUTO;
import org.eclipse.lyo.vocabularies.oslc.core.CORE;



import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class InvalidPredicateCheck {	
	private final static int CHECK_THRESHOLD = 1;	
	
	public static ArrayList<ClassifiedErrorMessage> CheckInvalidPredicate(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		String errorMsg = "";		
		errorMsg = check_OSLCResponseInfo(example, nspm);	
		if (errorMsg.length() > 0 ) {
			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "", errorMsg);
			errorMsgList.add(errMsg);
			OSLCToolLogger.error(errMsg.toString());
		}
		errorMsg = check_RDFSMember(example,nspm);
		if (errorMsg.length() > 0 ) {
			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "", errorMsg);
			errorMsgList.add(errMsg);
			OSLCToolLogger.error(errMsg.toString());
		}
		errorMsgList.addAll(check_InvalidPredicateName(example,nspm));
		
		errorMsgList.addAll(check_PredicatewithCommonSuffix(example, nspm));
		
		errorMsgList.addAll(check_OSLCauto_parameterDefinition(example, nspm));
		return errorMsgList;
	}
	
	// Look for mis-use of oslc:ResponseInfo
	private static String check_OSLCResponseInfo(Model example, Map<String, String> nspm) {
		ResIterator subjects = example.listSubjects();
		boolean found = false;
		Set<String> errorMsg =  new HashSet<String>();
		String responseInfoProp = CORE.ResponseInfo.toString();
		String nsPrefix = NameSpacePrefix.findNameSpacePrefix(nspm,CORE.ResponseInfo.getNameSpace());
		
		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
			found = false;
            StmtIterator tmpStmts = tmpRes.listProperties();
            while ( tmpStmts.hasNext()) {
            	Statement tmpStmt = tmpStmts.next();
            	if (tmpStmt.getPredicate().toString().equals(RDF.type.toString()) && 
            			tmpStmt.getObject().toString().equals(responseInfoProp)) {
        	    	found = true;
        	    	break;    
            	}
            }
            if (found) {
	            tmpStmts = tmpRes.listProperties();
	            while ( tmpStmts.hasNext()) {
	            	Statement tmpStmt = tmpStmts.next();
	            	Property pred = tmpStmt.getPredicate();
	            	if (!tmpStmt.getPredicate().toString().equals(RDF.type.toString()) && 
	            			!CoreCommon.ResponseInfoPredicateWhiteList.contains(pred.toString())) {
	            		errorMsg.add(pred.toString());
	            	}
	            }
            }
            
		}	
		if (!errorMsg.isEmpty()) {
			return nsPrefix + CORE.ResponseInfo.getLocalName() + "\tError: it's only used for OSLC paging, and ordinarily only has properties in the OSLC Core namespace, the following properties are not in OSLC Core name space: " + errorMsg.toString();
		}
		else {
			return "";
		}
		
	}
	
	// Look for mis-use of oslc:ResponseInfo
	private static String check_RDFSMember(Model example, Map<String, String> nspm) {
		ResIterator subjects = example.listSubjects();
		boolean found = false;
		String errorMsg = "";
		boolean underCorrectContainer = false;
		String rdfsContainerProp = RDFS.Container.toString();
		Resource Container = ResourceFactory.createProperty(  "http://www.w3.org/ns/ldp#" + "Container");
		String ldpContainerProp = Container.toString();
		String rdfsMemeberProp = RDFS.member.toString();
		String nsPrefix = NameSpacePrefix.findNameSpacePrefix(nspm,RDFS.member.getNameSpace());
		
		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
			found = false;
			underCorrectContainer = false;
            StmtIterator tmpStmts = tmpRes.listProperties();
            while ( tmpStmts.hasNext()) {
            	Statement tmpStmt = tmpStmts.next();
            	if (tmpStmt.getPredicate().toString().equals(rdfsMemeberProp)) {
             		found = true;
            		break;
            	}
            }
            if (found) {
            	tmpStmts = tmpRes.listProperties();
	            while ( tmpStmts.hasNext()) {
	            	Statement tmpStmt = tmpStmts.next();
	            	Property pred = tmpStmt.getPredicate();
	            	if (tmpStmt.getPredicate().toString().equals(RDF.type.toString()) 
	            			&& (tmpStmt.getObject().toString().equals(rdfsContainerProp) 
	            			|| tmpStmt.getObject().toString().equals(ldpContainerProp))) {
	            		underCorrectContainer = true;
	            	}
	            }
            }
            if ( found && !underCorrectContainer) {
    			errorMsg = "\tError: Subject's type should be one of the following: \"" + rdfsContainerProp + "\", \"" + ldpContainerProp + "\"";
    		}
		}
		if (!errorMsg.isEmpty()) {
			return nsPrefix + RDFS.member.getLocalName() + errorMsg;
		}
		else {
			return "";
		}
	}
	
	// Check for invalid predicates
	private static ArrayList<ClassifiedErrorMessage> check_InvalidPredicateName(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		ResIterator subjects = example.listSubjects();
		Map<String, Set<String>> errorMsgs = new HashMap<String, Set<String>>();
		Set<String> badURIs = new HashSet<String>();
		Set<String> goodURIs = new HashSet<String>();
				
		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
            StmtIterator tmpStmts = tmpRes.listProperties();
            
            while ( tmpStmts.hasNext()) {
            	boolean errorFound = false;
            	Statement tmpStmt = tmpStmts.next();
            	Property pred = tmpStmt.getPredicate();
            	String nsPrefix = NameSpacePrefix.findNameSpacePrefix(nspm, pred.getNameSpace());
            	String tmpMsg = "";
        		Set<String> tmpErrors = CommonPropertyCheck.checkCommonProperty(pred.getNameSpace(), 
        				pred.getLocalName(), nspm, RDF.Property, true);
        		if (!tmpErrors.isEmpty()) {
        			for (String s : tmpErrors) {
        				tmpMsg = s;
        			}
        			if (tmpMsg.equals("\tWarning: unknown predicate,")) {
        				// If the name space is not accessible, we assume it's a new instance document
    					// without any vocabulary defined. For this case, we don't raise any 
    					// "unknown predicate" error.
        				String nameSpace = pred.getNameSpace();
        				if (goodURIs.contains(nameSpace)) {
        					errorFound = true;
        					Set<String> tmp = new HashSet<String>();
        					tmp.add("\tWarning: " + pred.getLocalName() + " is used as a predicate, but does not exist in the cached copy of any vocabulary.");
        					OSLCToolLogger.addNewErrorMsg(errorMsgs, nsPrefix + pred.getLocalName(), tmp);
        				}
        				else if (!badURIs.contains(nameSpace)){
							int ret = InvalidURILinkCheck.checkURIexists(nameSpace, badURIs);
							if (ret != 0 ) {
								badURIs.add(nameSpace);
							}
							else {
								goodURIs.add(nameSpace);
								errorFound = true;
								Set<String> tmp = new HashSet<String>();
	        					tmp.add("\tWarning: " + pred.getLocalName() + " is used as a predicate, but does not exist in the cached copy of any vocabulary.");
								OSLCToolLogger.addNewErrorMsg(errorMsgs, nsPrefix + pred.getLocalName(), tmp);
							}
        				}
        			} 
        			else {
        				errorFound = true;
        				Set<String> tmp = new HashSet<String>();
    					tmp.add("\tError: " + pred.getLocalName() + " is used as a predicate, but it identifies a Resource.");
        				OSLCToolLogger.addNewErrorMsg(errorMsgs, nsPrefix + pred.getLocalName(), tmp);
        			}
        		
        		}   
        		// any predicate name that starts with an Upper case local name is 
        		// *probably invalid* (either it's invalid, or it's breaking 
        		// Linked Data naming conventions)
        		if (!errorFound) {
        			if (Character.isUpperCase(pred.getLocalName().charAt(0))) {       
        				Set<String> tmp = new HashSet<String>();
    					tmp.add("\tWarning: " + pred.getLocalName() + " is used as a predicate, but \"" + pred.getLocalName() + "\" begins with an uppercase letter which is contrary to Linked Data best practices.");  
    					OSLCToolLogger.addNewErrorMsg(errorMsgs, nsPrefix + pred.getLocalName(), tmp);
            				          		           		
        			}
        		}
	        }
	    }
		for (Iterator<Entry<String, Set<String>>> nspi = errorMsgs.entrySet().iterator();
				nspi.hasNext();) {
			Entry<String, Set<String>> nsp = (Entry<String, Set<String>>) nspi.next();
			List<String> tmplist = new ArrayList<String>(nsp.getValue());
		    Collections.sort(tmplist);
		    String msg = "";
		    for(String i: tmplist){
		    	msg += i;
		    }
		    errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, 
		    		nsp.getKey(), msg );
		    errorMsgList.add(errMsg);
		    OSLCToolLogger.error(errMsg.toString());
		}	
		return errorMsgList;
	}  
	
	// Check sets of predicates with common suffixes
	private static ArrayList<ClassifiedErrorMessage>  check_PredicatewithCommonSuffix(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		ResIterator subjects = example.listSubjects();
		ArrayList<String> nsList = CommonPropertyCheck.getPublicNameSpaces();
		HashMap<String, Set<String>> commonSuffixes = new HashMap<String, Set<String>>();
				
		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
            StmtIterator tmpStmts = tmpRes.listProperties();
            
            while ( tmpStmts.hasNext()) {
            	Statement tmpStmt = tmpStmts.next();
            	Property pred = tmpStmt.getPredicate();
            	String predName = pred.getLocalName();
            	String prefix = NameSpacePrefix.findNameSpacePrefix(nspm, pred.getNameSpace());
            	if (!nsList.contains(pred.getNameSpace())) {
            		boolean upperFound = false;
            		int index = 0;
            		int len = predName.length();
            		String reversePredName = new StringBuffer(predName).reverse().toString();
            		for (char c : reversePredName.toCharArray()) {
            		    if (Character.isUpperCase(c)) {
            		        upperFound = true;
            		        break;
            		    }
            		    index++;
            		}
            		if (upperFound) {
            			String commonPart = predName.substring(0, len - index - 1);
            			if (commonSuffixes.get(commonPart) != null ) {
            				Set<String> predList = commonSuffixes.get(commonPart);
            				predList.add(prefix + predName);
            				commonSuffixes.put(commonPart, predList);
            			}
            			else {
            				Set<String> predList = new HashSet<String>();
            				predList.add(prefix + predName);
            				commonSuffixes.put(commonPart, predList);
            			}
            		}            		
            	}
            	        		
	        }
	    }
		for (Iterator<Entry<String,Set<String>>> predsp = commonSuffixes.entrySet().iterator();
				predsp.hasNext();) {
			Entry<String, Set<String>> pred = (Entry<String, Set<String>>) predsp.next();
			// If we have > CHECK_THRESHOLD number of instances of common suffix, we raise a suggestion. 
			Set<String> predList = pred.getValue();
			String commonString = pred.getKey();
			if (predList.size() > CHECK_THRESHOLD) {
				String firstElem = new ArrayList<String>(predList).get(0);
				String nameSpace = firstElem.substring(0, firstElem.indexOf(":")+1);
				String subject = "";
				String suggestion = "";
				List<String> tmpList = new ArrayList<String>(predList);
				Collections.sort(tmpList);
				for (String i: tmpList) {
					if (subject.length() == 0) {
						subject += i ;
					}
					else {
						subject += ", " + i;
					}
					if (suggestion.length() == 0) {
						suggestion += i.replace(commonString, "").replace(nameSpace, "");
					}
					else {
						suggestion += "; " + i.replace(commonString, "").replace(nameSpace, "");
					}
				}
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, 
						subject, ":\tSuggestion:\tthe common entity \"" + commonString + "\" should be " +
								"defined as a resource with the following properties: " + suggestion + "." );
			    errorMsgList.add(errMsg);
			    OSLCToolLogger.error(errMsg.toString());
			}
			
		}	
		return errorMsgList;
	}  
		
	// In the context of an auto:parameterDefinition object, that object resource should
	// 1: have type (rdf:type) of oslc:Property
	// 2: ordinarily should have zero propertyDefinition predicates (if it has one, call that "unusual" rather than a warning)
	private static ArrayList<ClassifiedErrorMessage> check_OSLCauto_parameterDefinition(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		ResIterator subjects = example.listSubjects();
		String errorMsg = "";
		String autoPrefix = NameSpacePrefix.findNameSpacePrefix(nspm, AUTO.NS);
		String oslcPrefix = NameSpacePrefix.findNameSpacePrefix(nspm, CORE.NS);
		Property paramDef = AUTO.parameterDefinition;		
		Resource OSLCProperty = CORE.Property;
		Property OSLCpropertyDefinition = CORE.propertyDefinition;
		Property OSLCname = CORE.name;
		Property RDFtype = RDF.type;
		boolean foundOSLCProperty = false;
		Integer numOfOSLCpropertyDefinition = 0;		
		
		while (subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
            StmtIterator tmpStmts = tmpRes.listProperties();
            Property pred = null;
            while (tmpStmts.hasNext()) {
            	Statement tmpStmt = tmpStmts.next();
            	if (tmpStmt.getPredicate().toString().equals(paramDef.toString())) {
            		pred = tmpStmt.getPredicate();
            		RDFNode tmp = tmpStmt.getObject(); 
            		if (tmp.isResource() ){
	            		Resource objRes = tmp.asResource();
	            		StmtIterator objStmts = objRes.listProperties();
	            		foundOSLCProperty = false;
	            		numOfOSLCpropertyDefinition = 0;
	            		String paramName = "";
	            		while (objStmts.hasNext()){	            			
	            			Statement objStmt = objStmts.next();
	            			if (objStmt.getPredicate().toString().equals(RDFtype.toString())
	            					&& objStmt.getObject().toString().equals(OSLCProperty.toString())) {
	            				foundOSLCProperty = true;
	            			}
	            			if (objStmt.getPredicate().toString().equals(OSLCpropertyDefinition.toString())) {
	            				numOfOSLCpropertyDefinition++;
	            			}
	            			if (objStmt.getPredicate().toString().equals(OSLCname.toString())) {
	            				paramName = objStmt.getObject().toString();
	            			}
	            		}
	            		if (!foundOSLCProperty ) {	            			
	            			errorMsg = "\tError: doesn't have a type of " + oslcPrefix + OSLCProperty.getLocalName();
	            			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, 
	            			autoPrefix + paramDef.getLocalName() + " for \"" + paramName + "\"" , errorMsg);
	            			errorMsgList.add(errMsg);
	            			OSLCToolLogger.error(errMsg.toString());
		            	}
	            		if (numOfOSLCpropertyDefinition > 0) {
	            			errorMsg = "\tWarning: resource ordinarily should have zero " + oslcPrefix + OSLCpropertyDefinition.getLocalName() + " predicates.";
	            			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_WARNING, 
	            					autoPrefix + paramDef.getLocalName() + " for \"" + paramName + "\"" ,errorMsg);
	            			errorMsgList.add(errMsg);
	            			OSLCToolLogger.error(errMsg.toString());
	            		}
            		}            		
              	}            	
            } 
		}
		return errorMsgList;
	}
}
