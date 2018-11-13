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
package org.eclipse.lyo.examplechecker.junit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.lyo.tools.common.util.OSLCToolLogger;


public class Loader {
	InputStream inputStream = null;
	String expectedOutput = "";

	public Loader(String inFile, String OutFile) {
		inputStream = loadInputData(inFile);
		expectedOutput = loadExpectedOutputData(OutFile);
	}
	public InputStream getInputData() {
		return inputStream;
	}

	public String getOutputData() {
		return expectedOutput;
	}

	private InputStream loadInputData(String inFile) {
		InputStream in = this.getClass().getResourceAsStream(inFile);
		return in;
	}

	private String loadExpectedOutputData(String outFile) {
		InputStream in = this.getClass().getResourceAsStream(outFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String line;
		try {
		while ((line = reader.readLine()) != null) {
			out.append(line + "\n");
		}
		reader.close();
		} catch (Exception e) {
			OSLCToolLogger.error( e.getLocalizedMessage());
		}
		return out.toString();
	}
}
