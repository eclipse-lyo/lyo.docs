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
//	- the package name below.
//	- any variable name that is not a valid Java identifier.
//		Please add such variable to the method getSpecialDeclaredFields() manually.
package org.eclipse.lyo.tools.common.vocabulary.oslc.core;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OSLC {
	public static String NS = "http://open-services.net/ns/core#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "core";

	public static Resource AllowedValues = ResourceFactory.createProperty( NS + "AllowedValues");
	public static Resource Comment = ResourceFactory.createProperty( NS + "Comment");
	public static Resource Compact = ResourceFactory.createProperty( NS + "Compact");
	public static Resource CreationFactory = ResourceFactory.createProperty( NS + "CreationFactory");
	public static Resource Dialog = ResourceFactory.createProperty( NS + "Dialog");
	public static Resource Discussion = ResourceFactory.createProperty( NS + "Discussion");
	public static Resource Error = ResourceFactory.createProperty( NS + "Error");
	public static Resource ExendedError = ResourceFactory.createProperty( NS + "ExendedError");
	public static Resource OAuthConfiguration = ResourceFactory.createProperty( NS + "OAuthConfiguration");
	public static Resource PrefixDefinition = ResourceFactory.createProperty( NS + "PrefixDefinition");
	public static Resource Preview = ResourceFactory.createProperty( NS + "Preview");
	public static Resource Property = ResourceFactory.createProperty( NS + "Property");
	public static Resource Publisher = ResourceFactory.createProperty( NS + "Publisher");
	public static Resource QueryCapability = ResourceFactory.createProperty( NS + "QueryCapability");
	public static Resource ResourceShape = ResourceFactory.createProperty( NS + "ResourceShape");
	public static Resource ResponseInfo = ResourceFactory.createProperty( NS + "ResponseInfo");
	public static Resource Service = ResourceFactory.createProperty( NS + "Service");
	public static Resource ServiceProvider = ResourceFactory.createProperty( NS + "ServiceProvider");
	public static Resource ServiceProviderCatalog = ResourceFactory.createProperty( NS + "ServiceProviderCatalog");

	public static com.hp.hpl.jena.rdf.model.Property allowedValue = ResourceFactory.createProperty( NS + "allowedValue");
	public static com.hp.hpl.jena.rdf.model.Property allowedValues = ResourceFactory.createProperty( NS + "allowedValues");
	public static com.hp.hpl.jena.rdf.model.Property authorizationURI = ResourceFactory.createProperty( NS + "authorizationURI");
	public static com.hp.hpl.jena.rdf.model.Property comment = ResourceFactory.createProperty( NS + "comment");
	public static com.hp.hpl.jena.rdf.model.Property creation = ResourceFactory.createProperty( NS + "creation");
	public static com.hp.hpl.jena.rdf.model.Property creationDialog = ResourceFactory.createProperty( NS + "creationDialog");
	public static com.hp.hpl.jena.rdf.model.Property creationFactory = ResourceFactory.createProperty( NS + "creationFactory");
	public static com.hp.hpl.jena.rdf.model.Property defaultValue = ResourceFactory.createProperty( NS + "defaultValue");
	public static com.hp.hpl.jena.rdf.model.Property describes = ResourceFactory.createProperty( NS + "describes");
	public static com.hp.hpl.jena.rdf.model.Property details = ResourceFactory.createProperty( NS + "details");
	public static com.hp.hpl.jena.rdf.model.Property dialog = ResourceFactory.createProperty( NS + "dialog");
	public static com.hp.hpl.jena.rdf.model.Property discussedBy = ResourceFactory.createProperty( NS + "discussedBy");
	public static com.hp.hpl.jena.rdf.model.Property discussionAbout = ResourceFactory.createProperty( NS + "discussionAbout");
	public static com.hp.hpl.jena.rdf.model.Property domain = ResourceFactory.createProperty( NS + "domain");
	public static com.hp.hpl.jena.rdf.model.Property extendedError = ResourceFactory.createProperty( NS + "extendedError");
	public static com.hp.hpl.jena.rdf.model.Property hidden = ResourceFactory.createProperty( NS + "hidden");
	public static com.hp.hpl.jena.rdf.model.Property hintHeight = ResourceFactory.createProperty( NS + "hintHeight");
	public static com.hp.hpl.jena.rdf.model.Property hintWidth = ResourceFactory.createProperty( NS + "hintWidth");
	public static com.hp.hpl.jena.rdf.model.Property icon = ResourceFactory.createProperty( NS + "icon");
	public static com.hp.hpl.jena.rdf.model.Property inReplyTo = ResourceFactory.createProperty( NS + "inReplyTo");
	public static com.hp.hpl.jena.rdf.model.Property instanceShape = ResourceFactory.createProperty( NS + "instanceShape");
	public static com.hp.hpl.jena.rdf.model.Property isMemberProperty = ResourceFactory.createProperty( NS + "isMemberProperty");
	public static com.hp.hpl.jena.rdf.model.Property label = ResourceFactory.createProperty( NS + "label");
	public static com.hp.hpl.jena.rdf.model.Property maxSize = ResourceFactory.createProperty( NS + "maxSize");
	public static com.hp.hpl.jena.rdf.model.Property message = ResourceFactory.createProperty( NS + "message");
	public static com.hp.hpl.jena.rdf.model.Property modifiedBy = ResourceFactory.createProperty( NS + "modifiedBy");
	public static com.hp.hpl.jena.rdf.model.Property moreInfo = ResourceFactory.createProperty( NS + "moreInfo");
	public static com.hp.hpl.jena.rdf.model.Property name = ResourceFactory.createProperty( NS + "name");
	public static com.hp.hpl.jena.rdf.model.Property nextPage = ResourceFactory.createProperty( NS + "nextPage");
	public static com.hp.hpl.jena.rdf.model.Property oauthAccessTokenURI = ResourceFactory.createProperty( NS + "oauthAccessTokenURI");
	public static com.hp.hpl.jena.rdf.model.Property oauthConfiguration = ResourceFactory.createProperty( NS + "oauthConfiguration");
	public static com.hp.hpl.jena.rdf.model.Property oauthRequestTokenURI = ResourceFactory.createProperty( NS + "oauthRequestTokenURI");
	public static com.hp.hpl.jena.rdf.model.Property occurs = ResourceFactory.createProperty( NS + "occurs");
	public static com.hp.hpl.jena.rdf.model.Property partOfDiscussion = ResourceFactory.createProperty( NS + "partOfDiscussion");
	public static com.hp.hpl.jena.rdf.model.Property prefix = ResourceFactory.createProperty( NS + "prefix");
	public static com.hp.hpl.jena.rdf.model.Property prefixBase = ResourceFactory.createProperty( NS + "prefixBase");
	public static com.hp.hpl.jena.rdf.model.Property prefixDefinition = ResourceFactory.createProperty( NS + "prefixDefinition");
	public static com.hp.hpl.jena.rdf.model.Property property = ResourceFactory.createProperty( NS + "property");
	public static com.hp.hpl.jena.rdf.model.Property propertyDefinition = ResourceFactory.createProperty( NS + "propertyDefinition");
	public static com.hp.hpl.jena.rdf.model.Property queryBase = ResourceFactory.createProperty( NS + "queryBase");
	public static com.hp.hpl.jena.rdf.model.Property queryCapability = ResourceFactory.createProperty( NS + "queryCapability");
	public static com.hp.hpl.jena.rdf.model.Property range = ResourceFactory.createProperty( NS + "range");
	public static com.hp.hpl.jena.rdf.model.Property readOnly = ResourceFactory.createProperty( NS + "readOnly");
	public static com.hp.hpl.jena.rdf.model.Property rel = ResourceFactory.createProperty( NS + "rel");
	public static com.hp.hpl.jena.rdf.model.Property representation = ResourceFactory.createProperty( NS + "representation");
	public static com.hp.hpl.jena.rdf.model.Property resourceShape = ResourceFactory.createProperty( NS + "resourceShape");
	public static com.hp.hpl.jena.rdf.model.Property resourceType = ResourceFactory.createProperty( NS + "resourceType");
	public static com.hp.hpl.jena.rdf.model.Property results = ResourceFactory.createProperty( NS + "results");
	public static com.hp.hpl.jena.rdf.model.Property selectionDialog = ResourceFactory.createProperty( NS + "selectionDialog");
	public static com.hp.hpl.jena.rdf.model.Property service = ResourceFactory.createProperty( NS + "service");
	public static com.hp.hpl.jena.rdf.model.Property serviceProvider = ResourceFactory.createProperty( NS + "serviceProvider");
	public static com.hp.hpl.jena.rdf.model.Property serviceProviderCatalog = ResourceFactory.createProperty( NS + "serviceProviderCatalog");
	public static com.hp.hpl.jena.rdf.model.Property shortId = ResourceFactory.createProperty( NS + "shortId");
	public static com.hp.hpl.jena.rdf.model.Property shortTitle = ResourceFactory.createProperty( NS + "shortTitle");
	public static com.hp.hpl.jena.rdf.model.Property statusCode = ResourceFactory.createProperty( NS + "statusCode");
	public static com.hp.hpl.jena.rdf.model.Property totalCount = ResourceFactory.createProperty( NS + "totalCount");
	public static com.hp.hpl.jena.rdf.model.Property usage = ResourceFactory.createProperty( NS + "usage");
	public static com.hp.hpl.jena.rdf.model.Property valueShape = ResourceFactory.createProperty( NS + "valueShape");
	public static com.hp.hpl.jena.rdf.model.Property valueType = ResourceFactory.createProperty( NS + "valueType");

	public static Resource Any = ResourceFactory.createProperty( NS + "Any");
	public static Resource AnyResource = ResourceFactory.createProperty( NS + "AnyResource");
	public static Resource Either = ResourceFactory.createProperty( NS + "Either");
	//public static Resource Exactly-one = ResourceFactory.createProperty( NS + "Exactly-one");
	public static Resource Inline = ResourceFactory.createProperty( NS + "Inline");
	public static Resource LocalResource = ResourceFactory.createProperty( NS + "LocalResource");
	//public static Resource One-or-many = ResourceFactory.createProperty( NS + "One-or-many");
	public static Resource Reference = ResourceFactory.createProperty( NS + "Reference");
	public static Resource Resource = ResourceFactory.createProperty( NS + "Resource");
	//public static Resource Zero-or-many = ResourceFactory.createProperty( NS + "Zero-or-many");
	//public static Resource Zero-or-one = ResourceFactory.createProperty( NS + "Zero-or-one");
	//public static Resource default = ResourceFactory.createProperty( NS + "default");
	public static Resource document = ResourceFactory.createProperty( NS + "document");
	public static Resource initialHeight = ResourceFactory.createProperty( NS + "initialHeight");
	public static Resource largePreview = ResourceFactory.createProperty( NS + "largePreview");
	public static Resource smallPreview = ResourceFactory.createProperty( NS + "smallPreview");

	public static String getURI() {
		return (NS);
	}
	public HashMap<String, String> getSpecialDeclaredFields() {
		Field[] fields = this.getClass().getFields();
		HashMap<String, String> simpleFields = new HashMap<String, String>();
		for (Field i: fields) {simpleFields.put(i.getName(), i.getType().toString());}
		// Add Property/Resource with some special characters in the name
		// For example, simpleFields.put("Exactly-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("Exactly-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("One-or-many", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("Zero-or-many", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("Zero-or-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		simpleFields.put("default", "interface com.hp.hpl.jena.rdf.model.Resource");
		return simpleFields;
	}
}

