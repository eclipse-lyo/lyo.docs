<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2012 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 
 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 Contributors:
 
    Sam Padgett		 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.j2bugzilla.base.*" %>

<%
	int productId = (Integer) request.getAttribute("productId");
	String bugzillaUri = (String) request.getAttribute("bugzillaUri");
	String selectionUri = (String) request.getAttribute("selectionUri");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Bugzilla OSLC Adapter: Resource Selector</title>
<link href="<%=bugzillaUri%>/skins/standard/global.css" rel="stylesheet" type="text/css">
<link href="<%=bugzillaUri%>/skins/standard/index.css" rel="stylesheet" type="text/css">
<link href="<%=bugzillaUri%>/skins/standard/global.css" rel="alternate stylesheet" title="Classic" type="text/css">
<link href="<%=bugzillaUri%>/skins/standard/index.css" rel="alternate stylesheet" title="Classic" type="text/css">
<link href="<%=bugzillaUri%>/skins/contrib/Dusk/global.css" rel="stylesheet" title="Dusk" type="text/css">
<link href="<%=bugzillaUri%>/skins/contrib/Dusk/index.css" rel="stylesheet" title="Dusk" type="text/css">
<link href="<%=bugzillaUri%>/skins/custom/global.css" rel="stylesheet" type="text/css">
<link href="<%=bugzillaUri%>/skins/custom/index.css" rel="stylesheet" type="text/css">
<link rel="shortcut icon" href="<%=bugzillaUri%>/images/favicon.ico">

<script type="text/javascript" src="../../../bugzilla.js"></script>

</head>
<body style="padding: 10px;">
	<%-- LAB 3 Uncomment to create the selection dialog and call bugzilla.js select() with the selections
	     Go to bugzilla.js to complete the select() code
	
	<div id="bugzilla-body">

		<p id="searchMessage">Find a specific bug by entering words that describe it.</p>

		<p id="loadingMessage" style="display: none;">Bugzilla is pondering your
			search. Please stand by ...</p>

		<div>
			<input type="search" style="width: 335px" id="searchTerms" placeholder="Enter search terms" autofocus>
			<button type="button"
				onclick="search( '<%= selectionUri %>' )">Search</button>
		</div>

		<div style="margin-top: 5px;">
			<select id="results" size="10" style="width: 400px"></select>
		</div>

		<div style="width: 400px; margin-top: 5px;">
			<button style="float: right;" type="button"
				onclick="javascript: cancel()">Cancel</button>
			<button style="float: right;" type="button"
				onclick="javascript: select();">OK</button>
		</div>
		
		
		<div style="clear: both;"></div>
        --%>
	</div>
</body>
</html>