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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class PotentialEnumCheck {

	private final static int CHECK_THRESHOLD = 2;

	public static ArrayList<ClassifiedErrorMessage> CheckPotentialEnum(Model example, Map<String, String> nspm) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		//example.write(System.out, "N-TRIPLE", null  );
		ResIterator subjects = example.listSubjects();
		String errorMsg = "";
		ArrayList<String> nsList = CommonPropertyCheck.getPublicNameSpaces();
		HashMap<String, Set<String>> potentialEnums = new HashMap<String, Set<String>>();

		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
			StmtIterator tmpStmts = tmpRes.listProperties();
			while ( tmpStmts.hasNext()) {
				Statement tmpStmt = tmpStmts.next();
				Property pred = tmpStmt.getPredicate();
				String nameSpace = pred.getNameSpace();
				String prefix = NameSpacePrefix.findNameSpacePrefix(nspm, nameSpace);
				String shortPred = pred.toString().replace(nameSpace, prefix);
				if (!nsList.contains(nameSpace)) {
					RDFNode objectNode = tmpStmt.getObject();
					if (objectNode.isLiteral()) {
						if (potentialEnums.containsKey(shortPred)) {
							Set<String> values = potentialEnums.get(shortPred);
							if (!values.contains(objectNode.toString())) {
								values.add(objectNode.toString());
							}
						} else {
							Set<String> values = new HashSet<String>();
							values.add(objectNode.toString());
							potentialEnums.put(shortPred, values);
						}
					}
				}
			}
		}
		// Check the contents of potentialEnums:
		// 1) Check for property under non-standard name space
		// 2) the property has > CHECK_THRESHOLD different string values.
		Iterator<Entry<String, Set<String>>> it = potentialEnums.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Set<String>> potentialValue = (Entry<String, Set<String>>) it.next();
			if (potentialValue.getValue().size() > CHECK_THRESHOLD) {
				List<String> sortedList = new ArrayList<String>();
				sortedList.addAll(potentialValue.getValue());
				Collections.sort(sortedList);
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR,
				potentialValue.getKey(), "\tWarning: suspicious String values " + sortedList +
					". Suggestion:use enumeration instead.");
				errorMsgList.add(errMsg);
				OSLCToolLogger.error(errMsg.toString());
			}
		}
		return errorMsgList;
	}
}

