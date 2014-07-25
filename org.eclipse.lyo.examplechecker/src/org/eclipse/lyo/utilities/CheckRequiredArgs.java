/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2013. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:  
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp. 
 *******************************************************************************/
package org.eclipse.lyo.utilities;

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
