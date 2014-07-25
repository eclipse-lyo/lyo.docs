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
//This file is generated automatically, do not edit its content except:
//	- the package name below.
//	- any variable name that is not a valid Java identifier.
//		Please add such variable to the method getSpecialDeclaredFields() manually.
package org.eclipse.lyo.vocabularies.oslc.qualitymgmt;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.lang.reflect.Field;
import java.util.HashMap;

public class QM {
	public static String NS = "http://open-services.net/ns/qm#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "qm";

	public static Resource TestCase = ResourceFactory.createProperty( NS + "TestCase");
	public static Resource TestExecutionRecord = ResourceFactory.createProperty( NS + "TestExecutionRecord");
	public static Resource TestPlan = ResourceFactory.createProperty( NS + "TestPlan");
	public static Resource TestResult = ResourceFactory.createProperty( NS + "TestResult");
	public static Resource TestScript = ResourceFactory.createProperty( NS + "TestScript");

	public static Property affectedByChangeRequest = ResourceFactory.createProperty( NS + "affectedByChangeRequest");
	public static Property blockedByChangeRequest = ResourceFactory.createProperty( NS + "blockedByChangeRequest");
	public static Property executesTestScript = ResourceFactory.createProperty( NS + "executesTestScript");
	public static Property executionInstructions = ResourceFactory.createProperty( NS + "executionInstructions");
	public static Property producedByTestExecutionRecord = ResourceFactory.createProperty( NS + "producedByTestExecutionRecord");
	public static Property relatedChangeRequest = ResourceFactory.createProperty( NS + "relatedChangeRequest");
	public static Property reportsOnTestCase = ResourceFactory.createProperty( NS + "reportsOnTestCase");
	public static Property reportsOnTestPlan = ResourceFactory.createProperty( NS + "reportsOnTestPlan");
	public static Property runsOnTestEnvironment = ResourceFactory.createProperty( NS + "runsOnTestEnvironment");
	public static Property runsTestCase = ResourceFactory.createProperty( NS + "runsTestCase");
	public static Property testsChangeRequest = ResourceFactory.createProperty( NS + "testsChangeRequest");
	public static Property usesTestCase = ResourceFactory.createProperty( NS + "usesTestCase");
	public static Property usesTestScript = ResourceFactory.createProperty( NS + "usesTestScript");
	public static Property validatesRequirement = ResourceFactory.createProperty( NS + "validatesRequirement");
	public static Property validatesRequirementCollection = ResourceFactory.createProperty( NS + "validatesRequirementCollection");


	public static String getURI() {
		return (NS);
	}
	public HashMap<String, String> getSpecialDeclaredFields() {
		Field[] fields = this.getClass().getFields();
		HashMap<String, String> simpleFields = new HashMap<String, String>();
		for (Field i: fields) {simpleFields.put(i.getName(), i.getType().toString());}
		// Add Property/Resource with some special characters in the name
		// For example, simpleFields.put("Exactly-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		return simpleFields;
	}
}

