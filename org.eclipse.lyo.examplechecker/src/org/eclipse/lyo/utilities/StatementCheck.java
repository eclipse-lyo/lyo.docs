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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class StatementCheck {
	
	private final static Resource rdfPropertyR = RDF.Property;	
	private final static Resource rdfsClassR = RDFS.Class;
	
	
	public static ArrayList<ClassifiedErrorMessage> checkStatements(Model example, Map<String, String> nspm) {
		List<Statement> RSList = new ArrayList<Statement>();
		OSLCToolLogger.info("Checking predicates and objects in the statements...");
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		List<Statement> statements = example.listStatements().toList();
		Property tmpProp = null;
		Resource tmpResource = null;
		String nameSpace = null;
		String localName = null;
		String errorMsg = null;
		Set<String> errorList = new HashSet<String>();
		Map<String, Set<String>> errorMsgs = new HashMap<String, Set<String>>();
		Map<String, Set<String>> dataTypeErrorMsgs = new HashMap<String, Set<String>>();
		PredicateTypeCheck ref = new PredicateTypeCheck();
		findReifiedStatements(example, RSList);
		
		for( Statement s : statements ) {
			// Check property name of predicate
			tmpProp = s.getPredicate();
			nameSpace = tmpProp.getNameSpace();
			localName = tmpProp.getLocalName();
			errorList = CommonPropertyCheck.checkCommonProperty(nameSpace, localName, nspm, rdfPropertyR, false);
			if (!errorList.isEmpty()) {
				String nsPrefix = example.getNsURIPrefix(nameSpace);
				String key = (nsPrefix != null? nsPrefix + ":":nameSpace) + localName;
				if (errorMsgs.containsKey(key)) {
					if (!errorMsgs.get(key).equals(errorMsg)) {
						Set<String> tmp = errorMsgs.get(key);
						tmp.addAll(errorList);
						errorMsgs.put( key, tmp );
					}
				}
				else {
					errorMsgs.put( key, errorList );
				}
			}			
			// Check property name of object that is a URI resource, for example the <sco:IpGroup> 
			// in the fragment below. The triple is:
			// _:AX2dX371ce117X3aX1415fbe88b0X3aXX2dX7fec 
			// <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> 
			// <http://jazz.net/ns/ism/provisioning/sco#IpGroup>
			// <sco:ipGroup>
	        //  <sco:IpGroup>
	        //    <sco:name>RegionTwo_public_172.16.107.0/24</sco:name>
	        //    <sco:alias>RegionTwo_public_172.16.107.0/24</sco:alias>
	        //    <sco:ipPool rdf:resource="https://172.16.106.4/resources/ipGroups/2"/>
	        //  </sco:IpGroup>
	        //</sco:ipGroup>
			// Exempt the reification statements.
			// For example: for the object "executesAutomationPlan" in nodeID="A6" below, we don't return any error message
			// <rdf:Description rdf:nodeID="A4">
			// 		<oslc_auto:inputParameter rdf:nodeID="A3"/>
			// 		<oslc_auto:inputParameter rdf:nodeID="A2"/>
			// 		<oslc_auto:inputParameter rdf:nodeID="A1"/>
			// 		<oslc_auto:inputParameter rdf:nodeID="A0"/>
			// 		<oslc_auto:inputParameter rdf:nodeID="A5"/>
			// 		<oslc_auto:executesAutomationPlan rdf:resource="http://HOST:PORT/itmautomationprovider/services/plans/1"/>
			// 		<rdf:type rdf:resource="http://open-services.net/ns/auto#AutomationRequest"/>
			// </rdf:Description>
			// <rdf:Description rdf:nodeID="A6">
		    // 		<rdf:subject rdf:nodeID="A4"/>
		    // 		<rdf:predicate rdf:resource="http://open-services.net/ns/auto#executesAutomationPlan"/>
			// 		<rdf:object rdf:resource="http://HOST:PORT/itmautomationprovider/services/plans/1"/>
			// 		<rdf:type rdf:resource="http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement"/>
			// 		<dcterms:title>http://HOST:PORT/itmautomationprovider/services/plans/1</dcterms:title>
			// </rdf:Description>
			if (s.getObject().isURIResource()) {
				if (!RSList.contains(s)){	
					tmpResource = s.getObject().asResource();
					nameSpace = tmpResource.getNameSpace();
					localName = tmpResource.getLocalName();
					errorList = CommonPropertyCheck.checkCommonProperty(nameSpace, localName, nspm, rdfsClassR, false);
					if (!errorList.isEmpty()) {
						String nsPrefix = example.getNsURIPrefix(nameSpace);
						String key = (nsPrefix != null? nsPrefix + ":":nameSpace) + localName;
						OSLCToolLogger.addNewErrorMsg(errorMsgs, key, errorList);					
					}	
				}
			}			
			ref.checkDataType(s, example, dataTypeErrorMsgs);
		}
		OSLCToolLogger.mergeErrorMsgs(errorMsgs, dataTypeErrorMsgs);
		for (Iterator<Entry<String, Set<String>>> nspi = errorMsgs.entrySet().iterator();
				nspi.hasNext();) {
			Entry<String, Set<String>> nsp = (Entry<String, Set<String>>) nspi.next();
			List<String> tmplist = new ArrayList<String>(nsp.getValue());
		    Collections.sort(tmplist);
		    String msg = "";
		    for(String i: tmplist){
		    	msg += i + ";";
		    }
		    errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, 
		    		nsp.getKey(), msg );
		    errorMsgList.add(errMsg);
		    OSLCToolLogger.error(errMsg.toString());
		}		
		OSLCToolLogger.info("Checked " + statements.size() + " statements.");
		return errorMsgList;
	}
	
	// For the example below, we only exempt the following statements
	//      <rdf:subject rdf:nodeID="A4"/>
    // 		<rdf:predicate rdf:resource="http://open-services.net/ns/auto#executesAutomationPlan"/>
	// 		<rdf:object rdf:resource="http://HOST:PORT/itmautomationprovider/services/plans/1"/>
	// Example:
	// <rdf:Description rdf:nodeID="A4">
	// 		<oslc_auto:inputParameter rdf:nodeID="A3"/>
	// 		<oslc_auto:inputParameter rdf:nodeID="A2"/>
	// 		<oslc_auto:inputParameter rdf:nodeID="A1"/>
	// 		<oslc_auto:inputParameter rdf:nodeID="A0"/>
	// 		<oslc_auto:inputParameter rdf:nodeID="A5"/>
	// 		<oslc_auto:executesAutomationPlan rdf:resource="http://HOST:PORT/itmautomationprovider/services/plans/1"/>
	// 		<rdf:type rdf:resource="http://open-services.net/ns/auto#AutomationRequest"/>
	// </rdf:Description>
	// <rdf:Description rdf:nodeID="A6">
    // 		<rdf:subject rdf:nodeID="A4"/>
    // 		<rdf:predicate rdf:resource="http://open-services.net/ns/auto#executesAutomationPlan"/>
	// 		<rdf:object rdf:resource="http://HOST:PORT/itmautomationprovider/services/plans/1"/>
	// 		<rdf:type rdf:resource="http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement"/>
	// 		<dcterms:title>http://HOST:PORT/itmautomationprovider/services/plans/1</dcterms:title>
	// </rdf:Description>
	private static void findReifiedStatements(Model model, List<Statement> RSList){
		List<Statement> statements = model.listStatements().toList();
		ArrayList<String> exemptList = new ArrayList<String>(
	    		Arrays.asList( new String[]{RDF.subject.toString(),RDF.predicate.toString(),RDF.object.toString()}));

		for( Statement s : statements ) {
			RSIterator ri = s.listReifiedStatements();
			while (ri.hasNext()) {				
				ReifiedStatement rs = ri.next();
				StmtIterator smtI = rs.listProperties();
				while (smtI.hasNext()) {
					Statement stmt = smtI.next();		
					if (exemptList.contains(stmt.getPredicate().toString())) {
						RSList.add(stmt);
					}
					
				}
			}
		}		
	}
}
