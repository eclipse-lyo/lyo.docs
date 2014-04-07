/*******************************************************************************
* Copyright (c) 2012 IBM Corporation.
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

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;

//Ala com.hp.hpl.jena.vocabulary.DC_11

public class ResourceShape {
	public static final String NS = Core.NS;
	public static final Resource NAMESPACE = Core.NAMESPACE;
	public static final Resource resourceShape = ResourceFactory.createResource(Core.NS+"ResourceShape");
	public static final Property title = DCTerms.title;
	public static final Property describes = ResourceFactory.createProperty(NS+"describes");
	public static final Property property = ResourceFactory.createProperty(NS+"property");
}
