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
package org.eclipse.lyo.tools.common.vocabulary.oslc.changemgmt;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.lang.reflect.Field;
import java.util.HashMap;

public class CM {
	public static String NS = "http://open-services.net/ns/cm#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "cm";

	public static Resource ChangeRequest = ResourceFactory.createProperty( NS + "ChangeRequest");

	public static Property affectedByDefect = ResourceFactory.createProperty( NS + "affectedByDefect");
	public static Property affectsPlanItem = ResourceFactory.createProperty( NS + "affectsPlanItem");
	public static Property affectsRequirement = ResourceFactory.createProperty( NS + "affectsRequirement");
	public static Property affectsTestResult = ResourceFactory.createProperty( NS + "affectsTestResult");
	public static Property approved = ResourceFactory.createProperty( NS + "approved");
	public static Property blocksTestExecutionRecord = ResourceFactory.createProperty( NS + "blocksTestExecutionRecord");
	public static Property closeDate = ResourceFactory.createProperty( NS + "closeDate");
	public static Property closed = ResourceFactory.createProperty( NS + "closed");
	public static Property fixed = ResourceFactory.createProperty( NS + "fixed");
	public static Property implementsRequirement = ResourceFactory.createProperty( NS + "implementsRequirement");
	public static Property inprogress = ResourceFactory.createProperty( NS + "inprogress");
	public static Property relatedChangeRequest = ResourceFactory.createProperty( NS + "relatedChangeRequest");
	public static Property relatedTestCase = ResourceFactory.createProperty( NS + "relatedTestCase");
	public static Property relatedTestExecutionRecord = ResourceFactory.createProperty( NS + "relatedTestExecutionRecord");
	public static Property relatedTestPlan = ResourceFactory.createProperty( NS + "relatedTestPlan");
	public static Property relatedTestScript = ResourceFactory.createProperty( NS + "relatedTestScript");
	public static Property reviewed = ResourceFactory.createProperty( NS + "reviewed");
	public static Property status = ResourceFactory.createProperty( NS + "status");
	public static Property testedByTestCase = ResourceFactory.createProperty( NS + "testedByTestCase");
	public static Property tracksChangeSet = ResourceFactory.createProperty( NS + "tracksChangeSet");
	public static Property tracksRequirement = ResourceFactory.createProperty( NS + "tracksRequirement");
	public static Property verified = ResourceFactory.createProperty( NS + "verified");

	public static Resource defect = ResourceFactory.createProperty( NS + "defect");
	public static Resource planItem = ResourceFactory.createProperty( NS + "planItem");
	public static Resource requirementsChangeRequest = ResourceFactory.createProperty( NS + "requirementsChangeRequest");
	public static Resource task = ResourceFactory.createProperty( NS + "task");

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

