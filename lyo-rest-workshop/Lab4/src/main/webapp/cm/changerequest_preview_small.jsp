<!DOCTYPE html>
<!--
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
 -->
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.net.*,java.util.*,java.text.SimpleDateFormat" %>
<%@ page import="org.eclipse.lyo.oslc4j.bugzilla.resources.BugzillaChangeRequest" %>
<%@ page import="org.eclipse.lyo.oslc4j.bugzilla.resources.Person" %>


<%
BugzillaChangeRequest changeRequest = (BugzillaChangeRequest)request.getAttribute("changeRequest");
String bugzillaUri = (String) request.getAttribute("bugzillaUri");

Date createdDate = (Date) changeRequest.getCreated(); 
SimpleDateFormat formatter = new SimpleDateFormat();
String created = formatter.format(createdDate);
Date modifiedDate = (Date) changeRequest.getModified();
String modified = formatter.format(modifiedDate);

Person assigneePerson = (Person) changeRequest.getContributors().get(0);
String assignee = "Unknown";
if (assigneePerson != null)
	assignee = assigneePerson.getMbox();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Change Request: <%= changeRequest.getTitle() %>(<%= changeRequest.getIdentifier() %>))</title>
<link href="<%= bugzillaUri %>/skins/standard/global.css" rel="stylesheet" type="text/css">
<link href="<%= bugzillaUri %>/skins/standard/index.css" rel="stylesheet" type="text/css">
<link href="<%= bugzillaUri %>/skins/standard/global.css" rel="alternate stylesheet" title="Classic" type="text/css">
<link href="<%= bugzillaUri %>/skins/standard/index.css" rel="alternate stylesheet" title="Classic" type="text/css">
<link href="<%= bugzillaUri %>/skins/contrib/Dusk/global.css" rel="stylesheet" title="Dusk" type="text/css">
<link href="<%= bugzillaUri %>/skins/contrib/Dusk/index.css" rel="stylesheet" title="Dusk" type="text/css">
<link href="<%= bugzillaUri %>/skins/custom/global.css" rel="stylesheet" type="text/css">
<link href="<%= bugzillaUri %>/skins/custom/index.css" rel="stylesheet" type="text/css">
<link rel="shortcut icon" href="<%= bugzillaUri %>/images/favicon.ico">
<style type="text/css">
body {
	background: #FFFFFF;
	padding: 0;
}

td {
	padding-right: 5px;
	min-width: 175px;
}

th {
	padding-right: 5px;
	text-align: right;
}
</style>
</head>
<body>
<div id="bugzilla-body">

<h2>Lab 4 not implemented yet</h2>	

<%-- LAB 4 Uncomment to create the small preview html document 

<table class="edit_form">
	<tr>
		<th>Status:</th>
		<td><%= changeRequest.getStatus() %></td>
		<th>Product:</th>
		<td><%= changeRequest.getProduct() %></td>
	</tr>
	
	<tr>
		<th>Assignee:</th>
		<td><%= assignee %></td>
		<th>Component:</th>
		<td><%= changeRequest.getComponent() %></td>
	</tr>
	
	<tr>
		<th>Priority:</th>
		<td><%= changeRequest.getPriority() %></td>
		<th>Version:</th>
		<td><%= changeRequest.getVersion() %></td>
	</tr>
	
	<tr>
		<th>Reported:</th>
		<td><%= created %></td>
		<th>Modified:</th>
		<td><%= modified %></td>
	</tr>
</table>

--%>

</div>
</body>
</html>