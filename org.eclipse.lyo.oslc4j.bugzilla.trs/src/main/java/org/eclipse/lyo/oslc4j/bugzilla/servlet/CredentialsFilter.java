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
import java.net.URISyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.http.HttpMessage;
import net.oauth.server.OAuthServlet;

import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;
import org.eclipse.lyo.oslc4j.bugzilla.Credentials;
import org.eclipse.lyo.oslc4j.bugzilla.exception.UnauthorizedException;
import org.eclipse.lyo.oslc4j.bugzilla.utils.HttpUtils;
import org.eclipse.lyo.server.oauth.consumerstore.FileSystemConsumerStore;
import org.eclipse.lyo.server.oauth.core.Application;
import org.eclipse.lyo.server.oauth.core.AuthenticationException;
import org.eclipse.lyo.server.oauth.core.OAuthConfiguration;
import org.eclipse.lyo.server.oauth.core.OAuthRequest;
import org.eclipse.lyo.server.oauth.core.consumer.LyoOAuthConsumer;
import org.eclipse.lyo.server.oauth.core.token.LRUCache;
import org.eclipse.lyo.server.oauth.core.token.SimpleTokenStrategy;

import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.rpc.LogIn;

public class CredentialsFilter implements Filter {


	
    public static final String CONNECTOR_ATTRIBUTE = BugzillaAdapterCredentialsFilter.CONNECTOR_ATTRIBUTE;
    public static final String CREDENTIALS_ATTRIBUTE = BugzillaAdapterCredentialsFilter.CREDENTIALS_ATTRIBUTE;
    private static final String ADMIN_SESSION_ATTRIBUTE = BugzillaAdapterCredentialsFilter.ADMIN_SESSION_ATTRIBUTE;
    public static final String JAZZ_INVALID_EXPIRED_TOKEN_OAUTH_PROBLEM = BugzillaAdapterCredentialsFilter.JAZZ_INVALID_EXPIRED_TOKEN_OAUTH_PROBLEM;
    public static final String OAUTH_REALM = "Bugzilla";
		
	private static LRUCache<String, BugzillaConnector> keyToConnectorCache = new LRUCache<String, BugzillaConnector>(200);
	
	@Override
	public void destroy() {
		

	}
	

	/*
	 * Modified in Lab 1.2
	 */
	/**
	 * Check for OAuth or BasicAuth credentials and challenge if not found.
	 * 
	 * Store the BugzillaConnector in the HttpSession for retrieval in the REST services.
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		
		if(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;
		
			boolean isTwoLeggedOAuthRequest = false;
			
			//Don't protect requests to oauth service.   TODO: possibly do this in web.xml
			if (! request.getPathInfo().startsWith("/oauth"))
			{
			
				// First check if this is an OAuth request.
				try {
					try {
						OAuthMessage message = OAuthServlet.getMessage(request, null);
						// test if this is a valid two-legged oauth request
						if ("".equals(message.getToken())) {
							validateTwoLeggedOAuthMessage(message);
							isTwoLeggedOAuthRequest = true;
						}
						
						if (!isTwoLeggedOAuthRequest && message.getToken() != null) {
							OAuthRequest oAuthRequest = new OAuthRequest(request);
							oAuthRequest.validate();
							BugzillaConnector connector = keyToConnectorCache.get(message
									.getToken());
							if (connector == null) {
								throw new OAuthProblemException(
										OAuth.Problems.TOKEN_REJECTED);
							}
			
							request.getSession().setAttribute(CONNECTOR_ATTRIBUTE, connector);
						}
					} catch (OAuthProblemException e) {
						if (OAuth.Problems.TOKEN_REJECTED.equals(e.getProblem()))
							throwInvalidExpiredException(e);
						else
							throw e;
					}
				} catch (OAuthException e) {
					OAuthServlet.handleException(response, e, OAUTH_REALM);
					return;
				} catch (URISyntaxException e) {
					throw new ServletException(e);
				}
                
				
				// This is not an OAuth request. Check for basic access authentication.
				HttpSession session = request.getSession();
				BugzillaConnector connector = (BugzillaConnector) session
						.getAttribute(CONNECTOR_ATTRIBUTE);
				if (connector == null) {
					try {
						Credentials credentials;
						if (isTwoLeggedOAuthRequest) {
							connector = keyToConnectorCache.get("");
							if (connector == null) {
								credentials = BugzillaManager.getAdminCredentials();
								connector = getBugzillaConnector(credentials);
								keyToConnectorCache.put("", connector);
							}
							credentials = null; // TODO; Do we need to keep the credentials for this path ??
						} else {
							credentials = (Credentials) request.getSession().getAttribute(CREDENTIALS_ATTRIBUTE);
							if (credentials == null)
							{
								credentials = HttpUtils.getCredentials(request);
								if (credentials == null) {
									throw new UnauthorizedException();
								}
							}
							connector = getBugzillaConnector(credentials);
						}
						session.setAttribute(CONNECTOR_ATTRIBUTE, connector);
						session.setAttribute(CREDENTIALS_ATTRIBUTE,
						        credentials);
				
					} catch (UnauthorizedException e)
					{
						HttpUtils.sendUnauthorizedResponse(response, e);
						System.err.println(e.getMessage());
						return;
					} catch (ConnectionException ce)
					{
						throw new ServletException(ce);
					}
					
				}
				
				// Lock Bugzilla connector instance to prevent concurrent access
				if (connector != null) {
					synchronized (connector) {
						servletRequest.setAttribute(CONNECTOR_ATTRIBUTE, connector);
						chain.doFilter(servletRequest, servletResponse);
					}
					return;
				}
			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}
	
	/*
	 * Added in Lab 1.2
	 */
	private void validateTwoLeggedOAuthMessage(OAuthMessage message)
			throws IOException, OAuthException,
			URISyntaxException {
		OAuthConfiguration config = OAuthConfiguration.getInstance();
		LyoOAuthConsumer consumer = config.getConsumerStore().getConsumer(message.getConsumerKey());
		if (consumer != null && consumer.isTrusted()) {
			// The request can be a two-legged oauth request because it's a trusted consumer
			// Validate the message with an empty token and an empty secret
			OAuthAccessor accessor = new OAuthAccessor(consumer);
			accessor.requestToken = "";
			accessor.tokenSecret = "";
			config.getValidator().validateMessage(message, accessor);
		} else {
			throw new OAuthProblemException(
					OAuth.Problems.TOKEN_REJECTED);
		}
	}
	
	public static BugzillaConnector getBugzillaConnector(Credentials credentials)
			throws ConnectionException, UnauthorizedException {
		BugzillaConnector bc = new BugzillaConnector();
		bc.connectTo(BugzillaManager.getBugzillaUri() + "/xmlrpc.cgi");
		LogIn login = new LogIn(credentials.getUsername(), credentials.getPassword());
		try {
			bc.executeMethod(login);
		} catch (BugzillaException e) {
			e.printStackTrace();
			throw new UnauthorizedException(e.getCause().getMessage());
		}
		return bc;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		OAuthConfiguration config = OAuthConfiguration.getInstance();

		// Validates a user's ID and password.
		config.setApplication(new Application() {
			@Override
			public void login(HttpServletRequest request, String id,
					String password) throws AuthenticationException {
				try {
					BugzillaConnector bc = new BugzillaConnector();
					bc.connectTo(BugzillaManager.getBugzillaUri() + "/xmlrpc.cgi");
					LogIn login = new LogIn(id, password);
					bc.executeMethod(login);
					request.setAttribute(CONNECTOR_ATTRIBUTE, bc);
					String admin = BugzillaManager.getAdmin();
					request.getSession().setAttribute(ADMIN_SESSION_ATTRIBUTE,
							admin != null && admin.equals(id));
					Credentials creds = new Credentials();
					creds.setUsername(id);
					creds.setPassword(password);
                    request.getSession().setAttribute(CREDENTIALS_ATTRIBUTE,
                            creds);
					// Added in Lab 1.2
					request.getSession().setAttribute(CONNECTOR_ATTRIBUTE, bc);
				} catch (Exception e) {
					throw new AuthenticationException(e.getCause().getMessage(), e);
				}
			}

			@Override
			public String getName() {
				// Display name for this application.
				return "Bugzilla";
			}

			@Override
			public boolean isAdminSession(HttpServletRequest request) {
				return Boolean.TRUE.equals(request.getSession().getAttribute(
						ADMIN_SESSION_ATTRIBUTE));
			}

			@Override
			public String getRealm(HttpServletRequest request) {
				return BugzillaManager.REALM;
			}

			@Override
			public boolean isAuthenticated(HttpServletRequest request) {
				BugzillaConnector bc = (BugzillaConnector) request.getSession()
						.getAttribute(CONNECTOR_ATTRIBUTE);
				if (bc == null) {
					return false;
				}
				
				request.setAttribute(CONNECTOR_ATTRIBUTE, bc);
				return true;
			}
		});

		/*
		 * Override some SimpleTokenStrategy methods so that we can keep the
		 * BugzillaConnection associated with the OAuth tokens.
		 */
		config.setTokenStrategy(new SimpleTokenStrategy() {
			@Override
			public void markRequestTokenAuthorized(
					HttpServletRequest httpRequest, String requestToken)
					throws OAuthProblemException {
				keyToConnectorCache.put(requestToken,
						(BugzillaConnector) httpRequest.getAttribute(CONNECTOR_ATTRIBUTE));
				super.markRequestTokenAuthorized(httpRequest, requestToken);
			}

			@Override
			public void generateAccessToken(OAuthRequest oAuthRequest)
					throws OAuthProblemException, IOException {
				String requestToken = oAuthRequest.getMessage().getToken();
				BugzillaConnector bc = keyToConnectorCache.remove(requestToken);
				super.generateAccessToken(oAuthRequest);
				keyToConnectorCache.put(oAuthRequest.getAccessor().accessToken, bc);
			}
		});

		try {
			// For now, hard-code the consumers.
			config.setConsumerStore(new FileSystemConsumerStore("bugzillaOAuthStore.xml"));
		} catch (Throwable t) {
			System.err.println("Error initializing the OAuth consumer store: " +  t.getMessage());
		
		}

	}
	
	/**
	 * Jazz requires a exception with the magic string "invalid_expired_token" to restart
	 * OAuth authentication
	 * @param e
	 * @return
	 * @throws OAuthProblemException 
	 */
	private void throwInvalidExpiredException(OAuthProblemException e) throws OAuthProblemException {
		OAuthProblemException ope = new OAuthProblemException(JAZZ_INVALID_EXPIRED_TOKEN_OAUTH_PROBLEM);
		ope.setParameter(HttpMessage.STATUS_CODE, new Integer(
				HttpServletResponse.SC_UNAUTHORIZED));
		throw ope;
	}
	
	/**
	 * Get BugzillaConnector
	 * @param request
	 * @return
	 */
	public static BugzillaConnector getConnector(HttpServletRequest request) 
	{	
		//connector should never be null if CredentialsFilter is doing its job
		@SuppressWarnings("unchecked")
		BugzillaConnector connector = (BugzillaConnector) request.getAttribute(CONNECTOR_ATTRIBUTE);	
		return connector;
	}
	
	/**
	 * Get Credentials for this session 
	 * @param request
	 * @return credentials
	 */
	public static Credentials getCredentials(HttpServletRequest request)
	{
		@SuppressWarnings("unchecked")
		Credentials credentials = (Credentials) request.getSession().getAttribute(CREDENTIALS_ATTRIBUTE);
		return credentials;
	}


}
