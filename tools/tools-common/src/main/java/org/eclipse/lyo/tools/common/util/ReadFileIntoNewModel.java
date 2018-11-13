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
package org.eclipse.lyo.tools.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ReadFileIntoNewModel {

	/**
	 * @param infilename
	 * @param inFormat
	 */
	public static Model read(String infilename, String inFormat) {
		URL fileUrl = null;
		InputStream in = null;
		Model m = ModelFactory.createDefaultModel();

		try {
			fileUrl = new URL(infilename);
		} catch( MalformedURLException urle) {
			try {
			fileUrl = new File(infilename).toURI().toURL();
			} catch( MalformedURLException localurle)  {
				OSLCToolLogger.error("Invalid file name: " + infilename);
				OSLCToolLogger.error(localurle.getLocalizedMessage());
				return null;
			}
		}

		try {
			in = fileUrl.openStream();
		} catch( IOException e){
			OSLCToolLogger.error("Can't open file: " + infilename);
			OSLCToolLogger.error(e.getLocalizedMessage());
			return null;
		}

		try {
			m.read(in, null, inFormat);
		} catch (Exception e) {
			OSLCToolLogger.error("Can't read the file: " + infilename);
			OSLCToolLogger.error(e.getLocalizedMessage());
			return null;
		}

		return m;
	}

	public static Model read(InputStream in, String inFormat) {
		Model m = ModelFactory.createDefaultModel();

		try {
			m.read(in, null, inFormat);
		} catch (Exception e) {
			OSLCToolLogger.error("Can't read the input stream.");
			OSLCToolLogger.error(e.getLocalizedMessage());
			return null;
		}
		return m;
	}

}
