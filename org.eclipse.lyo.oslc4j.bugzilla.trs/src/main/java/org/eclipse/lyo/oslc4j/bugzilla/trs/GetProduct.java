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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.j2bugzilla.base.BugzillaMethod;

/**
 *
 */
public class GetProduct implements BugzillaMethod {
	/**
	 * The method name for this {@link BugzillaMethod}
	 */
	private static final String GET_PRPDUCT = "Product.get"; //$NON-NLS-1$
	
	private Map<Object, Object> hash = new HashMap<Object, Object>();
	private Map<Object, Object> params = new HashMap<Object, Object>();
//	private String productName;
//	private Integer[] ids;
	private int[] ids;

//	public GetProduct(String productName) {
//		this.productName = productName;
//		params.put("names", productName);
//	}

	public GetProduct(Integer[] ids) {
		this.ids = new int[ids.length];
		int offset = 0;
		for(Integer id : ids ) {
			ids[offset] = id.intValue();
			offset++;
		}
		params.put("ids", ids);
	}
	
	/* (non-Javadoc)
	 * @see com.j2bugzilla.base.BugzillaMethod#setResultMap(java.util.Map)
	 */
	@Override
	public void setResultMap(Map<Object, Object> hash) {
		this.hash = hash;
	}

	/* (non-Javadoc)
	 * @see com.j2bugzilla.base.BugzillaMethod#getParameterMap()
	 */
	@Override
	public Map<Object, Object> getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

	/* (non-Javadoc)
	 * @see com.j2bugzilla.base.BugzillaMethod#getMethodName()
	 */
	@Override
	public String getMethodName() {
		return GET_PRPDUCT;
	}

	/**
	 * Return the corresponding Product ID
	 */
	public String getProductID(String productName) {
		if (hash.containsKey("products")) {//$NON-NLS-1$
			Object[] products = (Object[]) hash.get("products");//$NON-NLS-1$
			if (products != null) {
				for(Object product : products) {
					Map<String, Object> productMap =  (HashMap<String, Object>)product;
					if (productMap.containsKey("name")) {//$NON-NLS-1$
						String name = (String)productMap.get("name"); //$NON-NLS-1$
						if ((name != null) && (name.equals(productName))) {
							return ((Integer)productMap.get("id")).toString(); //$NON-NLS-1$
						}
					}
				}
			}
		}
		return "";//$NON-NLS-1$
	}
}
