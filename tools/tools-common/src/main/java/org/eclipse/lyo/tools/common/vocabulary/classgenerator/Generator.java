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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.lyo.tools.common.util.CheckRequiredArgs;
import org.eclipse.lyo.tools.common.util.FileSuffixToJenaLanguage;
import org.eclipse.lyo.tools.common.util.FilenameParser;
import org.eclipse.lyo.tools.common.util.ReadFileIntoNewModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Generator {
	static String className = "YOUR CLASS NAME";
	static Model  m = null;
	static String nameSpace = null;
	private static final Logger logger = Logger.getLogger(Generator.class);

	static
	{
		BasicConfigurator.configure();
	}

	/**
	 *@param args input vocabulary file path or URL
	 */
	public static void main(String[] args) {
		CheckRequiredArgs.exactlyN(args, 1);
		String fileURL = args[0];
		parseFile(fileURL);
		if ( m == null ) {
			return;
		}

		try {
			new org.eclipse.lyo.tools.common.vocabulary.classgenerator.ClassFactory(className, m).dynamicClassCreation();
		}
		catch( Exception e)
		{
			logger.error("Class generation failed, error: " + e.getLocalizedMessage() );
		}
	}

	private static void parseFile(String fileURL) {
		// If the URL ends with x.rdf, e.g. http://open-services.net/ns/core/core.rdf
		// The name space is deduced from the URL to be http://open-services.net/ns/core#
		if (fileURL.endsWith("rdf")) {
			className = fileURL.substring(fileURL.lastIndexOf("/") +  1);
			className = className.substring(0, className.lastIndexOf("."));
		}
		// If the URL doesn't not end with x.rdf, e.g. http://www.w3.org/2000/01/rdf-schema#
		// The name space is the same as the URL http://www.w3.org/2000/01/rdf-schema#
		else {
			logger.error("Can't retrieve class name from the input fileURL: \"" + fileURL + "\", please make sure it ends with \".rdf\".");
			return;
		}

		FilenameParser fp = new FilenameParser(fileURL);
		String inFile = fp.getFullyQualifiedName();
		String in_FileNameSuffix = fp.getSuffix();
		retrieveVocabularyRdf(inFile, in_FileNameSuffix);
	}

	private static void retrieveVocabularyRdf(String inFile, String in_FileNameSuffix) {
		m = ModelFactory.createDefaultModel();
		String inModelLanguage = FileSuffixToJenaLanguage.toLang(in_FileNameSuffix);
		m = ReadFileIntoNewModel.read(inFile,inModelLanguage);
	}

}
