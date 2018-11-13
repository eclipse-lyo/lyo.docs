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
package org.eclipse.lyo.tools.common.shape.oslc.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;

public class CoreCommon {

	private static final HashMap<String, ArrayList<String>> coreRSFiles;

	static
	{
		coreRSFiles = new HashMap<String, ArrayList<String>>();
		ArrayList<String> core2fileList = new ArrayList<String> (
			Arrays.asList( new String[]{ "ActionsCoreShapeAction.ttl", "Comment.ttl", "Person.ttl"}));
		coreRSFiles.put("core2", core2fileList);
		// Add a new entry for core 3 resource shapes when they are available
	}

	private static final ArrayList<String> getResourceShapesFileList() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (Iterator<Entry<String, ArrayList<String>>> cfI = coreRSFiles.entrySet().iterator();
				cfI.hasNext();) {
			Entry<String, ArrayList<String>> corefile = (Entry<String, ArrayList<String>>) cfI.next();
			for (String i: corefile.getValue()) {
				returnList.add(corefile.getKey() + "/" + i );
			}
		}
		return returnList;
	}

	public static final Set<String> ResponseInfoPredicateWhiteList = new HashSet<String> (
			Arrays.asList( new String[]{ "http://purl.org/dc/terms/title",
					 "http://purl.org/dc/terms/description",
					 "http://open-services.net/ns/core#nextPage",
					 "http://open-services.net/ns/core#totalCount" }));

	public HashMap<String, String>	loadResourceShapes() {
		InputStream in = null;
		HashMap<String, String>	 resourceShapeSet = new HashMap<String, String>();
		for ( String i : getResourceShapesFileList()) {
			in = this.getClass().getResourceAsStream(i);
			loadOneResourceShape(in, resourceShapeSet);
		}
		return resourceShapeSet;
	}

	private void loadOneResourceShape(InputStream in, HashMap<String, String> resourceShapeSet) {
		Model reference = null;
		String coreNS = org.eclipse.lyo.tools.common.vocabulary.oslc.core.OSLC.NS;
		Model m = ModelFactory.createDefaultModel();
		reference = m.read(in,null, FileUtils.langTurtle);
		if ( reference == null ) System.exit(1);

		// Get the property name and their data type
		Resource subjectType = reference.getResource( coreNS + "Property");
		ResIterator resources = reference.listResourcesWithProperty( RDF.type, subjectType );
		while ( resources.hasNext() ) {
			Resource dataset = resources.next();
			StmtIterator stmts	= dataset.listProperties();
			String key = null;
			String value = null;
			while ( stmts.hasNext() ) {
				Statement tmpStat = stmts.next();

				if (tmpStat.getPredicate().toString().equals(coreNS+"propertyDefinition")) {
					key = tmpStat.getObject().toString();
				}
				if (tmpStat.getPredicate().toString().equals(coreNS+"valueType")) {
					value = tmpStat.getObject().toString();
				}
			}
			if( key != null && value != null ) {
				resourceShapeSet.put(key, value);
			}
		}

	}
}
