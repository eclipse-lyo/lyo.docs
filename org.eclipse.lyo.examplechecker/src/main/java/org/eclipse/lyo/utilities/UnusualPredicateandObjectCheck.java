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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class UnusualPredicateandObjectCheck {

	private static Map<String, ArrayList<String>> invalidPredandObjs = new HashMap<String, ArrayList<String>>();

	static {
		ArrayList<String> rdftypeObjTypes = new ArrayList<String>(
			    Arrays.asList("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement", 
			    		"http://www.w3.org/2001/XMLSchema#string",
			    		"http://www.w3.org/2001/XMLSchema#:boolean",
			    		"http://www.w3.org/2001/XMLSchema#:decimal",
			    		"http://www.w3.org/2001/XMLSchema#:integer",
			    		"http://www.w3.org/2001/XMLSchema#:float",
			    		"http://www.w3.org/2001/XMLSchema#:time",
			    		"http://www.w3.org/2001/XMLSchema#:dateTime",
			    		"http://www.w3.org/2001/XMLSchema#:dateTimeStamp",
			    		"http://www.w3.org/2001/XMLSchema#:gMonth",
			    		"http://www.w3.org/2001/XMLSchema#:gDay",
			    		"http://www.w3.org/2001/XMLSchema#:gYearMonth",
			    		"http://www.w3.org/2001/XMLSchema#:gMonthDay",
			    		"http://www.w3.org/2001/XMLSchema#:duration",
			    		"http://www.w3.org/2001/XMLSchema#:yearMonthDuration",
			    		"http://www.w3.org/2001/XMLSchema#:dayTimeDuration",
			    		"http://www.w3.org/2001/XMLSchema#:short",
			    		"http://www.w3.org/2001/XMLSchema#:int",
			    		"http://www.w3.org/2001/XMLSchema#:long",
			    		"http://www.w3.org/2001/XMLSchema#:unsignedByte",
			    		"http://www.w3.org/2001/XMLSchema#:unsignedShort",
			    		"http://www.w3.org/2001/XMLSchema#:unsignedInt",
			    		"http://www.w3.org/2001/XMLSchema#:unsignedLong",
			    		"http://www.w3.org/2001/XMLSchema#:positiveInteger",
			    		"http://www.w3.org/2001/XMLSchema#:nonNegativeInteger",
			    		"http://www.w3.org/2001/XMLSchema#:negativeInteger",
			    		"http://www.w3.org/2001/XMLSchema#:nonPositiveInteger",
			    		"http://www.w3.org/2001/XMLSchema#:base64Binary",
			    		"http://www.w3.org/2001/XMLSchema#:language",
			    		"http://www.w3.org/2001/XMLSchema#:normalizedString",
			    		"http://www.w3.org/2001/XMLSchema#:token",
			    		"http://www.w3.org/2001/XMLSchema#:NMTOKEN",
			    		"http://www.w3.org/2001/XMLSchema#:Name",
			    		"http://www.w3.org/2001/XMLSchema#:NCName"));
		
		invalidPredandObjs.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
				rdftypeObjTypes );
		
	}
	
	public static ArrayList<ClassifiedErrorMessage> CheckUnusualPredicateandObject(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		String errorMsg = "";
		
		ResIterator subjects = example.listSubjects();		
		Property tmpProp = null;
		
		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
		   	StmtIterator tmpStmts = tmpRes.listProperties();
            while ( tmpStmts.hasNext()) {
            	Statement tmpStmt = tmpStmts.next();
            	RDFNode objectNode = tmpStmt.getObject();             	
            	tmpProp = tmpStmt.getPredicate();
            	if (invalidPredandObjs.containsKey(tmpProp.toString())){  
            		ArrayList<String> invalidtypeList = invalidPredandObjs.get(tmpProp.toString());
            		for (String i:invalidtypeList) {
	            		if (objectNode.toString().equals(i)) {
		            		String prefix = NameSpacePrefix.findNameSpacePrefix(nspm, tmpProp.getNameSpace());
		            		String propName = tmpProp.getLocalName();
		            		errorMsg = "Warning:\tSuspicious usage of: <" + prefix + propName + " rdf:resource=\"" + objectNode.toString() + "\"/>";
		            		errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_WARNING, "", errorMsg);
		            		OSLCToolLogger.error(errMsg.toString());
		            		errorMsgList.add(errMsg);
	            		}
	            	}
            	}
            }
		}
		return errorMsgList;
	}	
	
}
