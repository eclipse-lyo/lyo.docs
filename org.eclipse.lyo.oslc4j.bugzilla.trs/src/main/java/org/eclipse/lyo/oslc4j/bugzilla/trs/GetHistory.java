/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j2bugzilla.base.BugzillaMethod;

/**
 * 
 */
public class GetHistory implements BugzillaMethod {
	/**
	 * The method name for this {@link BugzillaMethod}
	 */
	private static final String GET_HISTORY = "Bug.history"; //$NON-NLS-1$

	private Map<Object, Object> hash = new HashMap<Object, Object>();
	private Map<Object, Object> params = new HashMap<Object, Object>();

	public GetHistory() {
	}

	public GetHistory(int id) {
		params.put("ids", id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.j2bugzilla.base.BugzillaMethod#setResultMap(java.util.Map)
	 */
	public void setResultMap(Map<Object, Object> hash) {
		this.hash = hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.j2bugzilla.base.BugzillaMethod#getParameterMap()
	 */
	public Map<Object, Object> getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.j2bugzilla.base.BugzillaMethod#getMethodName()
	 */
	public String getMethodName() {
		return GET_HISTORY;
	}

	/**
	 * Get the list of history
	 * 
	 * This history has only "changed" events.
	 */
	public Date[] getModifiedTimestamps() {
		List<Date> result = new ArrayList<Date>();

		if (hash.containsKey("bugs")) { //$NON-NLS-1$
			Object[] bugs = (Object[]) hash.get("bugs"); //$NON-NLS-1$
			if (bugs.length == 0) {
				return new Date[0]; // early return if map is empty
			}
			for (Object o : bugs) {
				@SuppressWarnings("unchecked")
				Map<String, Object> bugMap = (HashMap<String, Object>) o;
				if (bugMap.containsKey("history")) { //$NON-NLS-1$
					Object[] histories = (Object[]) bugMap.get("history"); //$NON-NLS-1$
					for (Object h : histories) {
						@SuppressWarnings("unchecked")
						Map<String, Object> historyMap = (Map<String, Object>) h;
						if (historyMap.containsKey("when")) { //$NON-NLS-1$
							result.add((Date) historyMap.get("when")); //$NON-NLS-1$
						}
					}
				}
			}
		}
		Date[] copyresult = new Date[result.size()];
		return result.toArray(copyresult);
	}
}
