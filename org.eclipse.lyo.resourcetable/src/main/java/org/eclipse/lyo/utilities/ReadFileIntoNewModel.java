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
package org.eclipse.lyo.utilities;

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
				System.err.println("Invalid file name: " + infilename);
				System.err.println(localurle.getLocalizedMessage());
				return null;
			}
		}
		
		try {
			in = fileUrl.openStream();
		} catch( IOException e){
				System.err.println("Can't open file: " + infilename);
				System.err.println(e.getLocalizedMessage());
				return null;
		}
	
		try {
			m.read(in, null, inFormat);
		} catch (Exception e) {
			System.err.println("Can't read file: " + infilename);
			System.err.println(e.getLocalizedMessage());
		}
		
		return m;
	}

}
