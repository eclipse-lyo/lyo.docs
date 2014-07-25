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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NameSpacePrefix {
	public static String findNameSpacePrefix(Map<String, String> nspm, String uri) {
		for (Iterator<Entry<String, String>> nspi = nspm.entrySet().iterator();
				nspi.hasNext();) {
			Entry<String, String> nsp = (Entry<String, String>) nspi.next();
			String nsuri = nsp.getValue();
			if(nsuri.equals(uri)) {
				return(nsp.getKey()+ ":");
			}
		}
		return uri;
	}
}
