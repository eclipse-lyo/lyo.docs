<!DOCTYPE html>
<%--
 Copyright (c) 2015 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.

 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.

 Contributors:

    Sam Padgett 	- initial API and implementation
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="org.eclipse.lyo.oslc4j.bugzilla.BugzillaManager" %>
<%
final String bugzillaUri = BugzillaManager.getBugzillaUri();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
        <title>Bugzilla OSLC Adapter</title>
        <link href="<%= bugzillaUri %>/skins/standard/global.css" rel="stylesheet" type="text/css">
        <link href="<%= bugzillaUri %>/skins/standard/index.css" rel="stylesheet" type="text/css">
        <link href="<%= bugzillaUri %>/skins/standard/global.css" rel="alternate stylesheet" title="Classic" type="text/css">
        <link href="<%= bugzillaUri %>/skins/standard/index.css" rel="alternate stylesheet" title="Classic" type="text/css">
        <link href="<%= bugzillaUri %>/skins/contrib/Dusk/global.css" rel="stylesheet" title="Dusk" type="text/css">
        <link href="<%= bugzillaUri %>/skins/contrib/Dusk/index.css" rel="stylesheet" title="Dusk" type="text/css">
        <link href="<%= bugzillaUri %>/skins/custom/global.css" rel="stylesheet" type="text/css">
        <link href="<%= bugzillaUri %>/skins/custom/index.css" rel="stylesheet" type="text/css">
        <link rel="shortcut icon" href="<%= bugzillaUri %>/images/favicon.ico">
    </head>
    <body>
        <div id="header">
            <div id="banner"></div>
            <table border="0" cellspacing="0" cellpadding="0" id="titles">
                <tr>
                    <td id="title">
                        <p>
                            Bugzilla OSLC Adapter
                        </p>
                    </td>
                    <td id="information">
                        <p class="header_addl_info">
                            version 0.1
                        </p>
                    </td>
                </tr>
            </table>
        </div>

        <div id="bugzilla-body">
            <div id="page-index">

                <img src="images/resources/bugzilla.gif" alt="icon" width="80" height="80" />

                <h1>Bugzilla OSLC Adapter</h1>

                <p>Welcome to the Bugzilla OSLC Adapter! It implements OSLC
                    Change Management for Bugzilla using the
                    <a href="http://eclipse.org/lyo/">Eclipse Lyo</a>
                    OSLC4J SDK. Learn more
                    <a href="https://wiki.eclipse.org/Lyo/BuildOSLC4JBugzilla">here</a>.</p>

                <p>Make sure you've registered with
                    <a href="<%= bugzillaUri %>">Bugzilla</a> before
                    continuing. You'll need to log in with your Bugzilla
                    credentials to access the OSLC provider catalog.</p>

                <table style="margin: 5px;">
                    <tr>
                        <td><b>Bugzilla</b>:</td>
                        <td><a href="<%= bugzillaUri %>"><%= bugzillaUri %></a></td>
                    </tr>
                    <tr>
                        <td><b>Provider Catalog</b>:</td>
                        <td><a href="services/catalog/singleton">services/catalog/singleton</a></td>
                    </tr>
                    <tr>
                        <td><b>Adapter Publisher</b>:</td>
                        <td>Eclipse Lyo</td>
                    </tr>
                    <tr>
                        <td><b>Adapter Identity</b>:</td>
                        <td>org.eclipse.lyo.oslc4j.bugzilla.test</td>
                    </tr>
                </table>

            </div>

            <div id="footer">
                <div class="intro"></div>
                <div class="outro">
                    <div style="margin: 0 1em 1em 1em; line-height: 1.6em; text-align: left">
                        <b>OSLC Tools Adapter Server 0.1</b> brought to you by <a href="http://eclipse.org/lyo">Eclipse Lyo</a><br>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
