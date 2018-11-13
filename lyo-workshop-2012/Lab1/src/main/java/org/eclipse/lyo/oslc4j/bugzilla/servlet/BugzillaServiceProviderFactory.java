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
 *     Michael Fiedler      - Bugzilla adapter implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.lyo.oslc4j.bugzilla.services.BugzillaChangeRequestService;
import org.eclipse.lyo.oslc4j.bugzilla.Constants;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderFactory;

public class BugzillaServiceProviderFactory
{
    private static Class<?>[] RESOURCE_CLASSES =
    {
        BugzillaChangeRequestService.class
    };

    private BugzillaServiceProviderFactory()
    {
        super();
    }

    /**
     * Create a new Bugzilla OSLC change management service provider.
     * @param baseURI
     * @param product
     * @param parameterValueMap - a map containing the path replacement value for {productId}.  See ServiceProviderCatalogSingleton.initServiceProvidersFromProducts()
     * @return
     * @throws OslcCoreApplicationException
     * @throws URISyntaxException
     */
    public static ServiceProvider createServiceProvider(final String baseURI, final String product, final Map<String,Object> parameterValueMap)
           throws OslcCoreApplicationException, URISyntaxException
    {
        final ServiceProvider serviceProvider = ServiceProviderFactory.createServiceProvider(baseURI,
                                                                                             ServiceProviderRegistryURIs.getUIURI(),
                                                                                             product,
                                                                                             "Service provider for Bugzilla product: "+product,
                                                                                             new Publisher("Eclipse Lyo", "urn:oslc:ServiceProvider"),
                                                                                             RESOURCE_CLASSES,
                                                                                             parameterValueMap);
        URI detailsURIs[] = {new URI(baseURI)};
        serviceProvider.setDetails(detailsURIs);

        final PrefixDefinition[] prefixDefinitions =
        {
            new PrefixDefinition(OslcConstants.DCTERMS_NAMESPACE_PREFIX,             new URI(OslcConstants.DCTERMS_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_CORE_NAMESPACE_PREFIX,           new URI(OslcConstants.OSLC_CORE_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_DATA_NAMESPACE_PREFIX,           new URI(OslcConstants.OSLC_DATA_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDF_NAMESPACE_PREFIX,                 new URI(OslcConstants.RDF_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDFS_NAMESPACE_PREFIX,                new URI(OslcConstants.RDFS_NAMESPACE)),
            new PrefixDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE_PREFIX,       new URI(Constants.CHANGE_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.BUGZILLA_NAMESPACE_PREFIX,                new URI(Constants.BUGZILLA_NAMESPACE)),
            new PrefixDefinition(Constants.FOAF_NAMESPACE_PREFIX,                    new URI(Constants.FOAF_NAMESPACE)),
            new PrefixDefinition(Constants.QUALITY_MANAGEMENT_PREFIX,                new URI(Constants.QUALITY_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.REQUIREMENTS_MANAGEMENT_PREFIX,           new URI(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_PREFIX, new URI(Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_NAMESPACE))
        };

        serviceProvider.setPrefixDefinitions(prefixDefinitions);

        return serviceProvider;
    }
}
