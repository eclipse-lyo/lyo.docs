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
 * * Contributors:
 * 
 *    Hirotaka Matsumoto - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.oslc4j.bugzilla.trs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.j2bugzilla.base.BugzillaMethod;

public class UpdateURL implements BugzillaMethod {
	/**
	 * The XML-RPC method Bugzilla will use
	 */
	private static final String METHOD_NAME = "Bug.update";

	private Map<Object, Object> hash = new HashMap<Object, Object>();
	private Map<Object, Object> params = new HashMap<Object, Object>();

	public UpdateURL() {
		super();
	}

	public UpdateURL(int id, String url) {
		super();
		params.put("ids", id);
		params.put("url", url);
	}

	@Override
	public void setResultMap(Map<Object, Object> hash) {
		this.hash = hash;
		
	}

	@Override
	public Map<Object, Object> getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
	
	public int getID() {
		if (hash.containsKey("bugs")) {
			Object[] objs = (Object[])hash.get("bugs"); 
			Map<String, Object> maps = (Map<String, Object>)objs[0];
			Map<String, Map> changesMaps = (Map<String, Map>)maps.get("changes");
			if (changesMaps != null) {
				Map<String, String> changes = (Map<String, String>)changesMaps.get("url");
				if (changes != null) {
					String removed = (String)changes.get("removed");
					String added = (String)changes.get("added");
					if (added !=null) {
						int i = 0;
					}
				}
			}
			int id = ((Integer)maps.get("id")).intValue();
			return id;
		}
		return -1;
	}
}