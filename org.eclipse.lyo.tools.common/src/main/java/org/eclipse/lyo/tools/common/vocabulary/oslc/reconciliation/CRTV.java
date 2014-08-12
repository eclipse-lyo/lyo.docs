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
//This file is generated automatically, do not edit its content except:
//	- the package name below.
//	- any variable name that is not a valid Java identifier.
//		Please add such variable to the method getSpecialDeclaredFields() manually.
package org.eclipse.lyo.tools.common.vocabulary.oslc.reconciliation;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.lang.reflect.Field;
import java.util.HashMap;

public class CRTV {
	public static String NS = "http://open-services.net/ns/crtv#";
	public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
	public static final String PREFIX = "crtv";

	public static Resource ComputerSystem = ResourceFactory.createProperty( NS + "ComputerSystem");
	public static Resource Database = ResourceFactory.createProperty( NS + "Database");
	public static Resource IPAddress = ResourceFactory.createProperty( NS + "IPAddress");
	public static Resource OperatingSystem = ResourceFactory.createProperty( NS + "OperatingSystem");
	public static Resource Path = ResourceFactory.createProperty( NS + "Path");
	public static Resource Process = ResourceFactory.createProperty( NS + "Process");
	public static Resource ServerAccessPoint = ResourceFactory.createProperty( NS + "ServerAccessPoint");
	public static Resource ServiceInstance = ResourceFactory.createProperty( NS + "ServiceInstance");
	public static Resource SoftwareModule = ResourceFactory.createProperty( NS + "SoftwareModule");
	public static Resource SoftwareServer = ResourceFactory.createProperty( NS + "SoftwareServer");
	public static Resource StorageVolume = ResourceFactory.createProperty( NS + "StorageVolume");
	public static Resource Tablespace = ResourceFactory.createProperty( NS + "Tablespace");

	public static Property address = ResourceFactory.createProperty( NS + "address");
	public static Property assetTag = ResourceFactory.createProperty( NS + "assetTag");
	public static Property contextAddressSpace = ResourceFactory.createProperty( NS + "contextAddressSpace");
	public static Property dbInstance = ResourceFactory.createProperty( NS + "dbInstance");
	public static Property dependsOn = ResourceFactory.createProperty( NS + "dependsOn");
	public static Property deployedTo = ResourceFactory.createProperty( NS + "deployedTo");
	public static Property elementFrom = ResourceFactory.createProperty( NS + "elementFrom");
	public static Property elementTo = ResourceFactory.createProperty( NS + "elementTo");
	public static Property fileName = ResourceFactory.createProperty( NS + "fileName");
	public static Property fqdn = ResourceFactory.createProperty( NS + "fqdn");
	public static Property hostid = ResourceFactory.createProperty( NS + "hostid");
	public static Property instancePath = ResourceFactory.createProperty( NS + "instancePath");
	public static Property ipAddress = ResourceFactory.createProperty( NS + "ipAddress");
	public static Property manufacturer = ResourceFactory.createProperty( NS + "manufacturer");
	public static Property model = ResourceFactory.createProperty( NS + "model");
	public static Property name = ResourceFactory.createProperty( NS + "name");
	public static Property observationTime = ResourceFactory.createProperty( NS + "observationTime");
	public static Property occursBefore = ResourceFactory.createProperty( NS + "occursBefore");
	public static Property parentServiceInstance = ResourceFactory.createProperty( NS + "parentServiceInstance");
	public static Property portNumber = ResourceFactory.createProperty( NS + "portNumber");
	public static Property processId = ResourceFactory.createProperty( NS + "processId");
	public static Property runsOn = ResourceFactory.createProperty( NS + "runsOn");
	public static Property serialNumber = ResourceFactory.createProperty( NS + "serialNumber");
	public static Property serverAccessPoint = ResourceFactory.createProperty( NS + "serverAccessPoint");
	public static Property shortHostName = ResourceFactory.createProperty( NS + "shortHostName");
	public static Property systemBoardUUID = ResourceFactory.createProperty( NS + "systemBoardUUID");
	public static Property version = ResourceFactory.createProperty( NS + "version");
	public static Property vmid = ResourceFactory.createProperty( NS + "vmid");

	public static Resource Application = ResourceFactory.createProperty( NS + "Application");
	public static Resource Compute = ResourceFactory.createProperty( NS + "Compute");
	public static Resource DB2Instance = ResourceFactory.createProperty( NS + "DB2Instance");
	public static Resource DatabaseConnectionPool = ResourceFactory.createProperty( NS + "DatabaseConnectionPool");
	public static Resource DatabaseInstance = ResourceFactory.createProperty( NS + "DatabaseInstance");
	public static Resource IBMHTTPServer = ResourceFactory.createProperty( NS + "IBMHTTPServer");
	public static Resource J2CConnectorPool = ResourceFactory.createProperty( NS + "J2CConnectorPool");
	public static Resource J2EEApplication = ResourceFactory.createProperty( NS + "J2EEApplication");
	public static Resource J2EEServer = ResourceFactory.createProperty( NS + "J2EEServer");
	public static Resource MQQueue = ResourceFactory.createProperty( NS + "MQQueue");
	public static Resource MQQueueManager = ResourceFactory.createProperty( NS + "MQQueueManager");
	public static Resource MessagingServer = ResourceFactory.createProperty( NS + "MessagingServer");
	public static Resource NULL = ResourceFactory.createProperty( NS + "NULL");
	public static Resource Network = ResourceFactory.createProperty( NS + "Network");
	public static Resource OracleInstance = ResourceFactory.createProperty( NS + "OracleInstance");
	public static Resource Storage = ResourceFactory.createProperty( NS + "Storage");
	public static Resource ThreadPool = ResourceFactory.createProperty( NS + "ThreadPool");
	public static Resource Transaction = ResourceFactory.createProperty( NS + "Transaction");
	public static Resource WebServer = ResourceFactory.createProperty( NS + "WebServer");
	public static Resource WebSphereServer = ResourceFactory.createProperty( NS + "WebSphereServer");

	public static String getURI() {
		return (NS);
	}
	public HashMap<String, String> getSpecialDeclaredFields() {
		Field[] fields = this.getClass().getFields();
		HashMap<String, String> simpleFields = new HashMap<String, String>();
		for (Field i: fields) {simpleFields.put(i.getName(), i.getType().toString());}
		// Add Property/Resource with some special characters in the name
		// For example, simpleFields.put("Exactly-one", "interface com.hp.hpl.jena.rdf.model.Resource");
		return simpleFields;
	}
}
