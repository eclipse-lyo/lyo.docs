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
* Anamitra Bhattacharyya - initial API and implementation
*******************************************************************************/
package org.eclipse.lyo.vocabularies.oslc.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;

public class Property {
	public static final String NS = Core.NS;
	public static final Resource NAMESPACE = Core.NAMESPACE;
	public static final com.hp.hpl.jena.rdf.model.Property title = DCTerms.title;
	public static final com.hp.hpl.jena.rdf.model.Property description = DCTerms.description;
	public static final com.hp.hpl.jena.rdf.model.Property occurs = ResourceFactory.createProperty(NS+"occurs");
	public static final com.hp.hpl.jena.rdf.model.Property propertyDefinition = ResourceFactory.createProperty(NS+"propertyDefinition");
	public static final com.hp.hpl.jena.rdf.model.Property readOnly = ResourceFactory.createProperty(NS+"readOnly");
	public static final com.hp.hpl.jena.rdf.model.Property valueType = ResourceFactory.createProperty(NS+"valueType");
	public static final com.hp.hpl.jena.rdf.model.Property representation = ResourceFactory.createProperty(NS+"representation");
	public static final com.hp.hpl.jena.rdf.model.Property valueShape = ResourceFactory.createProperty(NS+"valueShape");
	public static final com.hp.hpl.jena.rdf.model.Property name = ResourceFactory.createProperty(NS+"name");
	public static final com.hp.hpl.jena.rdf.model.Property range = ResourceFactory.createProperty(NS+"range");

//	public static final com.hp.hpl.jena.rdf.model.Property oslcP = ResourceFactory.createProperty(NS+"");
	
	public static final Resource[] validOccursValues = new Resource[] { 
		  ResourceFactory.createResource(NS+"Exactly-one")
		, ResourceFactory.createResource(NS+"Zero-or-one") 
		, ResourceFactory.createResource(NS+"Zero-or-many") 
		, ResourceFactory.createResource(NS+"One-or-many") 
		} ;
	public static final Set<Resource> validOccurs = new HashSet<Resource>( Arrays.asList(validOccursValues) ); 
	
	private static final Resource[] validRangeValues = new Resource [] {
		ResourceFactory.createResource(NS+"Any")
	};
	public static final Set<Resource> validRange = new HashSet<Resource>( Arrays.asList(validRangeValues) ); 
	public static final Resource defaultRange = validRangeValues[0]; 

	private static final Resource[] validRepresentationValues = new Resource[] { 
		  ResourceFactory.createResource(NS+"Reference")
		, ResourceFactory.createResource(NS+"Inline") 
		, ResourceFactory.createResource(NS+"Either") 
		} ;
	public static final Set<Resource> validRepresentation = new HashSet<Resource>( Arrays.asList(validRepresentationValues) ); 
	
	public static final Resource[] validLiteralValueTypeValues = new Resource[] { 
		  XSD.xboolean
		, XSD.dateTime 
		, XSD.decimal
		, XSD.xdouble
		, XSD.xfloat
		, XSD.xint
		, XSD.xstring
		, ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral")
		} ;
	private static final Resource[] validResourceValueTypeValues = new Resource[] { 
		  ResourceFactory.createResource(NS+"Resource") 
		, ResourceFactory.createResource(NS+"LocalResource") 
		, ResourceFactory.createResource(NS+"AnyResource") 
		} ;
	public static final Set<Resource> validValueTypeLiteral = new HashSet<Resource>( Arrays.asList(validLiteralValueTypeValues) ); 
	public static final Set<Resource> validValueTypeResource = new HashSet<Resource>( Arrays.asList(validResourceValueTypeValues) ); 
	private static Set<Resource> iv () {
		final Set<Resource> v = new HashSet<Resource>();
		v.addAll(validValueTypeLiteral);
		v.addAll(validValueTypeResource);
		return v;
	}
	public static final Set<Resource> validValueType = iv();
	
}
