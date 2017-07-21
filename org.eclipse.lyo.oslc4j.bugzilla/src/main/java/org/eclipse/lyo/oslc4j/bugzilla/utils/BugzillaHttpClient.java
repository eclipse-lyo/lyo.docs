/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
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
 *    Steve Pitschke - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.lyo.oslc4j.bugzilla.Credentials;

/**
 * Utility for doing HTTP GET's against Bugzilla server.
 */
public class BugzillaHttpClient
{
    public
    BugzillaHttpClient(
        String rootURL,
        Credentials credentials
    ) throws UnsupportedEncodingException
    {
        this.rootURL = rootURL;
       
        authorization = "&login=" +
            URLEncoder.encode(credentials.getUsername(), "UTF-8") +
            "&password=" +
            URLEncoder.encode(credentials.getPassword(), "UTF-8");

        httpClient = getHttpClient();
        
        cntxt = new BasicHttpContext();        
    }
    
    public InputStream
    httpGet(String path) throws IOException
    {
        HttpGet get = new HttpGet(rootURL + "/" + path + authorization);
        
        HttpResponse response = httpClient.execute(get, cntxt);
        StatusLine status = response.getStatusLine();
        
        if (status.getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException(status.getReasonPhrase());
        }
        
        return response.getEntity().getContent();
    }
    
    private static HttpClient
    getHttpClient()
    {
    	
        SSLContext sc = getTrustingSSLContext();
                
        return HttpClientBuilder.create().useSystemProperties().setSSLContext(sc).build();
    }
    
    private static SSLContext
    getTrustingSSLContext()
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private final String rootURL;
    private final String authorization;
    private final HttpClient httpClient;
    private final HttpContext cntxt;
    
    private static final int TIMEOUT = 30000; // 30 seconds
}
