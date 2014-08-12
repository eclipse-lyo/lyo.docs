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
package org.eclipse.lyo.resourcetable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.lyo.tools.common.util.CheckForCommonMistakes;
import org.eclipse.lyo.tools.common.util.FileSuffixToJenaLanguage;
import org.eclipse.lyo.tools.common.util.FilenameParser;
import org.eclipse.lyo.tools.common.util.ReadFileIntoNewModel;
import org.eclipse.lyo.tools.common.vocabulary.oslc.changemgmt.CM;
import org.eclipse.lyo.tools.common.vocabulary.oslc.core.OSLC;
import org.eclipse.lyo.tools.common.vocabulary.oslc.qm.QM;
import org.eclipse.lyo.tools.common.vocabulary.oslc.reconciliation.CRTV;
import org.eclipse.lyo.tools.common.vocabulary.oslc.rm.RM;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class ResourceShape2WikiText {

	private Model shapes;
	private ResourceTableHandler rtHandler = null;
	static Map<String,String> nsPrefixMap = new LinkedHashMap<String,String>();
	private static final String NSPREFIX_AUTHORING = "authoring";
	private static final String NS_OSLC_AUTHORING = "http://open-services.net/ns/authoring#";
	
	private static final String NSPREFIX_DCTERMS = "dc";
	private static final String NSPREFIX_DCTERMS2 = "dcterms";
	private static final String NSPREFIX_XSD = "xsd";
	private static final String NSPREFIX_XSD2 = "xs";

	final static String NS_ = "";
	final static String NS_EXAMPLE_ORG = "http://example.org#";
	final static String nsCOREX = NS_OSLC_AUTHORING;
	final static Property oslcDescribesP = OSLC.describes;
	final static Property rdfsLabelP = RDFS.label;
	final Property corexOrder = ResourceFactory.createProperty(nsCOREX+"order");
	final Property corexRangeSuggestion = ResourceFactory.createProperty(nsCOREX+"rangeSuggestion");
	private static PrintWriter pw;
	private static boolean strict = false;
	
	private static Set<String> oslcCommonProps;
	private static Set<String> fixedRangeProps;
	
	private boolean uriMode = true;
	private boolean callOut = true;
	
	
	static
	{
		try
		{
			loadFixedRangeProperties();
			loadOslcCommonProperties();
			prepareNsPrefixMap();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void loadFixedRangeProperties() throws IOException
	{
		BufferedReader reader = null;
		fixedRangeProps = new HashSet<String>();
		fixedRangeProps.add(org.eclipse.lyo.tools.common.vocabulary.oslc.core.OSLC.instanceShape.getURI());
		fixedRangeProps.add(org.eclipse.lyo.tools.common.vocabulary.oslc.core.OSLC.serviceProvider.getURI());

		try
		{
			String fixedRangePropFile = "/wiki/fixedrangeprops.txt";
			InputStream is = ResourceShape2WikiText.class.getResourceAsStream(fixedRangePropFile);
			if(is != null)
			{
		    	reader = new BufferedReader(new InputStreamReader(is));
		    	String prop = reader.readLine();
		    	while(prop != null)
		    	{
		    		oslcCommonProps.add(prop);
		    		prop = reader.readLine();
		    	}
			}
		}
		finally
		{
			if(reader != null)
				reader.close();
		}
	}
	
	/**
	 * Prepare a namespace/prefix map for the well known domains/vocabularies. Others can be passed into the tool using the -m[prefix]|[uri] arg.
	 * These mappings help resolve the wiki text to concrete resource/property uri in the shape document.
	 * @return
	 */
	private static void prepareNsPrefixMap()
	{
		nsPrefixMap.put(OSLC.NS,"oslc");
		nsPrefixMap.put(NS_OSLC_AUTHORING,NSPREFIX_AUTHORING);
		nsPrefixMap.put(XSD.getURI(), NSPREFIX_XSD);
		nsPrefixMap.put(XSD.getURI(), NSPREFIX_XSD2);
		nsPrefixMap.put(RDF.getURI(), "rdf");
		nsPrefixMap.put(RDFS.getURI(), "rdfs");
		nsPrefixMap.put(DCTerms.NS, NSPREFIX_DCTERMS);
		nsPrefixMap.put(DCTerms.NS, NSPREFIX_DCTERMS2);
		nsPrefixMap.put("http://xmlns.com/foaf/0.1/", "foaf");

		nsPrefixMap.put(CRTV.NS, CRTV.PREFIX);
		nsPrefixMap.put(CM.NS, CM.PREFIX);
		nsPrefixMap.put(QM.NS, QM.PREFIX);
		nsPrefixMap.put(RM.NS, RM.PREFIX);
	}
	
	/**
	 * load oslc common properties. It can be configured using a commonprops.txt too.
	 * @throws IOException
	 */
	private static void loadOslcCommonProperties() throws IOException
	{
		BufferedReader reader = null;
		try
		{
			oslcCommonProps = new HashSet<String>();
			oslcCommonProps.add(RDF.type.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.description.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.title.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.contributor.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.identifier.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.creator.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.publisher.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.subject.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.created.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.modified.getURI());
			oslcCommonProps.add(com.hp.hpl.jena.vocabulary.DCTerms.relation.getURI());

			oslcCommonProps.add(OSLC.discussedBy.getURI());
			oslcCommonProps.add(OSLC.instanceShape.getURI());
			oslcCommonProps.add(OSLC.serviceProvider.getURI());
			oslcCommonProps.add(OSLC.shortId.getURI());
			oslcCommonProps.add(OSLC.shortTitle.getURI());
			oslcCommonProps.add(OSLC.modifiedBy.getURI());
			oslcCommonProps.add(OSLC.partOfDiscussion.getURI());
			oslcCommonProps.add(OSLC.inReplyTo.getURI());

			oslcCommonProps.add(oslcDescribesP.getURI());
			String commonpropFile = "/wiki/commonprops.txt";

			InputStream is = ResourceShape2WikiText.class.getResourceAsStream(commonpropFile);
			if(is != null)
			{
		    	reader = new BufferedReader(new InputStreamReader(is));
		    	String prop = reader.readLine();
		    	while(prop != null)
		    	{
		    		oslcCommonProps.add(prop);
		    		prop = reader.readLine();
		    	}
			}
		}
		finally
		{
			if(reader != null)
				reader.close();
		}
	}

	public ResourceShape2WikiText(Model shapes, boolean uriMode, boolean callOut, ResourceTableHandler rtHandler) {
		this.shapes = shapes;
		this.uriMode = uriMode;
		this.callOut = callOut;
		this.rtHandler = rtHandler;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		boolean uriMode = true;
		boolean callOut = true;
		String infilename = null;
		String inModelLanguage = null;
		ResourceTableHandler handler = null;
		for(String arg : args)
		{
			if(arg.startsWith("-f"))
			{
				infilename = arg.substring("-f".length());
				FilenameParser fp = new FilenameParser(infilename);
				String inDir = fp.getFullyQualifiedName();
				log("Input resource shape:\t" + inDir);
				String in_FileNameRoot = fp.getRoot();
				String in_FileNameSuffix = fp.getSuffix();

				inModelLanguage = FileSuffixToJenaLanguage.toLang(in_FileNameSuffix);

				String outFileNameRoot = null;
				outFileNameRoot = in_FileNameRoot + ".wiki";
				pw = new PrintWriter(new FileOutputStream(outFileNameRoot),true);
			}
			if(arg.equalsIgnoreCase("-nocallout"))
			{
				callOut = false;
			}
			if(arg.startsWith("-wf"))
			{
				String format = arg.substring("-wf".length());
				handler = ResourceTableHandler.getInstance(format);
			}

			if(arg.startsWith("-m"))
			{
				String mapping = arg.substring("-m".length());
				StringTokenizer strtk = new StringTokenizer(mapping,"|");
				String prefix = strtk.nextToken();
				String uri = strtk.nextToken();
				nsPrefixMap.put(uri, prefix);
			}
		}
		if(handler == null)
		{
			handler = ResourceTableHandler.getDefaultHandler();
		}
		Model shapes = ReadFileIntoNewModel.read(infilename,inModelLanguage);
		if ( shapes == null )
		{
			return;	//	Assumption is that routine above yelled if anything wrong
		}
		CheckForCommonMistakes.check(shapes);

		ResourceShape2WikiText me = new ResourceShape2WikiText(shapes,uriMode,callOut,handler);
		try{
			byte[] data = me.mainline();
			pw.print(new String(data));
			pw.flush();
		}
		finally{
			pw.close();
		}

	}

	private byte[] mainline() throws Exception {
		
		removeSubjectsInNamespace(NS_EXAMPLE_ORG);
		//	Scan for example.org predicates, objects, those will be errors
		List<Statement> badPredicates = listPrediatesInNamespace(NS_EXAMPLE_ORG);
		shapes.remove(badPredicates);
		List<Statement> badObjects = listObjectsInNamespace(NS_EXAMPLE_ORG);
		shapes.remove(badObjects);
		wikiTextForShape(shapes);
		return rtHandler.generateResourceTable();
	}
	
	private Seq getPropertyOrderSeq(Model shape)
	{
		ResIterator itr = shape.listSubjects();
		while(itr.hasNext())
		{
			Resource res = itr.nextResource();
			if(res.getURI().endsWith("_sections_"))
			{
				return shapes.getSeq(res);
			}
		}
		return null;
	}
	
	public static byte[] modelToBytes(Model rdfModel)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		rdfModel.write(out, "TTL");
		byte[] resourceRep = out.toByteArray();
		return resourceRep;
	}
	
	private String setResourceTableHeader(Resource shape)
	{
		//System.out.println(shape);
		Statement describesStmt = shape.getProperty(oslcDescribesP);
		//System.out.println(describesStmt);

		String localName = "";
		if(describesStmt != null)
		{
			rtHandler.setResourceType(describesStmt.getResource());
			localName = describesStmt.getResource().getLocalName();
			
		}
		Statement dcTitleStmt = shape.getProperty(DCTerms.title);
		if(dcTitleStmt != null)
		{
			rtHandler.setResourceDescription(dcTitleStmt.getString());
		}
		return localName;
	}

	private void wikiTextForShape(Model shapes) {
		Resource oslcResourceShapeR = ResourceFactory.createResource("http://open-services.net/ns/core#ResourceShape");
		List<Resource> goodShapes = shapes.listResourcesWithProperty(RDF.type,oslcResourceShapeR).toList();
		Resource shape = null;
		if(goodShapes.size()>=1)
		{
			shape = goodShapes.get(0);
		}
		else
		{
			log("No valid shape resource found!");
			System.exit(1);
		}
		String resLocalName = this.setResourceTableHeader(shape);
		Property p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.ResourceShape.property ;
		Seq seq = getPropertyOrderSeq(shapes);
		if(seq != null)//basically has embedded sections under http://open-services.net/wiki#_sections_
		{
			int size = seq.size();
			for(int i=1;i<=size;i++)
			{
				Seq secSeq = seq.getSeq(i);
				Statement descstmt = secSeq.getProperty(DCTerms.title);
				if(descstmt != null)
				{
					String sectionDesc = descstmt.getString();
					rtHandler.addResourceTableSection(new ResourceTableHandler.ResourceTableSection(sectionDesc));
				}
				int secSeqSize = secSeq.size();
				List<Resource> propertyResourceList = new ArrayList<Resource>(secSeqSize);

				for(int j=1;j<=secSeqSize;j++)
				{
					propertyResourceList.add(secSeq.getResource(j));
				}
				for(Resource propResource : propertyResourceList)		{
					wikiTextForResourceShapeProperty(propResource);
				}

			}
		}
		else
		{
			List<Resource> commonPropertyResourceList = new ArrayList<Resource>();
			List<Resource> additionalPropertyResourceList = new ArrayList<Resource>();

			StmtIterator sIter = shape.listProperties(p);
			while (sIter.hasNext()) {
				Statement s = sIter.nextStatement();
				RDFNode o = s.getObject();
				if ( o.isResource() ) {
					if(this.isCommonProperty(o.asResource()))
					{
						commonPropertyResourceList.add(o.asResource());
					}
					else
					{
						additionalPropertyResourceList.add(o.asResource());
					}
				}
				else {
					log(p.toString() + " is used where an instance of core:property was expected, as the object of the statement " /*+ s1*/);
				}
			}
			
			if(commonPropertyResourceList.size()>0)
			{
				rtHandler.addResourceTableSection(new ResourceTableHandler.ResourceTableSection("OSLC OSLC: Common Properties"));
				for(Resource propResource : commonPropertyResourceList)		{
					wikiTextForResourceShapeProperty(propResource);
				}
			}
			
			if(additionalPropertyResourceList.size()>0)
			{
				rtHandler.addResourceTableSection(new ResourceTableHandler.ResourceTableSection("OSLC "+resLocalName+": Start of additional properties"));

				for(Resource propResource : additionalPropertyResourceList)		{
					wikiTextForResourceShapeProperty(propResource);
				}
			}

		}
	}
	
	private boolean isCommonProperty(Resource propertyResource)
	{
		Statement propertyDefnStmt = propertyResource.getProperty(org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.propertyDefinition);
		if ( propertyDefnStmt != null ) {
			RDFNode value = propertyDefnStmt.getObject();
			if ( value.isURIResource() )
			{
				return oslcCommonProps.contains(value.asResource().getURI());
			}
		}
		return false;
	}
	
	
	protected void sortProperties(List<Resource> listProps)
	{
		Collections.sort(listProps,new Comparator<Resource>() {
            public int compare(Resource res1, Resource res2) {
            	Statement stmt1 = res1.getProperty(corexOrder);
            	if(stmt1 == null)    	{
            		throw new RuntimeException("shape property missing "+NSPREFIX_AUTHORING+":order predicate : " + res1.getURI());
            	}
            	int orderRes1 = stmt1.getInt();
            	
               	Statement stmt2 = res2.getProperty(corexOrder);
            	if(stmt2 == null)    	{
            		throw new RuntimeException("shape property missing "+NSPREFIX_AUTHORING+":order predicate : " + res2.getURI());
            	}
            	int orderRes2 = stmt2.getInt();
            	if(orderRes1==orderRes2)
            	{
            		throw new RuntimeException("shape properties have duplicate "+NSPREFIX_AUTHORING+":order predicate with value " + orderRes1);
            	}
                return orderRes1-orderRes2;
            }
        });
	}
	
	public String wikiTextForResourceShapeProperty(RDFNode o) {
		Property p = null;
		Resource propertyResource = o.asResource();
		List<Statement> s = null;
		String propertyUri = "" , propertyDesc = "" ;
		String propertyReadOnly = "" , propertyRep = "" , propertyRange = "";
		String wiki = "";
		boolean localResource = false;

		//	Ultimately should drive based on an input resource shape.  This is written for spec authoring purposes.
		ResourceTableHandler.ResourceTableRow rtRow = new ResourceTableHandler.ResourceTableRow(shapes);
		rtHandler.addResourceTableRow(rtRow);
		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.propertyDefinition;
		Statement propertyDefnStmt = propertyResource.getProperty(p);
		if ( propertyDefnStmt != null ) {
			RDFNode value = propertyDefnStmt.getObject();
			if ( value.isURIResource() ) propertyUri = value.asResource().getURI();
			if ( !value.isURIResource() ) {
				rtRow.setInvalidResourceURIMsg("***Object is not a URI but should be a URI***");
			} else {
				Resource r = value.asResource();
				rtRow.setPropertyRes(r);
			}
		} else
			rtRow.setInvalidResourceURIMsg("missing:propertyDefinition");

		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.occurs;
		Statement propertyOccursStmt = propertyResource.getProperty(p);
		if ( propertyOccursStmt != null ) 
		{
			RDFNode occursValue = propertyOccursStmt.getObject();
			if ( !occursValue.isURIResource() ) 
			{
				rtRow.setInvalidOccursURIMsg("***Object is not a URI but should be an enum URI***");
			} 
			else
			{
				Resource r = occursValue.asResource();
				String uri = r.getURI();
				if ( !org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validOccurs.contains(occursValue)) 
				{
					if(!strict)
					{
						String localPart = this.getLocalPart(uri).toLowerCase();
						if(localPart.contains("zero") && localPart.contains("one"))
						{
							r = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validOccursValues[1];
						}
						else if(localPart.contains("zero") && localPart.contains("many"))
						{
							r = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validOccursValues[2];
						}
						else if(localPart.contains("one") && localPart.contains("many"))
						{
							r = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validOccursValues[3];
						}
						else if(localPart.contains("exactly") && localPart.contains("one"))
						{
							r = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validOccursValues[0];
						}
						propertyOccursStmt.changeObject(r);
						rtRow.setOccurs(r);

					}
					else
					{
						rtRow.setInvalidOccursURIMsg("***Object is a URI but is not one of the OSLC URIs***");
					}
				} 
				else 
				{
					rtRow.setOccurs(r);
				}
			}
		} 
		else 
		{
			rtRow.setInvalidOccursURIMsg("***missing: occurs***");
		}

		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.readOnly;
		s = propertyResource.listProperties(p).toList();
		if ( s.size() == 1 ) {
			RDFNode roValue = s.get(0).getObject();
			if ( !roValue.isLiteral() ) {
				propertyReadOnly = "***Object is not a literal***" ;
				rtRow.setInvalidReadonlyMsg(propertyReadOnly);
			} else {
				rtRow.setReadOnly((Boolean)roValue.asLiteral().getValue());
			}
		} else 
		if ( s.size() == 0 ) {
//			propertyReadOnly = "Unspecified";
//			rtRow.setInvalidReadonlyMsg(propertyReadOnly);
		} 
		else
		{
			propertyReadOnly = "***Too many values***";
			rtRow.setInvalidReadonlyMsg(propertyReadOnly);
		}
		wiki += "| " + propertyReadOnly + " ";

		//	OSLC allows 0:*
		//	(Virtually?) every spec uses 1:1 - if you want spec automation, use ==1
		//	Other predicates' values (representation, range) depend on its value too
		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.valueType;
		s = propertyResource.listProperties(p).toList();
		if ( s.size() != 1 ) rtRow.setInvalidValueTypeMsg("***Wrong number of value types, run validation to list messages***");
		else {
			RDFNode vtValue = s.get(0).getObject();
			if ( !vtValue.isURIResource() ) 
			{
				rtRow.setInvalidValueTypeMsg("***Non-URI value type, run validation to list messages***");
			} 
			else
			{
				Resource r = vtValue.asResource();
				rtRow.setValueType(r);
				String uri = r.toString();
				String ncName = uri.substring(uri.indexOf('#')+1);

				if ( !org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validValueType.contains(vtValue)) 
				{
					
					if(!strict)
					{
						if(ncName.equalsIgnoreCase("date") || ncName.equalsIgnoreCase("time"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.dateTime;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("string"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.xstring;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("double"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.xdouble;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("float"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.xfloat;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("decimal"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.decimal;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("boolean"))
						{
							r = com.hp.hpl.jena.vocabulary.XSD.xboolean;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("long") || ncName.equalsIgnoreCase("int") || 
								ncName.equalsIgnoreCase("integer") || ncName.equalsIgnoreCase("short") || 
								ncName.equalsIgnoreCase("shortint") )
						{
							r = com.hp.hpl.jena.vocabulary.XSD.xint;
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("xmlliteral"))
						{
							r = ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("resource"))
						{
							r = ResourceFactory.createResource(OSLC.NS+"Resource") ;
							s.get(0).changeObject(r);
						}
						else if(ncName.toLowerCase().contains("local"))
						{
							r = ResourceFactory.createResource(OSLC.NS+"LocalResource") ;
							s.get(0).changeObject(r);
						}
						else if(ncName.toLowerCase().contains("either") || ncName.toLowerCase().contains("any"))
						{
							r = ResourceFactory.createResource(OSLC.NS+"AnyResource") ;
							s.get(0).changeObject(r);
						}
						rtRow.setValueType(r);
						
						// Update defaults for other columns based on value type
						if ( org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validValueTypeLiteral.contains(r) ) {//Anamitra - fix shape here by deleteing those statements from shape
							propertyRep = "N/A";
							propertyResource.removeAll(org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.representation);
							propertyRange = "N/A";
							propertyResource.removeAll(org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.range);
						}
						
						if(isLocalResource(r))
						{
							localResource = true;
						}

						if ( org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validValueTypeResource.contains(r) ) {//Anamitra - if anything other than Any - then fix shape and move that to suggested range predicate
							Resource rRange = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.defaultRange;
							rtRow.setRange(rRange);
						}
					}
					else
					{
						rtRow.setInvalidValueTypeMsg("***Unknown URI value type, run validation to list messages***");//Anamitra - apply intelligence to fix stuff here
					}
				} 
				else 
				{
					// Update defaults for other columns based on value type
					if ( org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validValueTypeLiteral.contains(vtValue) ) {//Anamitra - fix shape here by deleteing those statements from shape
						propertyRep = "N/A";
						propertyResource.removeAll(org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.representation);
						propertyRange = "N/A";
						propertyResource.removeAll(org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.range);
					}
					
					if(isLocalResource(r))
					{
						localResource = true;
					}

					if ( org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validValueTypeResource.contains(vtValue) ) {//Anamitra - if anything other than Any - then fix shape and move that to suggested range predicate
						Resource rRange = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.defaultRange;
						rtRow.setRange(rRange);
						//propertyRange = uriMode?"[Any]("+r.getURI()+")":"Any";
					}

				}
			}
		}
		//wiki += "| " + propertyValueType + " ";
		

		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.representation;
		s = propertyResource.listProperties(p).toList();
		if (s.size() == 1) {
			RDFNode representationValue = s.get(0).getObject();
			if (!representationValue.isURIResource()) {
				propertyRep = "***Non-URI representation, run validation to list messages***";
			} 
			else
			{
				Resource r = representationValue.asResource();
				rtRow.setRepresentation(r);
				String uri = r.toString();
				String ncName = uri.substring(uri.indexOf('#')+1);

				if (!org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.validRepresentation
					.contains(representationValue.asResource())) 
				{
					if(!strict)
					{
						if(ncName.equalsIgnoreCase("Reference"))
						{
							r = ResourceFactory.createResource(OSLC.NS+"Reference");
							s.get(0).changeObject(r);
						}
						else if(ncName.equalsIgnoreCase("Inline"))
						{
							r = ResourceFactory.createResource(OSLC.NS+"Inline");
							s.get(0).changeObject(r);
						}
						else
						{
							r = ResourceFactory.createResource(OSLC.NS+"Either");
							s.get(0).changeObject(r);
						}
						rtRow.setRepresentation(r);
						if(localResource && !r.equals(ResourceFactory.createResource(OSLC.NS+"Inline")))
						{
							propertyResource.removeAll(p);
							propertyResource.addProperty(p, ResourceFactory.createResource(OSLC.NS+"Inline"));
						}
					}
					else
					{
						rtRow.setInvalidRepresentationURIMsg("***Unknown URI representation, run validation to list messages***");
					}
				} 
				else 
				{
					if(localResource && !r.equals(ResourceFactory.createResource(OSLC.NS+"Inline")))
					{
						propertyResource.removeAll(p);
						propertyResource.addProperty(p, ResourceFactory.createResource(OSLC.NS+"Inline"));
					}
				}
			}
		} else if (s.size() == 0 && propertyRep.equals("")) { // Omitted AND a
																// suitable
																// default not
																// yet provided
			rtRow.setInvalidRepresentationURIMsg("***Unspecified***");
		} else if ( s.size() > 1 )
			rtRow.setInvalidRepresentationURIMsg("***Too many values***");

		p = org.eclipse.lyo.tools.common.vocabulary.oslc.core.Property.range;
		s = propertyResource.listProperties(p).toList();
		String suggestedRangeNcName = null;
		String suggestedRangeURI = null;//fully qualified name of the range resource
		if (s.size() == 1) {
			RDFNode rangeValue = s.get(0).getObject();
			if (!rangeValue.isURIResource()) {
				rtRow.setInvalidRangeURIMsg("***Non-URI range, run validation to list messages***");
			}  else {
				Resource r = rangeValue.asResource();
				rtRow.setRange(r);
				String uri = r.toString();
				String ncName = null;
				if(fixedRangeProps.contains(propertyUri))
				{
					ncName = this.getNcName(shapes, r.getLocalName(), r.getNameSpace());
				}
				else
				{
					ncName = shapes.shortForm(OSLC.NS+"Any");
					rtRow.setRange(ResourceFactory.createResource(OSLC.NS+"Any"));
					if(!uri.equals(OSLC.NS+"Any"))
					{
						suggestedRangeURI = uri;
						String nsURI = (new PropertyImpl(uri)).getNameSpace();
						String localPart = (new PropertyImpl(uri)).getLocalName();
						suggestedRangeNcName = this.getNcName(shapes, localPart, nsURI);
					}
					uri = OSLC.NS+"Any";
				}
				
			}
		} else if (s.size() == 0 && propertyRange.equals("")) { // Omitted AND a
																// suitable
																// default not
																// yet provided
			rtRow.setInvalidRangeURIMsg("***Unspecified***");
		} else if ( s.size() > 1 )
			rtRow.setInvalidRangeURIMsg("***Too many values***");

		p = DCTerms.description;
		s = propertyResource.listProperties(p).toList();
		if ( s.size() == 1 ) 
		{
			RDFNode value = s.get(0).getObject();
			propertyDesc = value.toString();	
			if(!propertyDesc.trim().endsWith("."))
			{
				propertyDesc += "."; 
			}
			rtRow.setPropDesc(propertyDesc);
		} 
		else
		{
			String rdfsComment = this.callOut?new RDFSResolver(propertyUri).getRDFSComment():null;
			if(rdfsComment == null)
			{
				rtRow.setPropDesc("***missing: property description***");
			}
			else
			{
				propertyDesc = rdfsComment;
				if(!propertyDesc.trim().endsWith("."))
				{
					propertyDesc += "."; 
				}
				rtRow.setPropDesc(propertyDesc);
			}
		}
		
		if(suggestedRangeNcName != null && !this.hasSuggestedRangeInDesc(propertyDesc, suggestedRangeNcName))
		{
			String propertyRangeSuggestion = rtHandler.formatURI2Text(suggestedRangeNcName, suggestedRangeURI);//uriMode?"[`" + suggestedRangeNcName + "`](" + suggestedRangeURI + ") ":shapes.shortForm(suggestedRangeURI);
			propertyDesc+=" It is likely that the target resource will be an "+propertyRangeSuggestion+" but that is not necessarily the case.";
			rtRow.setPropDesc(propertyDesc);
		}
		else
		{
			s = propertyResource.listProperties(corexRangeSuggestion).toList();
			if(s.size()==1)
			{
				RDFNode rangeHintValue = s.get(0).getObject();
				if (!rangeHintValue.isURIResource()) {
					propertyDesc+="***Non-URI rangeSuggestion, run validation to list messages***";
					rtRow.setPropDesc(propertyDesc);
				} else {
					Resource r = rangeHintValue.asResource();
					String localPart = r.getLocalName();
					
					String nsURI = r.getNameSpace();
					String uri = r.getURI();
					String ncName = this.getNcName(shapes, localPart, nsURI);
					if(!this.hasSuggestedRangeInDesc(propertyDesc, ncName))
					{
						String propertyRangeSuggestion = uriMode?"[`" + ncName + "`](" + uri + ") ":shapes.shortForm(uri);
						propertyDesc+=" It is likely that the target resource will be an "+propertyRangeSuggestion+" but that is not necessarily the case.";
						rtRow.setPropDesc(propertyDesc);
					}
				}
				
			}

		}
		return wiki;
	}
	
	private String getNcName(Model shape, String localPart, String nsURI)
	{
		String ncName = null;

		if(nsPrefixMap.containsKey(nsURI))
		{						
			ncName = nsPrefixMap.get(nsURI)+":"+localPart;
		}
		else if(shapes.getNsPrefixMap().containsKey(nsURI))
		{
			ncName = shapes.shortForm(nsURI+localPart);
		}
		else
		{
			ncName = this.prefixFromNSURI(nsURI)+":"+localPart;
		}
		return ncName;
	}
	
	private boolean hasSuggestedRangeInDesc(String desc, String ncName)
	{
		if(desc.indexOf(ncName)>0) return true;
		if(desc.indexOf(ncName.substring(ncName.indexOf(':')))>0) return true;
		return false;
	}
	
	private String prefixFromNSURI(String nsURI)
	{
		nsURI = nsURI.replaceAll("#", "");
		return nsURI.substring(nsURI.lastIndexOf('/')+1);
	}
	
	private boolean isLocalResource(Resource r)
	{
		return (r.equals(ResourceFactory.createResource(OSLC.NS+"LocalResource")));
	}
	
	
	private String getLocalPart(String uri)
	{
		Property p = new PropertyImpl(uri);
		return p.getLocalName();
	}

	private List<Statement> listObjectsInNamespace(String ns) {
		List<Statement> nsObjs = new ArrayList<Statement>();
		List<RDFNode> objs = shapes.listObjects().toList();
		for ( RDFNode obj : objs ) {
			if ( obj.isURIResource() ){
				Resource r = obj.asResource();
				String uri = r.getURI();
				uri = shapes.expandPrefix(uri);
				if ( uri != null && uri.startsWith(ns) ) {
					nsObjs.addAll(shapes.listStatements(null, null, r).toList());
				}
			}
		}
		return nsObjs;
	}

	private List<Statement> listPrediatesInNamespace(String ns) {
		List<Statement> nsPreds = new ArrayList<Statement>();
		List<Statement> ss = shapes.listStatements().toList();
		for ( Statement s : ss ) {
			Property p = s.getPredicate();
				String uri = p.getURI();
				uri = shapes.expandPrefix(uri);
				if ( uri != null && uri.startsWith(ns) ) {
					nsPreds.add(s);
				}
		}
		return nsPreds;
	}

	private void removeSubjectsInNamespace(String ns) {
		List<Resource> subjs = shapes.listSubjects().toList();
		for ( Resource subj : subjs ) {
			if(!subj.isURIResource()) continue;
			String uri = subj.getURI();
			uri = shapes.expandPrefix(uri);
			if ( uri != null && uri.startsWith(ns) ) {
				shapes.removeAll(subj, null, null);
			}
		}
	}
	
	private static void log(String text)
	{
		System.out.println(text);
	}


}
