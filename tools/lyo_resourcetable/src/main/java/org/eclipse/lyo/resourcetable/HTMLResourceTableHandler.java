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
* Anamitra Bhattacharyya - initial API and implementation
*******************************************************************************/
package org.eclipse.lyo.resourcetable;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.hpl.jena.rdf.model.Resource;

public class HTMLResourceTableHandler extends ResourceTableHandler {

	private Document htmlDoc;
	
	public HTMLResourceTableHandler(Map<String, String> props) throws ParserConfigurationException {
		super(props);
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		htmlDoc = documentBuilder.newDocument();
	}

	@Override
	public byte[] generateResourceTable() throws Exception {
		List<Object> rows = this.getResourceTableRows();
		Element tableElement = this.createTable();
		tableElement.appendChild(this.createTableHeader());
		Element bodyElement = null;
		
		for(Object row : rows)
		{
			if(row instanceof ResourceTableRow)
			{
				if(bodyElement == null)
				{
					bodyElement = this.createBodyElement();
					tableElement.appendChild(bodyElement);
				}
				bodyElement.appendChild(this.createResourcePropertyElement((ResourceTableRow)row));
			}
			else
			{
				tableElement.appendChild(this.createSectionElement(((ResourceTableSection)row).getTitle()));
				bodyElement = this.createBodyElement();
				tableElement.appendChild(bodyElement);
			}
		}
		TransformerFactory tFactory =
			    TransformerFactory.newInstance();
			    Transformer transformer = 
			    tFactory.newTransformer();
		DOMSource source = new DOMSource(htmlDoc);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
	    StreamResult result = new StreamResult(bo);
	    transformer.transform(source, result);
		return bo.toByteArray();
	}
	
	private Element createResourcePropertyElement(ResourceTableRow row)
	{
		Element tr = htmlDoc.createElement("tr");
		String shortName = row.getPropertyShortName();
		
		//property name
		String uri = null;
		String propertyQName = row.getInvalidResourceURIMsg();
		Element propQElement = null;
		if(row.getPropertyRes() != null)
		{
			uri = row.getPropertyRes().getURI();
			//propertyQName = uriMode?"[`" + shortName + "`](" + uri + ")":"`"+shortName+"`";
			//propertyQName = "<a href=\""+uri+"\">"+shortName+"</a>";
			propQElement = this.createHrefElement(uri, shortName);
			tr.appendChild(propQElement);
		}
		else
		{
			tr.appendChild(this.createDataElement(propertyQName));
		}
		
		
		//Occurs
		Resource occurs = row.getOccurs();
		String propertyOccurs = row.getInvalidOccursURIMsg();
		Element propertyOccursElement = null;
		if(occurs != null)
		{
			uri = occurs.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			//propertyOccurs = uriMode?"[" + ncName + "](" + uri + ")":ncName;
			//propertyOccurs = "<a href=\""+uri+"\">"+ncName+"</a>";
			propertyOccursElement = this.createHrefElement(uri, ncName);
			tr.appendChild(propertyOccursElement);
		}
		else
		{
			tr.appendChild(this.createDataElement(propertyOccurs));
		}
		
		//wikiRowText += "| " + propertyOccurs + " ";
		
		//Reaonly
		Boolean readOnly = row.getReadOnly();
		String propertyReadOnly = row.getInvalidReadonlyMsg()==null?"Unspecified":row.getInvalidReadonlyMsg();
		if(readOnly != null)
		{
			propertyReadOnly = readOnly.toString();
		}
		tr.appendChild(this.createDataElement(propertyReadOnly));
		
		Resource valType = row.getValTypeRes();
		String propertyValueType = row.getInvalidValueTypeMsg();
		Element propertyValueTypeElement = null;
		if(valType != null)
		{
			uri = valType.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			//propertyValueType = uriMode?"[" + this.upperCaseFirstChar(ncName) + "](" + uri + ")":ncName;
			//propertyValueType = "<a href=\""+uri+"\">"+this.upperCaseFirstChar(ncName)+"</a>";
			propertyValueTypeElement = this.createHrefElement(uri, this.upperCaseFirstChar(ncName));
			tr.appendChild(propertyValueTypeElement);
		}
		else
		{
			tr.appendChild(this.createDataElement(propertyValueType));
		}
		
		//wikiRowText += "| " + propertyValueType + " ";

		Resource representation = row.getRepresentationRes();
		String propertyRep = row.getInvalidRepresentationURIMsg()==null?"N/A":row.getInvalidRepresentationURIMsg();
		Element propertyRepElement =  null;
		if(representation != null)
		{
			uri = representation.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			//propertyRep = uriMode?"[" + ncName + "](" + uri + ")":ncName;
			//propertyRep = "<a href=\""+uri+"\">"+ncName+"</a>";
			propertyRepElement = this.createHrefElement(uri, ncName);
			tr.appendChild(propertyRepElement);
		}
		else
		{
			tr.appendChild(this.createDataElement(propertyRep));
		}
		
		//wikiRowText += "| " + propertyRep + " ";

		Resource range = row.getRangeRes();
		String propertyRange = row.getInvalidRangeURIMsg()==null?"N/A":row.getInvalidRangeURIMsg();
		Element propertyRangeElement = null;
		if(range != null)
		{
			uri = range.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			propertyRangeElement = this.createHrefElement(uri, ncName);
			
			tr.appendChild(propertyRangeElement);
		}
		else
		{
			tr.appendChild(this.createDataElement(propertyRange));
		}
		
		String propertyDesc = row.getPropDesc();
		tr.appendChild(this.createDataElement(propertyDesc));
		return tr;
	}


	@Override
	public String formatURI2Text(String ncName, String uri) {
		return null;
	}
	
	protected Element createTable() throws ParserConfigurationException{
		Element html = htmlDoc.createElement("html");
		htmlDoc.appendChild(html);
		Element body = htmlDoc.createElement("body");
		//body.appendChild(document.createTextNode("James"));
		html.appendChild(body);
		Element table = htmlDoc.createElement("table");
		body.appendChild(table);
		Attr attr = htmlDoc.createAttribute("border");
		attr.setValue("1");
		table.setAttributeNode(attr);
		return table;
	}
	
	private Element createTableHeader()
	{
		Element thead = htmlDoc.createElement("THEAD");
		Element tr = htmlDoc.createElement("tr");
		thead.appendChild(tr);
		tr.appendChild(this.createHeaderElement("Prefixed Name"));
		tr.appendChild(this.createHeaderElement("Occurs"));
		tr.appendChild(this.createHeaderElement("Read-only"));
		tr.appendChild(this.createHeaderElement("Value-type"));
		tr.appendChild(this.createHeaderElement("Representation"));
		tr.appendChild(this.createHeaderElement("Range"));
		tr.appendChild(this.createHeaderElement("Description"));
		return thead;
	}
	
	private Element createHrefElement(String uri, String text)
	{
		Element td = htmlDoc.createElement("td");

		Element a = htmlDoc.createElement("a");
		td.appendChild(a);
		Attr attr = htmlDoc.createAttribute("href");
		attr.setValue(uri);
		a.setAttributeNode(attr);
		a.appendChild(htmlDoc.createTextNode(text));
		return td;
	}

	
	private Element createHeaderElement(String header)
	{
		Element th = htmlDoc.createElement("th");
		th.appendChild(htmlDoc.createTextNode(header));
		return th;
	}
	
	private Element createSectionElement(String section)
	{
		Element td = htmlDoc.createElement("td");
		td.appendChild(htmlDoc.createTextNode(section));
		Element tr = htmlDoc.createElement("tr");
		tr.appendChild(td);
		Attr attr = htmlDoc.createAttribute("colspan");
		attr.setValue("7");
		td.setAttributeNode(attr);
		return tr;
	}
	
	private Element createBodyElement()
	{
		return htmlDoc.createElement("tbody");
	}
	
	private Element createDataElement(String data)
	{
		Element td = htmlDoc.createElement("td");
		td.appendChild(htmlDoc.createTextNode(data));
		return td;
	}
}
