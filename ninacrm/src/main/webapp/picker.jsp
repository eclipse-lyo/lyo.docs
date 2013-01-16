<%--
 Copyright (c) 2011, 2012 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 
 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 Contributors:
 
    Dave Johnson	 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J Workshop
--%>
<html>
<head>
<title>Pick a Defect</title>
</head>
<body onLoad="javascript: windowNameProtocol()">
<div id="wrapper">

<h2>Pick a Defect</h2>


<span id="frame"></span>
<br /><br /><br />
<span id="status"></span>

<script type="text/javascript">
	var pickerURL = "http://localhost:8080/OSLC4JBugzilla/services/1/selector";
	var returnURL = "http://localhost:8181/ninacrm/picked.jsp";
    var frame = document.createElement('iframe');
    
	function windowNameProtocol() {
		
		status("Adding frame contents");

		var ie = window.navigator.userAgent.indexOf("MSIE");

		// Step #1: create iframe with fragment to indicate protocol
		// Step #2: set the iframe's window.name to indicate the Return URL
		if (ie > 0) {
			frame = document.createElement('<iframe name=\'' + returnURL + '\'>');
		} else {
			frame = document.createElement('iframe');
			frame.name = returnURL;
		}
		frame.src = pickerURL + '#oslc-core-windowName-1.0';
		frame.width = 450;
		frame.height = 300;

		displayFrame(frame);

		// Step #3: listen for onload events on the iframe
		if (ie > 0) {
			status("Add onload handler using attachEvent for IE");
			frame.attachEvent("onload", onFrameLoaded);
		} else {
			status("Add onload handler the normal way");
			frame.onload = onFrameLoaded;
		}
	}

	function onFrameLoaded() {
		status("Selection made. Location = " + frame.contentWindow.location);
		try { // May throw an exception if the frame's location is still a different origin
		
			// Step #4: when frame's location is equal to the Return URL 
			// then read response and return.
			if (frame.contentWindow.location == returnURL) {
				status("User made selection");
				var message = frame.contentWindow.name;
				destroyFrame(frame);
				handleMessage(message);
			} 
		     
		} catch (e) {
			// ignore: access exception when trying to access window name
		}
	}
	
	function displayFrame(frame) {
		document.getElementById("frame").appendChild(frame);
	}
	
	function destroyFrame(frame) {
	}
	
	function handleMessage(message) {
		status(message);
	}
	
	function status(msg) {
		document.getElementById("status").innerHTML = msg;
	}
	
</script></div>
</html>
