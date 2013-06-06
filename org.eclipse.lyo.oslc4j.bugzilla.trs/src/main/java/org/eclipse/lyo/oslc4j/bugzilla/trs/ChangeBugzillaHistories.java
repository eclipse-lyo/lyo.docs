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

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.ChangeLog;
import org.eclipse.lyo.core.trs.Creation;
import org.eclipse.lyo.core.trs.Modification;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.jbugzx.rpc.GetAccessibleProducts;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.BugSearch;
import com.j2bugzilla.rpc.GetProduct;

/**
 * This class represents the list of History data in Bugzilla
 */
public class ChangeBugzillaHistories {
	private static final int MAXNUMBEROFBUGS = 100;
	private static final int MAXNUMBEROFBUGS_LANDFILL = 3; // Support for Bugzilla hosted on Landfill
	private static final int MAXNUMBEROFPRODUCTS = 5;
	private static final int MAXNUMBEROFPRODUCTS_LANDFILL = 1;
	private static final SimpleDateFormat XSD_DATETIME_FORMAT;
	static {
		XSD_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//$NON-NLS-1$
		XSD_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));//$NON-NLS-1$
	}

	private ChangeBugzillaHistories() {
	}

	private static HistoryData[] getHistory(HttpServletRequest httpServletRequest, Date dayAfter) {
		// See
		// org.eclipse.lyo.oslc4j.bugzilla.servlet.ServiceProviderCatalogSingleton.initServiceProvidersFromProducts(HttpServletRequest)
		try {
			BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			List<String> productIds = new ArrayList<String>();
			if (bc != null) {
				// get product ids
				GetAccessibleProducts getProductIds = new GetAccessibleProducts();
				bc.executeMethod(getProductIds);
				Integer[] ids = getProductIds.getIds();
				boolean isLandfillHostedBugzilla = (BugzillaManager.getBugzillaUri().indexOf("demo.bugzilla.org") != -1); // Support for Bugzilla hosted on Landfill
				int maxNumberOfProducts = (!isLandfillHostedBugzilla) ? ChangeBugzillaHistories.MAXNUMBEROFPRODUCTS : ChangeBugzillaHistories.MAXNUMBEROFPRODUCTS_LANDFILL;
				int numberOfProduct = 0;
				for (Integer p : ids) {
					productIds.add(Integer.toString(p));
					numberOfProduct++;
					if (numberOfProduct >= maxNumberOfProducts) break; // Support for Bugzilla hosted on Landfill
				}

				// get basePath
				// String basePath = BugzillaManager.getBugzServiceBase(); // this is
				// http://<hostname>:<port>/OSLC4JBugzilla/services

				// get bugs from each product
				List<HistoryData> allhistories = new ArrayList<HistoryData>();
				for (String productid : productIds) {
					List<Bug> bugList = ChangeBugzillaHistories.getBugsByProduct(httpServletRequest, productid, /* page */0, 
									/* limit */!isLandfillHostedBugzilla ? ChangeBugzillaHistories.MAXNUMBEROFBUGS : ChangeBugzillaHistories.MAXNUMBEROFBUGS_LANDFILL, // Support for Bugzilla hosted on Landfill
									dayAfter);
					for (Bug bug : bugList) {
						Collections.addAll(allhistories, BugzillaManager.getBugHistoryById(httpServletRequest, bug, productid, Integer.toString(bug.getID()), dayAfter));
					}
				}
				Collections.sort(allhistories, new Comparator<HistoryData>() {
					public int compare(HistoryData object1, HistoryData object2) {
						return object1.getTimestamp().compareTo(object2.getTimestamp()) * -1; // reverse sort, so new -> old
					}
				});
				HistoryData[] result = new HistoryData[allhistories.size()];
				allhistories.toArray(result);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	/*
	 * Retrieve the old implementation
	 */
	/**
	 * Create a list of Bugs for a product ID using paging
	 * 
	 * @param httpServletRequest
	 * @param productIdString
	 * @param page
	 * @param limit
	 * @return The list of bugs, paged if necessary
	 * @throws IOException
	 * @throws ServletException
	 */
	private static List<Bug> getBugsByProduct(final HttpServletRequest httpServletRequest,
			final String productIdString, int page, int limit, Date dayAfter) throws IOException, ServletException {
		List<Bug> results = null;
		try {
			final BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);
			final String pageString = httpServletRequest.getParameter("page");//$NON-NLS-1$

			if (null != pageString) {
				page = Integer.parseInt(pageString);
			}
			int productId = Integer.parseInt(productIdString);
			final GetProduct getProducts = new GetProduct(productId);
			if (bc != null) {
				bc.executeMethod(getProducts);
				final Product product = getProducts.getProduct();
				final BugSearch bugSearch = ChangeBugzillaHistories.createBugSearch(page, limit, product, dayAfter);
				bc.executeMethod(bugSearch);
				results = bugSearch.getSearchResults();
			} else {
				System.err.println("Bugzilla Connector not initialized - check bugz.properties");//$NON-NLS-1$
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
		return results;
	}

	private static BugSearch createBugSearch(int page, int limit, Product product, Date dayAfter) {
		BugSearch.SearchQuery productQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.PRODUCT, product.getName());
		BugSearch.SearchQuery limitQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.LIMIT, (limit + 1) + "");//$NON-NLS-1$
		BugSearch.SearchQuery offsetQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.OFFSET, (page * limit) + "");//$NON-NLS-1$
		if (dayAfter == null) {
			return new BugSearch(productQuery, limitQuery, offsetQuery);
		}
		BugSearch2 search = new BugSearch2(productQuery, limitQuery, offsetQuery);
		search.setDayAfter(new Date(dayAfter.getTime()+1000)); // +1s : bugs dates > dayAfter
		return search;
	}

	/**
	 * Mutex
	 */
	private static String mutex = "";//$NON-NLS-1$
	/**
	 * List of Base Resources (pageNum, List<Base>)
	 */
	private static Map<String, Base> baseResouces;
	private static Date mostRecentChangeLogDate;
	private static Date lastBaseResourceUpdatedDate;
	private static long UPDATEINTERVAL;
	static {
		Properties props = new Properties();
    try {
      props.load(BugzillaManager.class.getResourceAsStream("/bugz.properties"));//$NON-NLS-1$
      String interval = props.getProperty("rebuild_interval");//$NON-NLS-1$
      if ((interval != null) && (interval.length() != 0)) {
      	UPDATEINTERVAL = Integer.valueOf(interval).longValue();
      }
      if (UPDATEINTERVAL == 0) {
      	UPDATEINTERVAL = 5 * 60 * 1000; // 5 min = 300s = 300000 milliseconds
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	/**
	 * List of Change Logs
	 */
	private static Map<String, ChangeLog> changeLogs;
	/**
	 * Saved Bugzilla History
	 */
	private static HistoryData[] prevBugHistories;
	/**
	 * Page Limit for Base Resources
	 */
	private static final int BASE_PAGELIMIT = 3;
	/**
	 * Page Limit for ChangeLogs
	 */
	private static final int CHANGELOGS_PAGELIMIT = 3;

	/**
	 * Build Bugzilla ChangeLogs diff after mostRecentChangeData
	 */
	private static HistoryData[] buildChangeLogsDiff(HttpServletRequest httpServletRequest) {
		return ChangeBugzillaHistories.getHistory(httpServletRequest, mostRecentChangeLogDate);
	}

	/**
	 * Build Bugzilla Base resources and ChangeLogs
	 */
	private static void buildBaseResourcesAndChangeLogsInternal(HttpServletRequest httpServletRequest) {
		Date nowDate = new Date();
		if ((lastBaseResourceUpdatedDate != null) && 
				(nowDate.getTime() - lastBaseResourceUpdatedDate.getTime() > UPDATEINTERVAL)) {
			mostRecentChangeLogDate = null; // enforce to build all
		}
		boolean buildAll = ((mostRecentChangeLogDate == null) || (baseResouces == null));
		
		HistoryData[] updatedHistories = null;
		if (!buildAll) {
			// Get only updated Histories
			updatedHistories = ChangeBugzillaHistories.buildChangeLogsDiff(httpServletRequest);
			if ((updatedHistories == null) || (updatedHistories.length == 0)) {
				return;
			}
		} else {
			baseResouces = null;
			mostRecentChangeLogDate = null;
			lastBaseResourceUpdatedDate = nowDate;
		}

		changeLogs = null;

		int basePagenum = 1;
		int changeLogPageNum = 1;

		URI nilURI = URI.create(TRSConstants.RDF_NIL);

		// even if no change logs, Base is necessary
		Base base = null;
		Base prevBase = null;
		if (buildAll) {
			base = new Base();
			base.setAbout(URI.create(BugzillaManager.getBugzServiceBase()
					+ "/trs/" + TRSConstants.TRS_TERM_BASE));//$NON-NLS-1$
			base.setNextPage(nilURI);
			baseResouces = new HashMap<String, Base>();
			baseResouces.put(String.valueOf(basePagenum), base);
		}
			
		// Try to get Bugzilla histories now.
		HistoryData[] histories = null;
		if (buildAll) {
			histories = ChangeBugzillaHistories.getHistory(httpServletRequest, null);
			if (histories.length == 0) {
				return;
			}
		} else {
			int newSize = prevBugHistories != null ? prevBugHistories.length : 0;
			newSize += updatedHistories != null ? updatedHistories.length : 0;
			if (newSize == 0) {
				return;
			}
			histories = new HistoryData[newSize];
			int start = 0;
			if ((updatedHistories != null) && (updatedHistories.length > 0)) {
				System.arraycopy(updatedHistories, 0, histories, start, updatedHistories.length);
				start = updatedHistories.length;
			}
			if ((prevBugHistories != null) && (prevBugHistories.length > 0)) {
				System.arraycopy(prevBugHistories, 0, histories, start, prevBugHistories.length);
				start = prevBugHistories.length;
			}
		}
		prevBugHistories = new HistoryData[histories.length];
		System.arraycopy(histories, 0, prevBugHistories, 0, histories.length);

		ChangeLog changeLog = null;
		ChangeLog prevChangeLog = null;

		int changeOrder = histories.length;
		int currentNumberOfMember = 0;
		int currentNumberOfChangeLog = 0;
		URI mostRecentChangeEventURI = null;
		List<URI> allmembers = buildAll ? new ArrayList<URI>(histories.length) : null;
		for (HistoryData historyData : histories) {
			URI uri = historyData.getUri();
			String changedUriTemplate = "urn:urn-3:" + //$NON-NLS-1$
					"cm1.example.com" + //$NON-NLS-1$
					":" + //$NON-NLS-1$
					XSD_DATETIME_FORMAT.format(historyData.getTimestamp()) + ":" + //$NON-NLS-1$
					changeOrder;
			URI changedUri = URI.create(changedUriTemplate);
			ChangeEvent ce = historyData.getType() == HistoryData.CREATED ? 
					new Creation(changedUri, uri, changeOrder) : new Modification(changedUri, uri, changeOrder);
			if (mostRecentChangeEventURI == null) {
				mostRecentChangeEventURI = changedUri;
				mostRecentChangeLogDate = historyData.getTimestamp();
			}

			// ChangeLog Page
			if (changeLog == null) {
				URI prevPage;
				if (changeLogs == null) {
					prevPage = URI.create(BugzillaManager.getBugzServiceBase()
							+ "/trs/" + TRSConstants.TRS_TERM_CHANGE_LOG);//$NON-NLS-1$
				} else {
					prevPage = URI
							.create(BugzillaManager.getBugzServiceBase()
									+ "/trs/" + TRSConstants.TRS_TERM_CHANGE_LOG + "/" + String.valueOf(changeLogPageNum + 1));//$NON-NLS-1$ //$NON-NLS-2$
				}
				if (prevChangeLog != null) {
					prevChangeLog.setPrevious(prevPage);
					prevChangeLog = null;
				}
				if (changeLogs != null) {
					changeLogPageNum++;
				}
				changeLog = new ChangeLog();
				changeLog.setAbout(prevPage);
				changeLog.setPrevious(nilURI);
				if (changeLogs == null) {
					changeLogs = new HashMap<String, ChangeLog>();
				}
				changeLogs.put(String.valueOf(changeLogPageNum), changeLog);
			}

			changeLog.getChanges().add(ce);
			currentNumberOfChangeLog++;

			if (buildAll && !allmembers.contains(uri)) {
				// Base Page
				if (base == null) {
					URI nextPage = URI.create(BugzillaManager.getBugzServiceBase()
									+ "/trs/" + TRSConstants.TRS_TERM_BASE + "/" + String.valueOf(basePagenum + 1));//$NON-NLS-1$ //$NON-NLS-2$
					if (prevBase != null) {
						prevBase.setNextPage(nextPage);
						prevBase = null;
					}
					basePagenum++;
					base = new Base();
					base.setAbout(URI.create(BugzillaManager.getBugzServiceBase()
							+ "/trs/" + TRSConstants.TRS_TERM_BASE));//$NON-NLS-1$
					base.setNextPage(nilURI);
					baseResouces.put(String.valueOf(basePagenum), base);
				}
				base.getMembers().add(uri);// rdfs:member is mandatory
				currentNumberOfMember++;
				allmembers.add(uri);
			}
			changeOrder--;

			// Base Page
			if ((buildAll) && (currentNumberOfMember >= BASE_PAGELIMIT)) {
				prevBase = base;
				base = null;
				currentNumberOfMember = 0;
			}
			// ChangeLog Page
			if (currentNumberOfChangeLog >= CHANGELOGS_PAGELIMIT) {
				prevChangeLog = changeLog;
				changeLog = null;
				currentNumberOfChangeLog = 0;
			}
		}
		// Base resource's CutoffEvent is most recent Change event.
		// Since the order of the returned HistoryData is "new -> old",
		// the first one is the most recent.
		if (buildAll) {
			((Base) baseResouces.get("1")).setCutoffEvent(mostRecentChangeEventURI);//$NON-NLS-1$
		}
	}

	public static void buildBaseResourcesAndChangeLogs(
			HttpServletRequest httpServletRequest) {
		synchronized (mutex) {
			ChangeBugzillaHistories.buildBaseResourcesAndChangeLogsInternal(httpServletRequest);
		}
	}

	/**
	 * Return pagenum's Base resource
	 * 
	 * @param pagenum
	 * @param httpServletRequest
	 * @return
	 */
	public static Base getBaseResource(String pagenum,
			HttpServletRequest httpServletRequest) {
		synchronized (mutex) {
			ChangeBugzillaHistories.buildBaseResourcesAndChangeLogsInternal(httpServletRequest);
			return baseResouces != null ? baseResouces.get(pagenum) : null;
		}
	}

	/**
	 * Return pagenum's ChangeLog
	 * 
	 * @param pagenum
	 * @param httpServletRequest
	 * @return
	 */
	public static ChangeLog getChangeLog(String pagenum,
			HttpServletRequest httpServletRequest) {
		synchronized (mutex) {
			ChangeBugzillaHistories.buildBaseResourcesAndChangeLogsInternal(httpServletRequest);
			// changeLogs might be null
			return changeLogs != null ? changeLogs.get(pagenum) : null;
		}
	}
}
