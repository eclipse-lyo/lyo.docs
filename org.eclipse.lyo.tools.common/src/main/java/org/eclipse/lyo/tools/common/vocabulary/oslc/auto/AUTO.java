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
package org.eclipse.lyo.tools.common.vocabulary.oslc.auto;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.lang.reflect.Field;
import java.util.HashMap;

public class AUTO {
	public static String NS = "http://open-services.net/ns/auto#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "auto";

	public static Resource AutomationPlan = ResourceFactory.createProperty( NS + "AutomationPlan");
	public static Resource AutomationRequest = ResourceFactory.createProperty( NS + "AutomationRequest");
	public static Resource AutomationResult = ResourceFactory.createProperty( NS + "AutomationResult");
	public static Resource ParameterInstance = ResourceFactory.createProperty( NS + "ParameterInstance");
	public static Resource TeardownAction = ResourceFactory.createProperty( NS + "TeardownAction");

	public static Property desiredState = ResourceFactory.createProperty( NS + "desiredState");
	public static Property executesAutomationPlan = ResourceFactory.createProperty( NS + "executesAutomationPlan");
	public static Property hasContribution = ResourceFactory.createProperty( NS + "hasContribution");
	public static Property inputParameter = ResourceFactory.createProperty( NS + "inputParmeter");
	public static Property outputParameter = ResourceFactory.createProperty( NS + "outputParameter");
	public static Property parameterDefinition = ResourceFactory.createProperty( NS + "parameterDefinition");
	public static Property producedByAutomationRequest = ResourceFactory.createProperty( NS + "producedByAutomationRequest");
	public static Property reportsOnAutomationPlan = ResourceFactory.createProperty( NS + "reportsOnAutomationPlan");
	public static Property state = ResourceFactory.createProperty( NS + "state");
	public static Property usesExecutionEnvironment = ResourceFactory.createProperty( NS + "usesExecutionEnvironment");
	public static Property verdict = ResourceFactory.createProperty( NS + "verdict");

	public static Resource DeferredExecution = ResourceFactory.createProperty( NS + "DeferredExecution");
	public static Resource ImmediateExecution = ResourceFactory.createProperty( NS + "ImmediateExecution");
	public static Resource canceled = ResourceFactory.createProperty( NS + "canceled");
	public static Resource canceling = ResourceFactory.createProperty( NS + "canceling");
	public static Resource complete = ResourceFactory.createProperty( NS + "complete");
	public static Resource error = ResourceFactory.createProperty( NS + "error");
	public static Resource fail = ResourceFactory.createProperty( NS + "fail");
	public static Resource inProgress = ResourceFactory.createProperty( NS + "inProgress");
	//public static Resource new = ResourceFactory.createProperty( NS + "new");
	public static Resource passed = ResourceFactory.createProperty( NS + "passed");
	public static Resource queued = ResourceFactory.createProperty( NS + "queued");
	public static Resource unavailable = ResourceFactory.createProperty( NS + "unavailable");
	public static Resource warning = ResourceFactory.createProperty( NS + "warning");

	public static String getURI() {
		return (NS);
	}
	public HashMap<String, String> getSpecialDeclaredFields() {
		Field[] fields = this.getClass().getFields();
		HashMap<String, String> simpleFields = new HashMap<String, String>();
		for (Field i: fields) {simpleFields.put(i.getName(), i.getType().toString());}
		// Add Property/Resource with some special characters in the name
		// For example, simpleFields.put("Exactly-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("new", "interface com.hp.hpl.jena.rdf.model.Resource");
		return simpleFields;
	}
}

