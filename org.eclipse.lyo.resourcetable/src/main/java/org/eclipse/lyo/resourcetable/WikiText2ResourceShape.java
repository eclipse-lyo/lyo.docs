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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.lyo.tools.common.util.ReadFileIntoNewModel;
import org.eclipse.lyo.tools.common.vocabulary.oslc.changemgmt.CM;
import org.eclipse.lyo.tools.common.vocabulary.oslc.core.OSLC;
import org.eclipse.lyo.tools.common.vocabulary.oslc.qm.QM;
import org.eclipse.lyo.tools.common.vocabulary.oslc.rm.RM;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Utility to generate an OSLC shape document from a tWiki or mediawiki format resource table.
 * @author Anamitra Bhattacharyya
 */
public class WikiText2ResourceShape 
{
	//private static final Logger logger = Logger.getLogger(WikiText2ResourceShape.class);
	
	private static final String NSPREFIX_AUTHORING = "authoring";
	private static final String NS_OSLC_AUTHORING = "http://open-services.net/ns/authoring#";
	private static final String NSPREFIX_DCTERMS = "dc";
	private static final String NSPREFIX_DCTERMS2 = "dcterms";
	private static final String NSPREFIX_XSD = "xsd";
	private static final String NSPREFIX_XSD2 = "xs";
	
	private static Pattern ENDOFURI = null;;
	private static Pattern ANYALPHA = null;
	private static Pattern TYPEURI = null;
	private static Pattern DESCRIPTION = null;
	private static Pattern TWIKI_NAMESPACEURI = null;
	private static Pattern TWIKI_RESOURCE = null;
	
	private static final String space;
	
	final Property oslcDescribesP = OSLC.describes;
	final Property rdfsLabelP = RDFS.label;
	final Property rdftypeP = RDF.type;
	
	private static Set<String> fixedRangeProps;

	private String outAbsoluteFileName;
	
	static
	{
		char[] spaceChars = new char[10];
		BasicConfigurator.configure();
		Arrays.fill(spaceChars, ' ');
		space = new String(spaceChars);
		
		prepareFixedRangePropList();
		prepareRegExForParsingWiki();
	}
	
	/**
	 * Prepares the RegEx for parsing various wiki formats - currently supported are mediawiki and tWiki formats.
	 */
	private static void prepareRegExForParsingWiki()
	{
		ENDOFURI = Pattern.compile("[\\p{Cntrl}\\p{Space}>='`\"\\]]");
		ANYALPHA = Pattern.compile("[a-zA-Z]");
		TYPEURI = Pattern.compile("(?)\\*type\\p{Space}*uri(?)");
		DESCRIPTION = Pattern.compile("(?)\\*description(?)");
		TWIKI_NAMESPACEURI = Pattern.compile("((?)\\*namespace\\p{Space}*uri(?)|(?)\\p{Space}*uri(?)|(?)(?)\\p{Space}*namespace(?))");//Pattern.compile("(?)\\*namespace\\p{Space}*uri(?)");
		TWIKI_RESOURCE = Pattern.compile("(?)\\*resource(?)");
	}
	
	/**
	 * Prepares the list of properties that have a fixed "range" as opposed to "Any" range.
	 */
	private static void prepareFixedRangePropList()
	{
		fixedRangeProps = new HashSet<String>();
		fixedRangeProps.add(OSLC.instanceShape.getURI());
		fixedRangeProps.add(OSLC.serviceProvider.getURI());
	}
	
	/**
	 * Prepare a namespace/prefix map for the well known domains/vocabularies. Others can be passed into the tool using the -m[prefix]|[uri] arg.
	 * These mappings help resolve the wiki text to concrete resource/property uri in the shape document.
	 * @return
	 */
	private static Map<String,String> prepareNsPrefixMap()
	{
		Map<String,String> nsPrefixMap = new LinkedHashMap<String,String>();//use linked map to always maintain the order
		nsPrefixMap.put(OSLC.NS,"oslc");
		nsPrefixMap.put(NS_OSLC_AUTHORING,NSPREFIX_AUTHORING);
		nsPrefixMap.put(XSD.getURI(), NSPREFIX_XSD);
		nsPrefixMap.put(XSD.getURI(), NSPREFIX_XSD2);
		nsPrefixMap.put(RDF.getURI(), "rdf");
		nsPrefixMap.put(RDFS.getURI(), "rdfs");
		nsPrefixMap.put(DCTerms.NS, NSPREFIX_DCTERMS);
		nsPrefixMap.put(DCTerms.NS, NSPREFIX_DCTERMS2);
		nsPrefixMap.put("http://xmlns.com/foaf/0.1/", "foaf");

		nsPrefixMap.put(CM.NS, CM.PREFIX);
		nsPrefixMap.put(QM.NS, QM.PREFIX);
		nsPrefixMap.put(RM.NS, RM.PREFIX);
		return nsPrefixMap;
	}
	
	/**
	 * Handle the input args for this tool. looking for filename "-f" and namespace prefix mappings "-m".
	 * @param args
	 * @param nsPrefixMap
	 * @return the fully qualified wikifile name to process.
	 */
	private static String analyzeInput(String[] args,Map<String,String> nsPrefixMap)
	{
		String infilename = null;
		for(String arg : args)
		{
			if(arg.startsWith("-f"))
			{
				infilename = arg.substring("-f".length());
			}
			else if(arg.startsWith("-m"))
			{
				String mapping = arg.substring("-m".length());
				StringTokenizer strtk = new StringTokenizer(mapping,"|");
				String prefix = strtk.nextToken();
				String uri = strtk.nextToken();
				nsPrefixMap.put(uri, prefix);
			}
		}
		return infilename;
	}
	
	private static void preparePrefixNsMap(Map<String,String> nsPrefixMap, Map<String,String> prefixNsMap)
	{
		Set<Map.Entry<String,String>> set = nsPrefixMap.entrySet();
		for(Map.Entry<String,String> entry : set)
		{
			if(!prefixNsMap.containsKey(entry.getValue()))
			{
				prefixNsMap.put(entry.getValue(), entry.getKey());
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		String infilename = null;
		String describes = null;
		Map<String,String> nsPrefixMap = prepareNsPrefixMap();
		Map<String,String> prefixNsMap = new HashMap<String,String>();

		if(args != null)
		{
			infilename = analyzeInput(args,nsPrefixMap);
		}
		
		if(infilename == null)
		{
			log("Error::Missing wiki file name! Did you forget the -f argument?");
			log("The usage is simple. This tool consumes a text file (passed to the tool using the -f arg) containing a block of wiki table text that decribes a resource.");
			log("The tool would then generate a Resource shape document in text/turtle format.");
			log("The tool has known namespaces mapped to known prefixes. However your resource might have some namespaces that are not");
			log("known to this too. A simple way to make the tool aware of those ns/prefix bindings is to use the -m arg.");
			log("The tool allows 0..* of those -m args. A sample usage is provided below.");
			log("Usage.. java -f<Wiki File to process> [-m<ns-prefix mapping>] wiki.WikiTest2ResourceShape");
			log("Example.. java -fcm_wiki.txt sandbox.WikiTest2ResourceShape");
			log("Example with additional Ns-Prefix mappings.. java -fcmwiki.txt -mex|http://example.org# -mabc|http://a.b# sandbox.WikiTest2ResourceShape");
			return;
		}
		
		preparePrefixNsMap(nsPrefixMap,prefixNsMap);
		
		WikiText2ResourceShape wiki2shape = new WikiText2ResourceShape(infilename,describes,prefixNsMap,nsPrefixMap);
		wiki2shape.validateGeneratedShape();
	}
	
	private static void log(String text)
	{
		System.out.println(text);
	}
	
	private int matchesPatternAt(Pattern p, String line)
	{
		Matcher m = p.matcher(line);
		if(m.find())
		{
			return m.start();
		}
		return -1;
	}
	
	private String findNSForMissingPrefix(String prefix, Map<String,String> nsPrefixMap)
	{
		Set<Map.Entry<String,String>> set = nsPrefixMap.entrySet();
		for(Map.Entry<String,String> entry : set)
		{
			String key = entry.getKey();
			String value = entry.getValue();
			if(value.equals(prefix)) return key;
		}
		return null;
	}
	
	public WikiText2ResourceShape(String inFileName, String describes, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap) throws Exception
	{
		BufferedReader reader = null;
		PrintWriter pw = null;
		try
		{
			ShapeInfo shapeInfo = new ShapeInfo();
			File f = new File(inFileName);
			if(!f.exists())
			{
				throw new FileNotFoundException("File "+inFileName+" not found");
			}
			String fName = f.getName();
			int dotInd = fName.indexOf('.');
			String outFileName = fName.substring(0, dotInd)+".ttl";
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName)));
			outAbsoluteFileName = f.getParent()+File.separator+outFileName;
			pw = new PrintWriter(new FileOutputStream(outAbsoluteFileName));
			
			String line = reader.readLine();
			String introText = null;//can be used as description for TWIKI syntax
			String modelBaseURI = null;
			while(line != null)
			{
				if(line.trim().length()==0)
				{
					line = reader.readLine();
					continue;
				}
				int indx = -1;;
				if(((indx = this.matchesPatternAt(DESCRIPTION, line.toLowerCase())) >=0) && !line.trim().startsWith("|"))
				{
					int descIndex = indx;//line.toLowerCase().indexOf("*description");
					int descStartIndex = descIndex+12;
					for(int i=(descIndex+12);i<line.length();i++)
					{
						if(ANYALPHA.matcher(String.valueOf(line.charAt(i))).matches())
						{
							descStartIndex = i;
							break;
						}
					}
					shapeInfo.title = line.substring(descStartIndex).trim();
					System.out.println(shapeInfo.title);
				}
				else if((indx = this.matchesPatternAt(TYPEURI, line.toLowerCase())) >=0  && !line.trim().startsWith("|"))
				{
					StringBuilder sb = new StringBuilder();
					int typeIndex = line.toLowerCase().indexOf("http:");
					for(int i=typeIndex;i<line.length();i++)
					{
						if(!ENDOFURI.matcher(String.valueOf(line.charAt(i))).matches())
						{
							sb.append(line.charAt(i));
						}
						else
						{
							break;
						}
					}
					String localPart = this.getLocalPart(sb.toString());
					String nsPart = this.getNSPart(sb.toString());
					String prefix = nsPrefixMap.get(nsPart);
					if(prefix == null)
					{
						prefix = this.guessPrefixFromNS(nsPart);
						nsPrefixMap.put(nsPart, prefix);
						prefixNsMap.put(prefix, nsPart);
					}
					shapeInfo.describes = prefix+":"+localPart;
					modelBaseURI = sb.toString();
					log(shapeInfo.describes);
				}
				if(((indx = this.matchesPatternAt(TWIKI_RESOURCE, line.toLowerCase())) >=0) && !line.trim().startsWith("|"))
				{
					int resIndex = indx;//line.toLowerCase().indexOf("*description");
					int resStartIndex = resIndex+11;
					for(int i=(resIndex+11);i<line.length();i++)
					{
						if(ANYALPHA.matcher(String.valueOf(line.charAt(i))).matches() || String.valueOf(line.charAt(i)).equals(":"))
						{
							resStartIndex = i;
							break;
						}
					}
					String res = this.curateToken(line.substring(resStartIndex).trim());
					StringTokenizer strtk = new StringTokenizer(res,":");
					if(strtk.countTokens()>=2)
					{
						shapeInfo.prefix = this.curateToken(strtk.nextToken());
						shapeInfo.localName = strtk.nextToken();
					}
					else
					{
						shapeInfo.localName = strtk.nextToken();
					}
					log(shapeInfo.localName);
				}
				else if((indx = this.matchesPatternAt(TWIKI_NAMESPACEURI, line.toLowerCase())) >=0  && !line.trim().startsWith("|") && line.toLowerCase().indexOf("http:")>0)
				{
					StringBuilder sb = new StringBuilder();
					int typeIndex = line.toLowerCase().indexOf("http:");
					//if(typeIndex<0) continue;
					for(int i=typeIndex;i<line.length();i++)
					{
						try
						{
							if(!ENDOFURI.matcher(String.valueOf(line.charAt(i))).matches())
							{
								sb.append(line.charAt(i));
							}
							else
							{
								break;
							}
						}
						catch(Exception e)
						{
							System.out.println(line+"------"+i);
							
							throw e;
						}
					}
					shapeInfo.nsURI = sb.toString();
					int hashIndex = shapeInfo.nsURI.indexOf('#');
					if(hashIndex>0)
					{
						String nsURI = shapeInfo.nsURI.substring(0, hashIndex);
						shapeInfo.localName = shapeInfo.nsURI.substring(hashIndex+1, shapeInfo.nsURI.length());
						shapeInfo.nsURI = nsURI+"#";
					}
				}
				
				else if(this.isTableHeader(line))
				{
					StringTokenizer strtk = new StringTokenizer(line,"|");
					if(strtk.countTokens()<7)
					{
						log("Invalid property table header - Expected | *Prefixed Name* | *Occurs* | *Read-only* | *Value-type* | *Representation* | *Range* | *Description* |");
						log("Got "+line);
						//System.exit(1);
						throw new Exception("Invalid property table header - Expected | *Prefixed Name* | *Occurs* | *Read-only* | *Value-type* | *Representation* | *Range* | *Description* |");
					}
					break;
				}
				else if(!this.curateToken(line.trim()).startsWith("*"))
				{
					introText = this.curateToken(line.trim());
				}
	
				line = reader.readLine();
			}
			if((shapeInfo.title == null || shapeInfo.title.trim().length()==0) && introText != null)
			{
				shapeInfo.title = introText;
			}
			if((shapeInfo.describes == null || shapeInfo.describes.trim().length()==0) && shapeInfo.nsURI != null && shapeInfo.localName != null)
			{
				if(shapeInfo.prefix != null)
				{
					shapeInfo.describes = shapeInfo.prefix+":"+shapeInfo.localName;
					nsPrefixMap.put(shapeInfo.nsURI, shapeInfo.prefix);
				}
				else
				{
					shapeInfo.describes = nsPrefixMap.get(shapeInfo.nsURI)+":"+shapeInfo.localName;
				}
			}
			Section currentSection = null;
			line = reader.readLine();
			while(line != null)
			{
				if(line.trim().length()==0 || this.isTableSeparator(line) || this.isEmptyLine(line))
				{
					line = reader.readLine();
					continue;
				}
				Section wikiSection = this.processSectionLine(line);
				if(wikiSection != null)
				{
					currentSection = wikiSection;
					shapeInfo.sections.add(currentSection);
				}
				else
				{
					StringTokenizer strtk = new StringTokenizer(line,"|");
					PropertyInfo prop = new PropertyInfo();
					if(currentSection == null)
					{
						currentSection = new Section();
						shapeInfo.sections.add(currentSection);
						currentSection.header = "Properties";
					}
					currentSection.props.add(prop);
					//	TODO: Added before all properties filled in - if columns omitted, will that cause downstream exceptions?  Maybe fill in some obvious "bad" values as defaults?
					if(strtk.hasMoreTokens())//prefixed name
					{
						String prefixedName = this.curateToken(strtk.nextToken().trim());
						if(prefixedName.indexOf('[')>=0 && prefixedName.indexOf(']')>0)
						{
							String[] tokens = this.parseURINotationText(prefixedName);
							this.handlePrefixedName(prop, tokens[0], tokens[1], prefixNsMap, nsPrefixMap);
						}
						else
						{
							this.handlePrefixedName(prop, prefixedName, null, prefixNsMap, nsPrefixMap);
						}
					}
					if(strtk.hasMoreTokens())//occurs
					{
						String occurs = this.curateToken(strtk.nextToken().trim());
						if(occurs.indexOf('[')>=0 && occurs.indexOf(']')>0)
						{
							String[] tokens = this.parseURINotationText(occurs);
							this.handleOccurs(prop, tokens[0], tokens[1], prefixNsMap, nsPrefixMap);
						}
						else
						{
							this.handleOccurs(prop, occurs, null, prefixNsMap, nsPrefixMap);
						}
					}
					if(strtk.hasMoreTokens())//readOnly
					{
						String readOnly = this.curateToken(strtk.nextToken().trim());
						
						if(readOnly.equalsIgnoreCase("true") || readOnly.equalsIgnoreCase("1"))
						{
							prop.readOnly = "true";
						}
						else if(readOnly.equalsIgnoreCase("false") || readOnly.equalsIgnoreCase("0"))
						{
							prop.readOnly = "false";
						}
						else prop.readOnly = null;
					}
					if(strtk.hasMoreTokens())//valueType
					{
						String valueType = this.curateToken(strtk.nextToken().trim());
						if(valueType.indexOf('[')>=0 && valueType.indexOf(']')>0)
						{
							String[] tokens = this.parseURINotationText(valueType);
							this.handleValueType(prop, tokens[0], tokens[1], prefixNsMap, nsPrefixMap);
						}
						else
						{
							this.handleValueType(prop, valueType, null, prefixNsMap, nsPrefixMap);
						}
					}
					if(strtk.hasMoreTokens())//representation
					{
						String representation = this.curateToken(strtk.nextToken().trim());
						
						if(representation.indexOf('[')>=0 && representation.indexOf(']')>0)
						{
							String[] tokens = this.parseURINotationText(representation);
							this.handleRepresentation(prop, tokens[0], tokens[1], prefixNsMap, nsPrefixMap);
						}
						else
						{
							this.handleRepresentation(prop, representation, null, prefixNsMap, nsPrefixMap);
						}
					}
					if(strtk.hasMoreTokens())//range
					{
						String range = this.curateToken(strtk.nextToken().trim());
						if(range.indexOf('[')>=0 && range.indexOf(']')>0)
						{
							String[] tokens = this.parseURINotationText(range);
							this.handleRange(prop, tokens[0], tokens[1], prefixNsMap, nsPrefixMap);
						}
						else
						{
							this.handleRange(prop, range, null, prefixNsMap, nsPrefixMap);
						}
					}
					if(strtk.hasMoreTokens())//description
					{
						String description = strtk.nextToken().trim();
						prop.description = description;
					}
				}
				line = reader.readLine();//while loop
			}
			this.writePrefixMap(pw, nsPrefixMap, modelBaseURI);
	
			this.writeShapeInfo(pw, shapeInfo, prefixNsMap, nsPrefixMap);
			for(Section s : shapeInfo.sections)
			{
				
				for(PropertyInfo p : s.props)
				{
					this.writePropertyInfo(pw, shapeInfo, p, prefixNsMap, nsPrefixMap);
				}
			}
			this.writeSectionInfo(pw, shapeInfo, prefixNsMap, nsPrefixMap);
	
			pw.flush();
			
			log("Finished creating " + outAbsoluteFileName );
			System.out.println();
		}
		finally
		{
			if(reader != null) reader.close();
			if(pw != null) pw.close();
		}
		
	}
	
	private void validateGeneratedShape()
	{
		log("Reading output file "+outAbsoluteFileName+" in as Turtle to check generated file's syntax" );

		try {
			ReadFileIntoNewModel.read(outAbsoluteFileName,"TURTLE");
			log("File is syntactically correct");
		} catch (Exception e) {
			e.printStackTrace();
			log(e.getLocalizedMessage());
		}

	}
	
	private String curateToken(String token)
	{
		return token.replaceAll("[\"'`=]", "");
	}
	
	private void handlePrefixedName(PropertyInfo prop, String prefixedName, String uri, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		StringTokenizer strtk2 = new StringTokenizer(prefixedName,":");
		String prefix = strtk2.nextToken();
		String nsURI = prefixNsMap.get(prefix);
		
		if(nsURI == null)
		{
			nsURI = this.findNSForMissingPrefix(prefix, nsPrefixMap);
			if(nsURI != null)
			{
				prefixNsMap.put(prefix, nsURI);
			}
		}
		
		String ncName = strtk2.nextToken();
		prop.name = ncName;
		prop.title = prefixedName;
		prop.propertyDefinition = prefixedName;
		prop.nsURI = nsURI;
	}
	
	private void handleOccurs(PropertyInfo prop, String occurs, String uri, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		String oslcPrefix = nsPrefixMap.get(OSLC.NS);

		if(this.isExactlyOne(occurs))
		{
			occurs = oslcPrefix+":Exactly-one";
		}
		else if(this.isOneOrMany(occurs))
		{
			occurs = oslcPrefix+":One-or-many";
		}
		else if(this.isZeroOrOne(occurs))
		{
			occurs = oslcPrefix+":Zero-or-one";
		}
		else if(this.isZeroOrMany(occurs))
		{
			occurs = oslcPrefix+":Zero-or-many";
		}
		else
		{
			log("Error!Occurs could not be determined****");
		}
		prop.occurs = occurs;

	}
	
	private String[] parseURINotationText(String text)
	{
		String[] parts = text.split("[\\])(\\[]");//"5/31/1948@14:57".split("[/@:]");
		List<String> tokens = new ArrayList<String>();
		for(String s : parts)
			if(s != null && s.trim().length()>0)
			{
				tokens.add(s.replaceAll("[\"'`]", ""));
			}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	private String guessPrefixFromNS(String nsURI)
	{
		if(nsURI.endsWith("#") || nsURI.endsWith("/"))
		{
			nsURI = nsURI.substring(0, nsURI.length()-1);
		}
		int lastIndx = nsURI.lastIndexOf('/');
		return nsURI.substring(lastIndx+1);
	}
	
	private String getLocalPart(String uri)
	{
		return (new PropertyImpl(uri)).getLocalName();
	}
	
	private String getNSPart(String uri)
	{
		return (new PropertyImpl(uri)).getNameSpace();
	}

	
	private boolean isTableSeparator(String line)
	{
		if(line == null || line.trim().length()==0 || !line.trim().startsWith("|")) return false;
		StringTokenizer strtk = new StringTokenizer(line,"|");
		if(strtk.countTokens()<=1) return false;
		while(strtk.hasMoreTokens())
		{
			if(!Pattern.matches("-*", strtk.nextToken().trim())) return false;
		}
		return true;
	}
	
	private boolean isTableHeader(String line)
	{
		if(line == null || line.trim().length()==0 || !line.trim().startsWith("|")) return false;
		StringTokenizer strtk = new StringTokenizer(line,"|");
		if(strtk.countTokens()<=1) return false;
		String lineH = line.toLowerCase().trim();
		return (lineH.indexOf("*range*")>=0 || lineH.indexOf("*prefixed name*")>0 || lineH.indexOf("*occurs*")>0 || lineH.indexOf("*value-type*")>0);
	}

	
	private boolean isEmptyLine(String line)
	{
		return (line.trim().length()==0 || !line.trim().startsWith("|"));//empty line
	}
	
	private Section processSectionLine(String line)
	{
		if(isEmptyLine(line)) return null;//empty line
		Section section = new Section();
		StringTokenizer strtk = new StringTokenizer(line,"|");
		if(strtk.countTokens()>1)
		{
			int countValidTokens = 0;//not null tokens - for a section line - there can be at most 1 not null token
			while(strtk.hasMoreTokens())
			{
				String checkBlankToken = strtk.nextToken();
				
				if(checkBlankToken != null && checkBlankToken.trim().length()>0)
				{
					++countValidTokens;
				}
				else
				{
					if(section.header != null)
					{
						section.header = checkBlankToken;
					}
				}
			}
			
			return countValidTokens>1?null:section;
		}
		else if(strtk.countTokens()==1)
		{
			section.header = strtk.nextToken();
			return section;
		}
		else
		{
			return null;
		}
	}
	
	
	private void handleRange(PropertyInfo prop, String range, String uri, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		if(range.toLowerCase().indexOf("any") >=0)
		{
			prop.range = nsPrefixMap.get("http://open-services.net/ns/core#")+":Any";
		}
		else if(range.toLowerCase().equals("n/a") || range.toLowerCase().equals("na") )
		{
			prop.range = null;
		}
		else
		{
			if(!fixedRangeProps.contains(prop.nsURI+prop.name))
			{
				prop.range = nsPrefixMap.get("http://open-services.net/ns/core#")+":Any";
				StringTokenizer strk = new StringTokenizer(range,":");
				String prefix = strk.nextToken();
				//String ncName = strk.nextToken();
				if(!prefixNsMap.containsKey(prefix))
				{
					System.err.println("prefix "+prefix+" for "+range+"not bound to a namespace");
				}
				prop.suggestedRange = range;
			}
			else
			{
				prop.range = range;
			}
		}
	}
	
	private void handleRepresentation(PropertyInfo prop, String representation, String uri, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		String oslcPrefix = nsPrefixMap.get(OSLC.NS);
		if(representation.toLowerCase().indexOf("reference") >=0)
		{
			prop.representation = oslcPrefix+":Reference";
		}
		else if(representation.toLowerCase().indexOf("inline") >=0)
		{
			prop.representation = oslcPrefix+":Inline";
		}
		else if(representation.toLowerCase().indexOf("either") >=0 || representation.toLowerCase().indexOf("any") >=0)
		{
			prop.representation = oslcPrefix+":Either";
		}
		else if(representation.toLowerCase().indexOf("ref") >=0)
		{
			prop.representation = oslcPrefix+":Reference";
		}
	}
	
	private void handleValueType(PropertyInfo prop, String valueType, String uri, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		String xsdPrefix = nsPrefixMap.get(XSD.getURI());
		String oslcPrefix = nsPrefixMap.get(OSLC.NS);
		if(valueType.toLowerCase().indexOf("string") >=0)
		{
			prop.valueType = xsdPrefix+":string";
		}
		else if(valueType.toLowerCase().indexOf("xmlliteral") >=0)
		{
			prop.valueType = xsdPrefix+":XMLLiteral";
		}
		else if(valueType.toLowerCase().indexOf("int") >=0)
		{
			prop.valueType = xsdPrefix+":integer";
		}
		else if(valueType.toLowerCase().indexOf("float") >=0)
		{
			prop.valueType = xsdPrefix+":float";
		}
		else if(valueType.toLowerCase().indexOf("double") >=0)
		{
			prop.valueType = xsdPrefix+":double";
		}
		else if(valueType.toLowerCase().indexOf("decimal") >=0)
		{
			prop.valueType = xsdPrefix+":decimal";
		}
		else if(valueType.toLowerCase().indexOf("bool") >=0)
		{
			prop.valueType = xsdPrefix+":boolean";
		}
		else if(valueType.toLowerCase().indexOf("date") >=0 || valueType.toLowerCase().indexOf("time") >=0)
		{
			prop.valueType = xsdPrefix+":dateTime";
		}
		else if(valueType.toLowerCase().indexOf("localresource") >=0)
		{
			prop.valueType = oslcPrefix+":LocalResource";
		}
		else if(valueType.toLowerCase().indexOf("resource") >=0)
		{
			prop.valueType = oslcPrefix+":Resource";
		}
		else if(valueType.toLowerCase().indexOf("either") >=0 || valueType.toLowerCase().indexOf("any")>=0)
		{
			prop.valueType = oslcPrefix+":AnyResource";
		}
	}
	
	private boolean isZeroOrOne(String occurs)
	{
		if (occurs.toLowerCase().indexOf("zero")>=0 && occurs.toLowerCase().indexOf("one")>0) return true;
		if (occurs.equalsIgnoreCase("0:1")) return true;
		if (occurs.equalsIgnoreCase("0-1")) return true;
		return false;
	}
	
	private boolean isZeroOrMany(String occurs)
	{
		if (occurs.toLowerCase().indexOf("zero")>=0 && occurs.toLowerCase().indexOf("many")>0) return true;
		if (occurs.equalsIgnoreCase("0:*")) return true;
		if (occurs.equalsIgnoreCase("0-*")) return true;
		return false;
	}

	private boolean isOneOrMany(String occurs)
	{
		if (occurs.toLowerCase().indexOf("one")>=0 && occurs.toLowerCase().indexOf("many")>0) return true;
		if (occurs.equalsIgnoreCase("1:*")) return true;
		if (occurs.equalsIgnoreCase("1-*")) return true;
		return false;
	}
	
	private boolean isExactlyOne(String occurs)
	{
		if (occurs.toLowerCase().indexOf("exactly")>=0 && occurs.toLowerCase().indexOf("one")>0) return true;
		if (occurs.equalsIgnoreCase("1:1")) return true;
		if (occurs.equalsIgnoreCase("1-1")) return true;
		return false;
	}


	private void writePrefixMap(PrintWriter pw, Map<String,String> nsPrefixMap, String baseURI)
	{
		Set<String> keys = nsPrefixMap.keySet();
		int longestPrefix = 0;
		for(String k : keys) {
			longestPrefix = Math.max(longestPrefix, nsPrefixMap.get(k).length());
		}
		String pad = ":" + new String(new char[longestPrefix+1]).replace("\0", " ");
		String paddedPrefix = "";
		Set<Map.Entry<String,String>> set = nsPrefixMap.entrySet();
		for(Map.Entry<String,String> entry : set)
		{
			paddedPrefix = (entry.getValue() + pad).substring(0, longestPrefix+2 );	// +1 for colon, +1 for space
			pw.println("@prefix "+paddedPrefix+"<"+entry.getKey()+"> .");
		}
		pw.println();
		baseURI = baseURI==null?"http://open-services.net/wiki#":baseURI;
		pw.println("@base <"+baseURI+"> .");
		pw.println();
	}
	
	private void writeShapeInfo(PrintWriter pw, ShapeInfo shapeInfo, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		String describes = shapeInfo.describes;
		StringTokenizer strDescr = new StringTokenizer(describes,":");
		String describesName = strDescr.nextToken();
		shapeInfo.localName = describesName;
		pw.println("<>");
		String predicate = "a";
		pw.println(this.prettyfy(predicate)+nsPrefixMap.get("http://open-services.net/ns/core#")+":ResourceShape ;");
		predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":describes";
		pw.println(this.prettyfy(predicate)+describes+" ;");
		predicate = nsPrefixMap.get("http://purl.org/dc/terms/")+":title";
		pw.println(this.prettyfy(predicate)+"\""+shapeInfo.title+"\" ;");

		for(Section s: shapeInfo.sections)
		{
			for(PropertyInfo p : s.props)
			{
				predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":property";
				pw.println(this.prettyfy(predicate)+"<"+"#"+p.name+"> ;");
			}
		}
		pw.println(".");
		pw.println();
	}
	
	private void writeSectionInfo(PrintWriter pw, ShapeInfo shapeInfo, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		pw.println("<#_sections_>");
		String predicate = "a";
		pw.println(this.prettyfy(predicate)+nsPrefixMap.get("http://www.w3.org/2000/01/rdf-schema#")+":Seq ;");
		int i = 1;
		for(Section s: shapeInfo.sections)
		{
			predicate = nsPrefixMap.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#")+":_"+i;
			pw.println(this.prettyfy(predicate)+"<#_section_"+i+"> ;");
			i++;
		}
		pw.println(".");
		pw.println();
		int j = 1;
		for(Section s: shapeInfo.sections)
		{
			pw.println("<#_section_"+j+">");
			predicate = "a";
			pw.println(this.prettyfy(predicate)+nsPrefixMap.get("http://www.w3.org/2000/01/rdf-schema#")+":Seq ;");
			predicate = nsPrefixMap.get(DCTerms.NS)+":title";
			pw.println(this.prettyfy(predicate)+"\""+s.header.trim()+"\" ;");

			int k=1;
			for(PropertyInfo prop : s.props)
			{
				predicate = nsPrefixMap.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#")+":_"+k;
				pw.println(this.prettyfy(predicate)+"<#"+prop.name+"> ;");
				k++;
			}
			j++;
			pw.println(".");
			pw.println();
		}
	}
	
	private void writePropertyInfo(PrintWriter pw, ShapeInfo shapeInfo, PropertyInfo propInfo, Map<String,String> prefixNsMap, Map<String,String> nsPrefixMap)
	{
		String name = propInfo.name;
		pw.println("<#"+name+">");
		String predicate = "a";
		pw.println(this.prettyfy(predicate)+nsPrefixMap.get("http://open-services.net/ns/core#")+":Property ;");
		predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":name";
		pw.println(this.prettyfy(predicate)+"\""+name+"\" ;");
		predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":propertyDefinition";
		pw.println(this.prettyfy(predicate)+propInfo.propertyDefinition+" ;");
		predicate = nsPrefixMap.get("http://purl.org/dc/terms/")+":title";
		pw.println(this.prettyfy(predicate)+"\""+propInfo.title+"\" ;");
		predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":occurs";
		pw.println(this.prettyfy(predicate)+propInfo.occurs+" ;");
		if(propInfo.readOnly != null)
		{
			predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":readOnly";
			pw.println(this.prettyfy(predicate)+propInfo.readOnly+" ;");
		}
		if(propInfo.valueType != null)
		{
			predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":valueType";
			pw.println(this.prettyfy(predicate)+propInfo.valueType+" ;");
		}
		if(propInfo.range != null)
		{
			predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":range";
			pw.println(this.prettyfy(predicate)+propInfo.range+" ;");
		}
		if(propInfo.suggestedRange != null)
		{
			predicate = nsPrefixMap.get(NS_OSLC_AUTHORING)+":rangeSuggestion";
			pw.println(this.prettyfy(predicate)+propInfo.suggestedRange+" ;");
		}
		if(propInfo.representation != null)
		{
			predicate = nsPrefixMap.get("http://open-services.net/ns/core#")+":representation";
			pw.println(this.prettyfy(predicate)+propInfo.representation+" ;");
		}

		if(propInfo.description != null && propInfo.description.trim().length()>0)
		{
			predicate = nsPrefixMap.get("http://purl.org/dc/terms/")+":description";
			pw.println(this.prettyfy(predicate)+"\""+propInfo.description+"\" ;");
		}

		pw.println(".");
		pw.println();

	}
	
	private String prettyfy(String predicate)
	{
		char[] chars = new char[31-predicate.length()];
		Arrays.fill(chars, ' ');

		return space + predicate + (new String(chars));
	}

	
	private static class PropertyInfo
	{
		String name;
		String title;
		String description;
		String occurs;
		String readOnly;
		String valueType;
		String representation;
		String range;
		String suggestedRange;
		String propertyDefinition;
		String nsURI;
	}
	
	private static class Section
	{
		String header;
		List<PropertyInfo> props = new ArrayList<PropertyInfo>();
	}
	
	private static class ShapeInfo
	{
		String title;
		String describes;
		String localName;
		String prefix;
		String nsURI;
		List<Section> sections = new ArrayList<Section>();
	}
	
}
