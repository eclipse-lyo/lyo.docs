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
package org.eclipse.lyo.tools.common.util;

public class NameSpaceWhiteList {

	private String[] whiteList = {
			// common name space
			"http://purl.org/dc/terms/",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
			"http://www.w3.org/2000/01/rdf-schema#",
			"http://xmlns.com/foaf/0.1/",
			"http://www.w3.org/2002/07/owl#",
			"http://www.w3.org/2001/XMLSchema#",
			"http://www.rddl.org/",
			"http://www.w3.org/2011/http-headers#",
			"http://www.w3.org/2011/http#",
			"http://www.w3.org/2011/http-methods#",
			"http://www.w3.org/2011/content#",
			"http://open-services.net/ns/authoring#",
			// name space white list from http://open-services.net/wiki/core/Vocabulary-index/
			"http://open-services.net/ns/core#",
			"http://open-services.net/ns/cm#",
			"http://open-services.net/ns/qm#",
			"http://open-services.net/ns/rm#",
			"http://open-services.net/ns/am#",
			"http://open-services.net/ns/asset#",
			"http://open-services.net/ns/auto#",
			"http://open-services.net/ns/perfmon#",
			"http://open-services.net/ns/metrics#",
			"http://open-services.net/ns/crtv#",
			// name space white list from https://jazz.net/ns/
			"http://jazz.net/ns/dm/diagram#",
			"http://jazz.net/ns/dm/document#",
			"http://jazz.net/ns/dm/linktypes#",
			"http://jazz.net/ns/dm/sketcher#",
			"http://jazz.net/ns/pd/extensions#",
			"http://jazz.net/ns/psm/focalpoint/datatypes#",
			"http://jazz.net/ns/psm/focalpoint#",
			"http://jazz.net/ns/pd#",
			"http://jazz.net/ns/qm/rqm#",
			"http://jazz.net/ns/rm/linktypes#",
			"http://jazz.net/ns/rm#",
			"http://jazz.net/ns/dm/rhapsody/sysml#",
			"http://jazz.net/ns/dm/rhapsody/testing#",
			"http://jazz.net/ns/dm/rhapsody/uml#",
			"http://jazz.net/ns/dm/rsa/deployment/core#",
			"http://jazz.net/ns/dm/rsa/uml#",
			"http://jazz.net/ns/reporting/sparqlgateway#",
			"http://jazz.net/ns/ism/admin/health#",
			"http://jazz.net/ns/ism/admin#",
			"http://jazz.net/ns/ism/perfmon/itm#",
			"http://jazz.net/ns/ism/event/omnibus#",
			"http://jazz.net/ns/ism/event/omnibus/itnm#",
			"http://jazz.net/ns/ism/event/omnibus/misc#",
			"http://jazz.net/ns/ism/event/omnibus/tbsm#",
			"http://jazz.net/ns/ism/registry#",
			// name space white list from
			// https://w3-connections.ibm.com/wikis/home?lang=en#!/wiki/Wde14c82a1d28_4726_a1e3_68030f57eeab/page/Tivoli%20namespace%20registry
			"http://jazz.net/ns/ism#",
			"http://jazz.net/ns/ism/registry#",
			"http://jazz.net/ns/ism/perfmon#",
			//"http://jazz.net/ns/ism/perfmon/itm#",
			"http://jazz.net/ns/ism/perfmon/cloudhealth#",
			"http://jazz.net/ns/ism/event#",
			//"http://jazz.net/ns/ism/event/omnibus#",
			"http://jazz.net/ns/ism/event/impact#",
			"http://jazz.net/ns/ism/config#",
			"http://jazz.net/ns/ism/config/taddm#",
			"http://jazz.net/ns/ism/ui#",
			"http://jazz.net/ns/ism/admin#",
			"http://jazz.net/ns/ism/metering#",
			"http://jazz.net/ns/ism/metering/sccm#",
			"http://jazz.net/ns/ism/provisioning#",
			"http://jazz.net/ns/ism/provisioning/sco#",
			"http://jazz.net/ns/ism/asset#",
			"http://jazz.net/ns/ism/asset/smarter_physical_infrastructure#",
			"http://jazz.net/ns/ism/change#",
			"http://jazz.net/ns/ism/work/smarter_physical_infrastructure#",
			"http://jazz.net/ns/ism/automation#",
			"http://jazz.net/ns/ism/automation/scheduling#",
			"http://jazz.net/ns/ism/helpdesk#",
			"http://jazz.net/ns/ism/helpdesk/sccd#",
			"http://jazz.net/ns/ism/storage#",
			"http://jazz.net/ns/ism/storage/tsm#"
};

	public NameSpaceWhiteList() {
		}

	public boolean checkNameSpace(String nameSpace)
	{
		for (String s: whiteList)
		{
			if (s.equals(nameSpace) )
			{
				return true;
			}
		}
		return false;
	}

	public boolean checkNameSpaceDomain(String domainStr)
	{
		for (String s: whiteList)
		{
			if (s.contains(domainStr) )
			{
				return true;
			}
		}
		return false;
	}
}
