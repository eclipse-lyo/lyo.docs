/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
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
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Credentials;
import org.eclipse.lyo.oslc4j.bugzilla.utils.HttpUtils;
import org.eclipse.lyo.server.oauth.consumerstore.FileSystemConsumerStore;
import org.eclipse.lyo.server.oauth.core.consumer.ConsumerStore;
import org.eclipse.lyo.server.oauth.core.utils.AbstractAdapterCredentialsFilter;

import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.rpc.LogIn;

/**
 * This is yet another {@link CredentialsFilter} that is based on the common base class.
 * The base class provide common technique to implement both HTTP Basic and OAuth for
 * your adapter. You concentrate in this class implementing login method to your tool.
 * 
 * <p>You can try this CredentialsFilter by replacing all occurences of CredentialsFilter
 * in this Bugzilla TRS adapter example. They are in {@link BugzillaManager} and
 * web.xml.
 * 
 * @see AbstractAdapterCredentialsFilter
 */
public class BugzillaAdapterCredentialsFilter 
		extends AbstractAdapterCredentialsFilter<Credentials, BugzillaConnector> {
	
	public BugzillaAdapterCredentialsFilter()
	{
		super("Bugzilla", "Bugzilla"); // Initialize superclass with the display name and realm.
	}
	
	@Override
	protected Credentials getCredentialsForOAuth(String id, String password) {
		if (id == OAUTH_EMPTY_TOKEN_KEY) {
			return BugzillaManager.getAdminCredentials();
		}
		Credentials result = new Credentials();
		result.setUsername(id);
		result.setPassword(password);
		return result;
	}
	
	@Override
	protected Credentials getCredentialsFromRequest(HttpServletRequest request) throws org.eclipse.lyo.server.oauth.core.utils.UnauthorizedException {
		try {
			Credentials credentials = HttpUtils.getCredentials(request);
			if (credentials == null) {
				throw new org.eclipse.lyo.server.oauth.core.utils.UnauthorizedException();
			}
			return credentials;
		} catch (org.eclipse.lyo.oslc4j.bugzilla.exception.UnauthorizedException e) {
			throw new org.eclipse.lyo.server.oauth.core.utils.UnauthorizedException(e);
		}
	}
	
	@Override
	protected BugzillaConnector login(Credentials creds, HttpServletRequest request) 
			throws org.eclipse.lyo.server.oauth.core.utils.UnauthorizedException, ServletException 
	{
		BugzillaConnector bc = new BugzillaConnector();
		try {
			bc.connectTo(BugzillaManager.getBugzillaUri() + "/xmlrpc.cgi");
		} catch (ConnectionException e) {
			throw new ServletException(e);
		}
		
		LogIn login = new LogIn(creds.getUsername(), creds.getPassword());
		try {
			bc.executeMethod(login);
		} catch (BugzillaException e) {
			e.printStackTrace();
			throw new org.eclipse.lyo.server.oauth.core.utils.UnauthorizedException(e.getCause().getMessage());
		}
		return bc;
	}
	
	@Override
	protected void logout(BugzillaConnector loginSession, HttpSession session)
	{
		// do nothing for Bugzilla
	}
	
	@Override
	protected boolean isAdminSession(String id, BugzillaConnector session,
			HttpServletRequest request) 
	{
		String admin = BugzillaManager.getAdmin();
		return admin != null && admin.equals(id);
	}
	
	@Override
	protected ConsumerStore createConsumerStore() throws Exception
	{
		return new FileSystemConsumerStore("bugzillaOAuthStore.xml");
	}
	
	@Override
	protected void doChainDoFilterWithConnector(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			BugzillaConnector connector) throws IOException, ServletException {
		
		//
		// As BugzillaConnector doesn't support concurrent access, serialize the 
		// request processes here to avoid concurrent access.
		//
		synchronized (connector) {
			request.setAttribute(CONNECTOR_ATTRIBUTE, connector);
			chain.doFilter(request, response);
		}
	}

}
