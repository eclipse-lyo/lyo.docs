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

public class ClassifiedErrorMessage {
	private String priority = "";
	private String subject = "";
	private String message = "";
	public static final String PRIORITY_INFO = "";
	public static final String PRIORITY_WARNING = "Warning";
	public static final String PRIORITY_ERROR = "Error";

	public ClassifiedErrorMessage(String prio, String sub, String msg) {
		priority = prio;
		subject = sub;
		message = msg;
	}

	public String getPriority() {
		return priority;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessge() {
		return message;
	}

	@Override
	public String toString() {
		if (this.subject.length() > 0) {
			return(this.subject + "\t" + this.message);
		}
		else {
			return(this.message);
		}
	}
}
