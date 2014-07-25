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
* John Arwe - initial API and implementation
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
	
	public static final Resource[] validOccursValues = new Resource[] { 
		  ResourceFactory.createResource(CORE.NS+"Exactly-one")
		, ResourceFactory.createResource(CORE.NS+"Zero-or-one") 
		, ResourceFactory.createResource(CORE.NS+"Zero-or-many") 
		, ResourceFactory.createResource(CORE.NS+"One-or-many") 
		} ;
	public static final Set<Resource> validOccurs = new HashSet<Resource>( Arrays.asList(validOccursValues) ); 
	
	private static final Resource[] validRangeValues = new Resource [] {
		ResourceFactory.createResource(CORE.NS+"Any")
	};
	public static final Set<Resource> validRange = new HashSet<Resource>( Arrays.asList(validRangeValues) ); 
	public static final Resource defaultRange = validRangeValues[0]; 

	private static final Resource[] validRepresentationValues = new Resource[] { 
		  ResourceFactory.createResource(CORE.NS+"Reference")
		, ResourceFactory.createResource(CORE.NS+"Inline") 
		, ResourceFactory.createResource(CORE.NS+"Either") 
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
		  ResourceFactory.createResource(CORE.NS+"Resource") 
		, ResourceFactory.createResource(CORE.NS+"LocalResource") 
		, ResourceFactory.createResource(CORE.NS+"AnyResource") 
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
