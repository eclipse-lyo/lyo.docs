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
package org.eclipse.lyo.tools.common.vocabulary.classgenerator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ClassFactory {
	private String path = "YOUR PACKAGE NAME";
	private Model vocabModel = null;
	private String className = "";
	private String nameSpace = "";

	public ClassFactory(String cn, Model m) {
		vocabModel = m;
		className = cn;

		ResIterator ri = m.listResourcesWithProperty(RDF.type, OWL.Ontology);
		List<Resource> owlOntologies = ri.toList();
		// owlOntologies.size should be 1
		for (Resource c : owlOntologies){
			nameSpace = c.toString();
		}

	}
	public void dynamicClassCreation() {
		StringBuilder source = new StringBuilder();
		List<String> dataMembers  = new ArrayList<String>();

		source.append("//This file is generated automatically, " +
				"do not edit its content except:\n" +
				"//\t- the package name below.\n" +
				"//\t- any variable name that is not a valid Java identifier. \n" +
				"//\t\tPlease add such variable to the method getSpecialDeclaredFields() manually.\n");
		source.append("package " + path + ";\n\n");
		source.append("import com.hp.hpl.jena.rdf.model.Property;\n");
		source.append("import com.hp.hpl.jena.rdf.model.Resource;\n");
		source.append("import com.hp.hpl.jena.rdf.model.ResourceFactory;\n");
		source.append("import java.lang.reflect.Field;\n");
		source.append("import java.util.HashMap;\n");
		source.append("\n");

		source.append("public class " + className.toUpperCase() + " {\n");
		source.append("\tpublic static String NS = \"" + nameSpace + "\";\n");
		source.append("\tpublic static final Resource NAMESPACE = ResourceFactory.createResource(NS);\n");
		source.append("\tpublic static final String PREFIX = \"" + className.toLowerCase() + "\";\n");
		source.append("\n");

		// Resources
		ResIterator ri = vocabModel.listResourcesWithProperty(RDF.type, RDFS.Class);
		List<Resource> rdfsClasses = ri.toList();
		Collections.sort(rdfsClasses,new Comparator<Resource>() {
			public int compare(Resource o1, Resource o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		for (Resource c : rdfsClasses) {
			source.append("\tpublic static Resource " + c.getLocalName() + " = ResourceFactory.createProperty( NS + \""+ c.getLocalName() + "\");\n");
			dataMembers.add(c.getLocalName());
		}
		source.append("\n");

		// Properties
		ri = vocabModel.listResourcesWithProperty(RDF.type, RDF.Property);
		List<Resource> rdfProperties = ri.toList();
		Collections.sort(rdfProperties,new Comparator<Resource>() {
			public int compare(Resource o1, Resource o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		for (Resource c : rdfProperties) {
			source.append("\tpublic static Property " + c.getLocalName()+ " = ResourceFactory.createProperty( NS + \"" + c.getLocalName() + "\");\n");
			dataMembers.add(c.getLocalName());
		}
		source.append("\n");

		// Individuals
		StmtIterator iter = vocabModel.listStatements(
				new SimpleSelector(null, RDFS.isDefinedBy, (RDFNode) null) {
					public boolean selects(Statement s)
						{return true;}
				});
		List<Statement> stmtList = iter.toList();
		Collections.sort(stmtList,new Comparator<Statement>() {
			public int compare(Statement o1, Statement o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		for (Statement i: stmtList) {
			if (!dataMembers.contains(i.getSubject().getLocalName())) {
				source.append("\tpublic static Resource " + i.getSubject().getLocalName() + " = ResourceFactory.createProperty( NS + \""+ i.getSubject().getLocalName() + "\");\n");
			}
		}

		source.append("\n");
		source.append("\tpublic static String getURI() {\n");
		source.append("\t\treturn (NS);\n\t}\n");


		source.append("\tpublic HashMap<String, String> getSpecialDeclaredFields() {\n");
		source.append("\t\tField[] fields = this.getClass().getFields();\n");
		source.append("\t\tHashMap<String, String> simpleFields = new HashMap<String, String>();\n");
		source.append("\t\tfor (Field i: fields) {simpleFields.put(i.getName(), i.getType().toString());}\n");
		source.append("\t\t// Add Property/Resource with some special characters in the name \n" );
		source.append("\t\t// For example, simpleFields.put(\"Exactly-one\", \"interface com.hp.hpl.jena.rdf.model.Resource\");\n");
		source.append("\t\treturn simpleFields;\n");
		source.append("\t}\n");

		source.append("}\n");
		System.out.println(source.toString());
	}
}
