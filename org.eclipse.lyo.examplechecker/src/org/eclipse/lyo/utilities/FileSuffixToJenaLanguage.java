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
