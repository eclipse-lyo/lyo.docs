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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class InvalidURILinkCheck {
	private static final int SUCCESS = 0; // valid url
	private static final int BAD_DOMAIN = 1; // URLName's domain is not accessible, e.g. https://198.51.100.194
	private static final int BAD_CONTENT_TYPE = 2; // URLName is accessible but the returned content-type is not rdf or turtle.
	private static final int BAD_URI = 3; // URLName is not accessible
	private static final ArrayList<String> uriWhiteList = new ArrayList<String> (
			Arrays.asList( new String[]{"http://www.w3.org/2001/XMLSchema#"}));	 //URI that is valid - but not retrievable
	private static ArrayList<String> hostsToSuppressList = null;
	private static final String IPADDRESS_PATTERN =
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

	public static ArrayList<ClassifiedErrorMessage> CheckURILink(Model example, Map<String, String> nspm, ArrayList<String> hosts) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		//example.write(System.out, "N-TRIPLE", null  );
		ResIterator subjects = example.listSubjects();
		hostsToSuppressList = hosts;
		String errorMsg = "";
		ArrayList<String> uriListtoCheck = new ArrayList<String>();
		HashMap<String, Integer> uriList = new HashMap<String, Integer>();
		Set<String> badURIs = new HashSet<String>();

		while ( subjects.hasNext() ) {
			Resource tmpRes = subjects.next();
			StmtIterator tmpStmts = tmpRes.listProperties();
			while ( tmpStmts.hasNext()) {
				Statement tmpStmt = tmpStmts.next();
				RDFNode objectNode = tmpStmt.getObject();
				if ( objectNode.isURIResource())
				{
					String urlStr = objectNode.toString();
					if (!uriListtoCheck.contains(urlStr)) {
						//int retCode = checkURIexists(urlStr, badURIs);
						uriListtoCheck.add(urlStr);
					}
				}

			}
		}

		OSLCToolLogger.info( "Checking " + uriListtoCheck.size() + " URIs, it may take a while...");
		for ( String i : uriListtoCheck ) {
			int retCode = checkURIexists(i, badURIs);
			uriList.put(i, retCode );
		}

		Map<String, Integer> UrisInTreemap = new TreeMap<String, Integer>(uriList);
		Iterator<Entry<String, Integer>> it = UrisInTreemap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Integer> item = (Entry<String,Integer>) it.next();
			if (item.getValue() == BAD_DOMAIN) {
				errorMsg = "Warning:\t\"" + item.getKey() + "\" is not accessible";
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_WARNING, "",
						errorMsg);
				errorMsgList.add(errMsg);
				OSLCToolLogger.error(errMsg.toString());
			}
			else if (item.getValue() == BAD_URI) {
				errorMsg = "Error:\t\t\"" + item.getKey() + "\" is not accessible via GET, or its content does not contain the URI as would be expected in a RDFS vocabulary document.";
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "",
						errorMsg);
				errorMsgList.add(errMsg);
				OSLCToolLogger.error(errMsg.toString());
			}
			else if (item.getValue() == BAD_CONTENT_TYPE) {
				errorMsg = "Warning:\t\"" + item.getKey() + "\" doesn't return a rdf/turtle document.";
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_WARNING, "",
						errorMsg);
				errorMsgList.add(errMsg);
				OSLCToolLogger.error(errMsg.toString());
			}
		}
		return errorMsgList;
	}

	private static boolean inURIBlackList(String domainStr, Set<String> badURIs) {
		String localhostStr = "localhost";
		String lowerUri = domainStr.toLowerCase();
		if (lowerUri.contains(localhostStr)) {
			badURIs.add(domainStr);
			return true;
		}
		else {
			Matcher matcher = pattern.matcher(domainStr);
			boolean found = matcher.find();
			if (found) {
				badURIs.add(domainStr);
				return true;
			}
		}
		return false;
	}
	// Return code
	// SUCCESS - success
	// BAD_DOMAIN - URLName's domain is not accessible, e.g. https://198.51.100.194
	// BAD_CONTENT_TYPE - URLName is accessible but the returned content-type is not rdf or turtle.
	// BAD_URI - URLName is not accessible
	public static int checkURIexists(String URLName, Set<String> badURIs){
		int startIndex = URLName.indexOf("//") + 2;
		int endIndex = URLName.substring(startIndex).indexOf("/") + 1 ;
		String domainStr = URLName.substring(0, endIndex + startIndex);
		NameSpaceWhiteList nsList = new NameSpaceWhiteList();

		if (hostsToSuppressList != null ) {
			uriWhiteList.addAll(hostsToSuppressList);
		}
		for ( String i: uriWhiteList ) {
			String lowerURLName = URLName.toLowerCase();
			String loweri = i.toLowerCase();
			if (lowerURLName.contains(loweri)) {
				return SUCCESS;
			}
		}

		if (badURIs.contains(domainStr) || inURIBlackList(domainStr, badURIs)) {
			return BAD_DOMAIN;
		}
		else {
			try {
				// Check if the domain is reachable first
				HttpURLConnection myCon =
					(HttpURLConnection) new URL(domainStr).openConnection();
				myCon.setInstanceFollowRedirects(true);
				myCon.setRequestMethod("HEAD");
				int retCode = myCon.getResponseCode();

				if (retCode != HttpURLConnection.HTTP_OK && retCode != HttpURLConnection.HTTP_MOVED_TEMP) {
					if ( !nsList.checkNameSpaceDomain(domainStr) ){
						badURIs.add(domainStr);
						return BAD_DOMAIN;
					}
				}
				else {
					String newURLName = URLName;
					// Handle the redirect from http to https.
					if (retCode== HttpURLConnection.HTTP_MOVED_TEMP ){
						newURLName = URLName.replace(domainStr, myCon.getHeaderField("Location"));
					}
					try {
						// Now check the entire URI
						myCon = (HttpURLConnection) new URL(newURLName).openConnection();
						myCon.setInstanceFollowRedirects(true);
						myCon.setRequestMethod("GET");
						myCon.addRequestProperty("Accept", "application/rdf+xml; text/turtle");
						retCode = myCon.getResponseCode();
						if (retCode != HttpURLConnection.HTTP_OK) {
							return BAD_URI;
						}
						else {
							String contentType = myCon.getHeaderField("Content-Type");
							if (contentType.contains("text/turtle") || contentType.contains("application/rdf+xml")) {
								InputStreamReader in = new InputStreamReader((InputStream) myCon.getContent());
								BufferedReader buff = new BufferedReader(in);
								String line = "";
								String responseBody = "";
								// "http://open-services.net/ns/auto#unavailableeeee" will return HTTP_OK
								// We need to check the response body to make sure the property does exist
								do {
									line = buff.readLine();
									responseBody += line;
								} while (line != null);
								if (responseBody.contains(URLName)) {
									return SUCCESS;
								}
								else {
									return BAD_URI;
								}
							}
							else {
								return BAD_CONTENT_TYPE;
							}
						}
					}
					catch (Exception e) {
						return BAD_URI;
					}
				}
			}
			catch (Exception e) {
			  // e.printStackTrace();
				if ( !nsList.checkNameSpaceDomain(domainStr) ){
					badURIs.add(domainStr);
				}
				return BAD_DOMAIN;
			}
		}
		return BAD_URI;
	 }
}

