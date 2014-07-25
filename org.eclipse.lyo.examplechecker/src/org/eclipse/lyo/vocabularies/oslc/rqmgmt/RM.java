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
package org.eclipse.lyo.vocabularies.oslc.rqmgmt;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.lang.reflect.Field;
import java.util.HashMap;

public class RM {
	public static String NS = "";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "rm";

	public static Resource Requirement = ResourceFactory.createProperty( NS + "Requirement");
	public static Resource RequirementCollection = ResourceFactory.createProperty( NS + "RequirementCollection");

	public static Property affectedBy = ResourceFactory.createProperty( NS + "affectedBy");
	public static Property elaboratedBy = ResourceFactory.createProperty( NS + "elaboratedBy");
	public static Property implementedBy = ResourceFactory.createProperty( NS + "implementedBy");
	public static Property specifiedBy = ResourceFactory.createProperty( NS + "specifiedBy");
	public static Property trackedBy = ResourceFactory.createProperty( NS + "trackedBy");
	public static Property uses = ResourceFactory.createProperty( NS + "uses");
	public static Property validatedBy = ResourceFactory.createProperty( NS + "validatedBy");


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