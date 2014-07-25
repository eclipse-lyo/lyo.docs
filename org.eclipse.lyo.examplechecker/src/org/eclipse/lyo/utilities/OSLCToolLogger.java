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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class OSLCToolLogger {
	private static final Logger oSLCToolLogger = Logger.getLogger(OSLCToolLogger.class);
	private static PrintStream outputStream = null;

	public static void debug(String text) {
		if (outputStream != null ) {
			outputStream.println(text);
		}
		oSLCToolLogger.debug(text);
	}

	public static void info(String text) {
		if (outputStream != null ) {
			outputStream.println(text);
		}
		oSLCToolLogger.info(text);
	}

	public static void warn(String text) {
		if (outputStream != null ) {
			outputStream.println(text);
		}
		oSLCToolLogger.warn(text);
	}

	public static void error(String text) {
		if (outputStream != null ) {
			outputStream.println(text);
		}
		oSLCToolLogger.error(text);
	}

	public static void fatal(String text) {
		if (outputStream != null ) {
			outputStream.println(text);
		}
		oSLCToolLogger.fatal(text);
	}

	static
	{
		BasicConfigurator.configure();
	}

	public static void addLogOutput(PrintStream output) {
		outputStream = output;

	}

	public static void removeLogOutput() {
		outputStream = null;
	}

	public static void mergeErrorMsgs(Map<String, Set<String>> errorMsgs, Map<String, Set<String>> localMsgs) {
		// Merge the two message map and sort the error messages for each key
		for (Iterator<Entry<String, Set<String>>> errorMsg = localMsgs.entrySet().iterator();
				errorMsg.hasNext();) {
			Entry<String, Set<String>> emp = (Entry<String, Set<String>>) errorMsg.next();
			if (errorMsgs.containsKey(emp.getKey())){
				Set<String> tmp = errorMsgs.get(emp.getKey());
				tmp.addAll(emp.getValue());
				List<String> tmplist = new ArrayList<String>(tmp);
				Collections.sort(tmplist);
				Set<String> tmpset = new HashSet<String>(tmplist);
				errorMsgs.put(emp.getKey(), tmpset );
			}
			else {
				List<String> tmplist = new ArrayList<String>(emp.getValue());
				Collections.sort(tmplist);
				Set<String> tmpset = new HashSet<String>(tmplist);
				errorMsgs.put(emp.getKey(), tmpset);
			}
		}

	}

	public static void addNewErrorMsg(Map<String, Set<String>> errorMsgs, String key, Set<String> errorMsgList) {
		if (errorMsgs.containsKey(key)) {
			Set<String> tmp = errorMsgs.get(key);
			tmp.addAll(errorMsgList);
			errorMsgs.put(key, tmp);
		}
		else {
			errorMsgs.put(key, errorMsgList);
		}
	}
}
