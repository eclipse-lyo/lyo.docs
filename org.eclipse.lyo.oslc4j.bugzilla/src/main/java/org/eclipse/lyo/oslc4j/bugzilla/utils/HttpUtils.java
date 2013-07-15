/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.server.OAuthServlet;

import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.DecodingException;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Credentials;
import org.eclipse.lyo.oslc4j.bugzilla.exception.BugzillaOAuthException;
import org.eclipse.lyo.oslc4j.bugzilla.exception.RestException;
import org.eclipse.lyo.oslc4j.bugzilla.exception.UnauthorizedException;
//import org.eclipse.lyo.oslc4j.bugzilla.resources.Error;
import org.eclipse.lyo.server.oauth.core.OAuthConfiguration;

/**
 * Utilities for working with HTTP requests and responses.
 * 
 * @author Samuel Padgett <spadgett@us.ibm.com>
 */
public class HttpUtils {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
	private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";
	private static final String BASIC_AUTHENTICATION_CHALLENGE = BASIC_AUTHORIZATION_PREFIX
			+ "realm=\"" + BugzillaManager.REALM + "\"";
	private static final String OAUTH_AUTHORIZATION_PREFIX = "OAuth ";
	private static final String OAUTH_AUTHENTICATION_CHALLENGE = OAUTH_AUTHORIZATION_PREFIX
			+ "realm=\"" + BugzillaManager.REALM + "\"";
	
	/**
	 * Gets the credentials from an HTTP request.
	 * 
	 * @param request
	 *            the request
	 * @return the Bugzilla credentials or <code>null</code> if the request did
	 *         not contain an <code>Authorization</code> header
	 * @throws UnauthorizedException
	 *             on problems reading the credentials from the
	 *             <code>Authorization</code> request header
	 */
	public static Credentials getCredentials(HttpServletRequest request)
			throws UnauthorizedException {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader == null || "".equals(authorizationHeader)) {
			return null;
		}
	
		Credentials credentials = new Credentials();
		if (!authorizationHeader.startsWith(HttpUtils.BASIC_AUTHORIZATION_PREFIX)) {
			throw new UnauthorizedException(
					"Only basic access authentication is supported.");
		}
		
		String encodedString = authorizationHeader.substring(HttpUtils.BASIC_AUTHORIZATION_PREFIX.length());
		try {
			String unencodedString = new String(Base64.decode(encodedString), "UTF-8");
			int seperator = unencodedString.indexOf(':');
			if (seperator == -1) {
				throw new UnauthorizedException("Invalid Authorization header value.");
			}
			
			credentials.setUsername(unencodedString.substring(0, seperator));
			credentials.setPassword(unencodedString.substring(seperator + 1));
		} catch (DecodingException e) {
			throw new UnauthorizedException("Username and password not Base64 encoded.");
		} catch (UnsupportedEncodingException e) {
			throw new UnauthorizedException("Invalid Authorization header value.");
		}
		
		return credentials;
	}

	public static void sendUnauthorizedResponse(HttpServletResponse response,
			UnauthorizedException e) throws IOException, ServletException {
		if (e instanceof BugzillaOAuthException) {
			OAuthServlet.handleException(response, e, BugzillaManager.REALM);
		} else {
			// Accept basic access or OAuth authentication.
			response.addHeader(WWW_AUTHENTICATE_HEADER,
					OAUTH_AUTHENTICATION_CHALLENGE);
			response.addHeader(WWW_AUTHENTICATE_HEADER,
					BASIC_AUTHENTICATION_CHALLENGE);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}


	
}
