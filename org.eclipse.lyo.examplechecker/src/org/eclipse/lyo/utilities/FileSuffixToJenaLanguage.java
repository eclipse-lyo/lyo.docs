/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2013. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/
package org.eclipse.lyo.utilities;

import com.hp.hpl.jena.util.FileUtils;

public class FileSuffixToJenaLanguage {

	/**
	 * @param in_FileNameSuffix
	 */
	public static String toLang(String in_FileNameSuffix) {
		String inModelLanguage = null;
		if ( in_FileNameSuffix.equals("rdf")) inModelLanguage = FileUtils.langXML;
		else if ( in_FileNameSuffix.equals("xml")) inModelLanguage = FileUtils.langXML;
		else if ( in_FileNameSuffix.equals("rdfxml")) inModelLanguage = FileUtils.langXML;
		else if ( in_FileNameSuffix.equals("ttl")) inModelLanguage = FileUtils.langTurtle;
		else {
			OSLCToolLogger.error("Input model type:\t\t" + in_FileNameSuffix + " not recognized." +
					"  Assuming RDF/XML, but you will get parse errors if this is wrong.");
			inModelLanguage = FileUtils.langXML;
		}
		return inModelLanguage;
	}

}
