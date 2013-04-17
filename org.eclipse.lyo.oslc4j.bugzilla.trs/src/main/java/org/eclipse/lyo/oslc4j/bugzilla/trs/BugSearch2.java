/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * * Contributors:
 * 
 *    Hirotaka Matsumoto - Initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.trs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.j2bugzilla.rpc.BugSearch;

/**
 * In addition to com.j2bugzilla.rpc.BugSearch, this class accepts "last_change_tim"
 * 
 */
public class BugSearch2 extends BugSearch {
	private Date dayAfter;

	public BugSearch2(SearchQuery... queries) {
		super(queries);
	}

	@Override
	public Map<Object, Object> getParameterMap() {
		Map<Object, Object> ret = new HashMap<Object, Object>(super.getParameterMap());
		if (dayAfter != null) {
			ret.put("last_change_time", dayAfter);//$NON-NLS-1$
		}
		return ret;
	}

	public void setDayAfter(Date dayAfter) {
		this.dayAfter = dayAfter;
	}
}
