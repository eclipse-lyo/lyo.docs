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
 * Contributors:
 *
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.servlet;

import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Credentials;
import org.eclipse.lyo.oslc4j.bugzilla.exception.UnauthorizedException;

import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.rpc.LogIn;

/**
 * Concurrent access support
 * 
 * <p>This class keeps {@link #connectorPoolMap} map between an instance of BugzillaConnector, which represents a login session (we
 * call this as "session instance" here for after), and
 * available instances of the BugzillaConnectors built for the session. The shape of
 * the map is illustrated bellow:
 * <pre>{[BugzillaConnector at login] =&lt; [QUEUE: [BugzillaConnector at login],[BugzillaConnector],..]}</pre>
 * 
 * Note that the session instance is also in the queue.
 * 
 * <p>Client takes the instance using {@link #acquireConnector(BugzillaConnector)} and release
 * by {@link #releaseConnector(BugzillaConnector, BugzillaConnector)}.
 * 
 * @see #buildConnectorPool(BugzillaConnector, Credentials)
 * @see #acquireConnector(BugzillaConnector)
 * @see #releaseConnector(BugzillaConnector, BugzillaConnector)
 */
public class BugzillaAdapterCredentialsFilterMT 
		extends BugzillaAdapterCredentialsFilter {
	
	/**
	 * The number of allowed concurrent bugzilla connector threads for each session
	 */
	private final int MAX_CONCURRENT_THREADS = 2;
	
	/**
	 */
	private WeakHashMap<BugzillaConnector, BlockingQueue<BugzillaConnector>> connectorPoolMap = new WeakHashMap<BugzillaConnector, BlockingQueue<BugzillaConnector>>();
	
	public BugzillaAdapterCredentialsFilterMT()
	{
		super();
	}
	
	@Override
	protected BugzillaConnector login(Credentials creds, HttpServletRequest request) 
			throws UnauthorizedException, ServletException 
	{
		BugzillaConnector keyConnector = super.login(creds, request);
		buildConnectorPool(keyConnector, creds);
		return keyConnector;
	}

	private BugzillaConnector doLogin(Credentials creds)
			throws ServletException, UnauthorizedException {
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
			throw new UnauthorizedException(e.getCause().getMessage());
		}
		return bc;
	}
	
	@Override
	protected void doChainDoFilterWithConnector(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			BugzillaConnector connector) throws IOException, ServletException {
		
		BugzillaConnector requestConnector = acquireConnector(connector);
		try {
			//super.doChainDoFilterWithConnector(request, response, chain, requestConnector);
			request.setAttribute(CONNECTOR_ATTRIBUTE, requestConnector);
			chain.doFilter(request, response);
		} finally {
			releaseConnector(connector, requestConnector);
		}
	}
	
	/**
	 * Prepare connection pool with two connections available for the current login
	 */
	private void buildConnectorPool(BugzillaConnector keyConnector, Credentials creds) throws ServletException {
		BlockingQueue<BugzillaConnector> queue = new ArrayBlockingQueue<BugzillaConnector>(MAX_CONCURRENT_THREADS);
		queue.add(keyConnector);
		for (int i=1; i<MAX_CONCURRENT_THREADS; i++) {
			BugzillaConnector con;
			try {
				con = doLogin(creds);
				queue.add(con);
			} catch (UnauthorizedException e) {
				throw new ServletException(e);
			}
		}
		connectorPoolMap.put(keyConnector, queue);
	}
	
	private BugzillaConnector acquireConnector(BugzillaConnector keyConnector) throws ServletException {
		try {
			return connectorPoolMap.get(keyConnector).take();
		} catch (InterruptedException e) {
			throw new ServletException(e);
		}
	}

	private void releaseConnector(BugzillaConnector keyConnector,
			BugzillaConnector requestConnector) throws ServletException {
		try {
			connectorPoolMap.get(keyConnector).put(requestConnector);
		} catch (InterruptedException e) {
			throw new ServletException(e);
		}
	}

}
