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

package org.eclipse.lyo.vocabularies.oslc.changemgmt;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class CM {
	public static String NS = "http://open-services.net/ns/cm#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "cm";

}
