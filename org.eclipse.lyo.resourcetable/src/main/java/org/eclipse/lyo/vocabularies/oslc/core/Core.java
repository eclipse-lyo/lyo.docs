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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// Ala com.hp.hpl.jena.vocabulary.DC_11

public class Core {
	public static String NS = "http://open-services.net/ns/core#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final com.hp.hpl.jena.rdf.model.Property serviceProvider = ResourceFactory.createProperty(NS+"serviceProvider");
	public static final com.hp.hpl.jena.rdf.model.Property shortTitle = ResourceFactory.createProperty(NS+"shortTitle");
	public static final com.hp.hpl.jena.rdf.model.Property shortId = ResourceFactory.createProperty(NS+"shortId");
	public static final com.hp.hpl.jena.rdf.model.Property discussedBy = ResourceFactory.createProperty(NS+"discussedBy");
	public static final com.hp.hpl.jena.rdf.model.Property modifiedBy = ResourceFactory.createProperty(NS+"modifiedBy");
	public static final com.hp.hpl.jena.rdf.model.Property instanceShape = ResourceFactory.createProperty(NS+"instanceShape");
	public static final com.hp.hpl.jena.rdf.model.Property inReplyTo = ResourceFactory.createProperty(NS+"inReplyTo");
	public static final com.hp.hpl.jena.rdf.model.Property partOfDiscussion = ResourceFactory.createProperty(NS+"partOfDiscussion");
	public static final com.hp.hpl.jena.rdf.model.Property describes = ResourceFactory.createProperty(NS+"describes");

}
