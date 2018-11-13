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

public class CheckRequiredArgs {

	/**
	 * @param args
	 */
	public static void exactlyN(String[] args, int n) {
		if (args.length < n) {
			OSLCToolLogger.error("Required arguments missing");
			System.exit(1);
		}
		if (args.length > n) {
			OSLCToolLogger.error("Extra arguments present");
			System.exit(1);
		}

	}

}
