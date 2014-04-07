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
package org.eclipse.lyo.utilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FilenameParser {
	File f = null;
	String root = null;
	String suffix = null;
	String pqn = null;		//	Path-qualified name
	String filename = null;

	public FilenameParser(String infilename) {
		filename = infilename;
		try {
			new URL(infilename);
			pqn = infilename;
		} catch( MalformedURLException urle) {			
			f = new File(infilename);
			pqn = f.getAbsolutePath();
		}
		int lastPeriod = pqn.lastIndexOf('.');
		if ( lastPeriod == -1 ) {	// No file extension in name
			root = pqn;
			suffix = "";
		}
		else if ( lastPeriod == 0 ) {	// Only file extension
			root = "";
			suffix = pqn;
		}
		else if ( lastPeriod == pqn.length()-1 ) {	// Ends with .
			root = pqn;
			suffix = "";
		}
		else {	// Non empty string before and after .
			root = pqn.substring(0, lastPeriod);
			suffix = pqn.substring(lastPeriod+1);
		}
	}
	public String getRoot() {
		return root;
		}
	public String getSuffix() {
		return suffix;
	}
	public String getFullyQualifiedName() {
		return pqn;
	}
	public boolean isDirectory() {
		if (f != null) {
			return f.isDirectory();
		}
		else {
			return false;
		}
	}
	public String[] getFileNames() {
		List<String> results = new ArrayList<String>();
		File[] files = (new File(filename)).getParentFile().listFiles();
		for (File file : files) {
		    if (file.isFile() && file.canRead() ) {
		        try {
					results.add(file.getCanonicalPath());
				} catch (IOException e) {
					System.err.println("Unable to get canonical path for file " + file.getName() + ", in directory " + filename);
					System.err.println(e.getLocalizedMessage());
				}
		    }
		}
		return( (String[]) results.toArray(new String[results.size()]) );
	}
}
