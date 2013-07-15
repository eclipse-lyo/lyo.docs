/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
function search(baseUrl){
	var ie = window.navigator.userAgent.indexOf("MSIE");
	list = document.getElementById("results");
	list.options.length = 0;
	var searchMessage = document.getElementById('searchMessage');
	var loadingMessage = document.getElementById('loadingMessage');
	xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			// populate results
			txt = xmlhttp.responseText;
			resp = eval('(' + txt + ')');
			for( var i=0; i<resp.results.length; i=i+1 ) {
				var item=document.createElement('option');
				item.text = resp.results[i].title;
				item.value = resp.results[i].resource;
				if (ie > 0) {
	 				list.add(item); 
				} else {
	 				list.add(item, null); 
				}
			}
			
			searchMessage.style.display = 'block';
			loadingMessage.style.display = 'none';
		}
	};
	terms = document.getElementById("searchTerms").value;
	xmlhttp.open("GET", baseUrl + "?terms=" + encodeURIComponent(terms), true);	
	searchMessage.style.display = 'none';
	loadingMessage.style.display = 'block';
	xmlhttp.send();
}

function create(baseUrl){
	var form = document.getElementById("Create");
	xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && (xmlhttp.status==201)) {
			txt = xmlhttp.responseText;
			resp = eval('(' + txt + ')');
			// Send response to listener
			sendResponse(resp.title, resp.resource);
		}
	};
 	var postData=""; 
 	if (form.component) {
 		postData += "&component="+encodeURIComponent(form.component.value);
 	}
 	if (form.summary) {
 		postData += "&summary="+encodeURIComponent(form.summary.value);
 	}
 	if (form.version) {
 		postData += "&version="+encodeURIComponent(form.version.value);
 	}
 	if (form.op_sys) {
 		postData += "&op_sys="+encodeURIComponent(form.op_sys.value);
	} 
	if (form.platform) {
		postData += "&platform="+encodeURIComponent(form.platform.value);	
	}
	if (form.description) {
		postData += "&description="+encodeURIComponent(form.description.value);	
	}
	xmlhttp.open("POST", baseUrl, true);
	xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlhttp.setRequestHeader("Content-length",postData.length);
	xmlhttp.send(postData);
}

function select(){
	list = document.getElementById("results");
	if( list.length>0 && list.selectedIndex >= 0 ) {
		option = list.options[list.selectedIndex];
		sendResponse(option.text, option.value);
	}
}

function sendResponse(label, url) {
	var oslcResponse = 'oslc-response:{ "oslc:results": [ ' +  
		' { "oslc:label" : "' + label + '", "rdf:resource" : "' + url + '"} ' + 
	' ] }';
	
	if (window.location.hash == '#oslc-core-windowName-1.0') {       
  	  // Window Name protocol in use
        respondWithWindowName(oslcResponse);
	} else if (window.location.hash == '#oslc-core-postMessage-1.0') {
    	// Post Message protocol in use
		respondWithPostMessage(oslcResponse);
	} 
}

function sendCancelResponse() {
	var oslcResponse = 'oslc-response:{ "oslc:results": [ ]}';
	
	if (window.location.hash == '#oslc-core-windowName-1.0') {       
  	  // Window Name protocol in use
        respondWithWindowName(oslcResponse);
	} else if (window.location.hash == '#oslc-core-postMessage-1.0') {
    	// Post Message protocol in use
		respondWithPostMessage(oslcResponse);
	} 
}


function respondWithWindowName(/*string*/ response) {
   // Step #2: read the return URL
   var returnURL = window.name;

   // Step #4: send the response via the window.name variable
   window.name = response;

   // Step #5: indicate that user has responded
   window.location.href = returnURL;
   
}

function respondWithPostMessage(/*string*/ response) {
	if( window.parent != null ) {
		window.parent.postMessage(response, "*");
	} else {
		window.postMessage(response, "*");
	}
}

function cancel(){
	sendCancelResponse();
}
