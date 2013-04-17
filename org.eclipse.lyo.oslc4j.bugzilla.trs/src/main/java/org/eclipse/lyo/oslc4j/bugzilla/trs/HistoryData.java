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

import java.net.URI;
import java.util.Date;

/**
 * This class represents the one hisotry data in Bugzilla bug
 */
public class HistoryData {
  final public static String CREATED="Created"; // trs:Creation //$NON-NLS-1$
  final public static String MODIFIED="Modified"; // trs:Modification //$NON-NLS-1$

  private int productid; // Bugzilla product id
  private int id; // Bugzilla id
  private Date timestamp; // Time stamp;
  private URI uri; // OSLC link
  private String type = CREATED; // trs:Creation or trs:Modification
  
  private HistoryData() {
  }
  
  public static HistoryData getInstance(int productid, int id, Date timestamp, URI uri, String type) {
    HistoryData h = new HistoryData();
    h.productid = productid;
    h.id = id;
    h.timestamp = timestamp;
    h.uri = uri;
    h.type = type;
    return h;
  }

  /**
   * @return the productid
   */
  public int getProductid() {
    return productid;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @return the timestamp
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * @return the url
   */
  public URI getUri() {
    return uri;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "id="+Integer.toString(getId())+ ", "+//$NON-NLS-1$ //$NON-NLS-2$
	"ProductId="+Integer.toString(getProductid())+ ", "+//$NON-NLS-1$ //$NON-NLS-2$
	"Type=" + getType()+ ", "+//$NON-NLS-1$ //$NON-NLS-2$
	"Timestamp=" + getTimestamp()+ ", "+//$NON-NLS-1$ //$NON-NLS-2$
	"URI="+getUri()+"\n";//$NON-NLS-1$ //$NON-NLS-2$
  }

}
