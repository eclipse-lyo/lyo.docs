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
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *     Michael Fiedler      - adapted for Bugzilla service provider
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.servlet;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.bugzilla.jbugzx.rpc.GetAccessibleProducts;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager;

import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.rpc.GetProduct;

/**
 * This is the OSLC service provider catalog for the Bugzilla adapter.  Service providers are
 * not registered with the catalog until a request comes in to access either the catalog or a 
 * specific service provider.   This request could be from an external consumer or an internal
 * request triggered by a consumer accessing a change request.
 * 
 * The service providers are created and registered in the initServiceProvidersFromProducts()
 * method.  A list of accessible products is retrieved from Bugzilla and a ServiceProvider is
 * created and registered for each using the Bugzilla productId as the identifier.
 * 
 * The registered service providers are refreshed on each catalog or service provider collection
 * request.
 */
public class ServiceProviderCatalogSingleton
{
    private static final ServiceProviderCatalog             serviceProviderCatalog;
    private static final SortedMap<String, ServiceProvider> serviceProviders = new TreeMap<String, ServiceProvider>();

    static
    {
        try
        {
            serviceProviderCatalog = new ServiceProviderCatalog();

            serviceProviderCatalog.setAbout(new URI(ServiceProviderRegistryURIs.getServiceProviderRegistryURI()));
            serviceProviderCatalog.setTitle("OSLC Service Provider Catalog");
            serviceProviderCatalog.setDescription("OSLC Service Provider Catalog");
            serviceProviderCatalog.setPublisher(new Publisher("Project Lyo", "org.eclipse.lyo.oslc4j.bugzilla"));
            serviceProviderCatalog.getPublisher().setIcon(new URI("http://open-services.net/css/images/logo-forflip.png"));
        }
        catch (final URISyntaxException exception)
        {
            // We should never get here.
            throw new ExceptionInInitializerError(exception);
        }
    }


    private ServiceProviderCatalogSingleton()
    {   	
        super();
    }
    

    public static URI getUri()
    {
    	return serviceProviderCatalog.getAbout();
    }

    public static ServiceProviderCatalog getServiceProviderCatalog(HttpServletRequest httpServletRequest) 
    {
    	initServiceProvidersFromProducts(httpServletRequest);
        return serviceProviderCatalog;
    }

    public static ServiceProvider[] getServiceProviders(HttpServletRequest httpServletRequest) 
    {
        synchronized(serviceProviders)
        {
        	initServiceProvidersFromProducts(httpServletRequest);
            return serviceProviders.values().toArray(new ServiceProvider[serviceProviders.size()]);
        }
    }

    public static ServiceProvider getServiceProvider(HttpServletRequest httpServletRequest, final String serviceProviderId) 
    {
        ServiceProvider serviceProvider;

        synchronized(serviceProviders)
        {     	
            serviceProvider = serviceProviders.get(serviceProviderId);
            
            //One retry refreshing the service providers
            if (serviceProvider == null)
            {
            	getServiceProviders(httpServletRequest);
            	serviceProvider = serviceProviders.get(serviceProviderId);
            }
        }

        if (serviceProvider != null)
        {
            return serviceProvider;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    public static ServiceProvider registerServiceProvider(final HttpServletRequest httpServletRequest,
                                                          final ServiceProvider    serviceProvider,
                                                          final String productId) throws URISyntaxException
    {
        synchronized(serviceProviders)
        {
            final URI serviceProviderURI = new URI(httpServletRequest.getScheme(),
                                                   null,
                                                   httpServletRequest.getServerName(),
                                                   httpServletRequest.getServerPort(),
                                                   httpServletRequest.getContextPath() + "/serviceProviders/" + productId,
                                                   null,
                                                   null);

            return registerServiceProviderNoSync(serviceProviderURI,
                                                 serviceProvider,
                                                 productId);
        }
    }


/**
 * Register a service provider with the OSLC catalog
 * 
 * @param serviceProviderURI
 * @param serviceProvider
 * @param productId
 * @return
 */
    private static ServiceProvider registerServiceProviderNoSync(final URI             serviceProviderURI,
                                                                 final ServiceProvider serviceProvider,
                                                                 final String productId)
    {
        final SortedSet<URI> serviceProviderDomains = getServiceProviderDomains(serviceProvider);

        serviceProvider.setAbout(serviceProviderURI);
        serviceProvider.setIdentifier(productId);
        serviceProvider.setCreated(new Date());
        serviceProvider.setDetails(new URI[] {serviceProviderURI});

        serviceProviderCatalog.addServiceProvider(serviceProvider);
        serviceProviderCatalog.addDomains(serviceProviderDomains);

        serviceProviders.put(productId,
                             serviceProvider);


        return serviceProvider;
    }
    
    // This version is for self-registration and thus package-protected
    static ServiceProvider registerServiceProvider(final String          baseURI,
                                                   final ServiceProvider serviceProvider,
                                                   final String productId)
           throws URISyntaxException
    {
        synchronized(serviceProviders)
        {
            final URI serviceProviderURI = new URI(baseURI + "/serviceProviders/" + productId);

            return registerServiceProviderNoSync(serviceProviderURI,
                                                 serviceProvider,
                                                 productId);
        }
    }

    public static void deregisterServiceProvider(final String serviceProviderId)
    {
        synchronized(serviceProviders)
        {
            final ServiceProvider deregisteredServiceProvider = serviceProviders.remove(serviceProviderId);

            if (deregisteredServiceProvider != null)
            {
                final SortedSet<URI> remainingDomains = new TreeSet<URI>();

                for (final ServiceProvider remainingServiceProvider : serviceProviders.values())
                {
                    remainingDomains.addAll(getServiceProviderDomains(remainingServiceProvider));
                }

                final SortedSet<URI> removedServiceProviderDomains = getServiceProviderDomains(deregisteredServiceProvider);

                removedServiceProviderDomains.removeAll(remainingDomains);
                serviceProviderCatalog.removeDomains(removedServiceProviderDomains);
                serviceProviderCatalog.removeServiceProvider(deregisteredServiceProvider);
            }
            else
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
    }

    private static SortedSet<URI> getServiceProviderDomains(final ServiceProvider serviceProvider)
    {
        final SortedSet<URI> domains = new TreeSet<URI>();

        if (serviceProvider!=null) {
    		final Service[] services = serviceProvider.getServices();
	        for (final Service service : services)
	        {
	            final URI domain = service.getDomain();

	            domains.add(domain);
	        }
        }
        return domains;
    }
    
    /**
     * Retrieve a list of products from Bugzilla and construct a service provider for each.
     * 
     * Each product ID is added to the parameter map which will be used during service provider
     * creation to create unique URI paths for each Bugzilla product.  See @Path definition at
     * the top of BugzillaChangeRequestService.
     * 
     * @param httpServletRequest
     */
    protected static void initServiceProvidersFromProducts (HttpServletRequest httpServletRequest)   
    {
    	
      	
		try {
		/* LAB 1 - Uncomment this method which creates OSLC ServiceProviders for each Product in Bugzilla
		 * 
			BugzillaConnector bc = BugzillaManager.getBugzillaConnector(httpServletRequest);

			GetAccessibleProducts getProductIds = new GetAccessibleProducts();
	        bc.executeMethod(getProductIds);
	        Integer[] productIds = getProductIds.getIds();

	        String basePath = BugzillaManager.getBugzServiceBase();
	        
	        //Register a service provider for each product
	        for (Integer p : productIds) {
		        String productId = Integer.toString(p);
		        	
		        if (! serviceProviders.containsKey(productId)) {
		        		        
		        	GetProduct getProductMethod = new GetProduct(p);
	            	bc.executeMethod(getProductMethod);
	            	String product = getProductMethod.getProduct().getName();
	            	
	            	
		        	Map<String, Object> parameterMap = new HashMap<String, Object>();
		        	parameterMap.put("productId",productId);
		        	final ServiceProvider bugzillaServiceProvider = BugzillaServiceProviderFactory.createServiceProvider(basePath, product, parameterMap);
		            registerServiceProvider(basePath,bugzillaServiceProvider,productId);
	        	}
	         }
		*/
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}
		
	
    }
}
