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
* Anamitra Bhattacharyya - initial API and implementation
*******************************************************************************/
package org.eclipse.lyo.resourcetable;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Implements media-wiki resource table format.
 * @author anamitra
 *
 */
public class MediaWikiResourceTableHandler extends ResourceTableHandler 
{
	private boolean uriMode = true;
	public MediaWikiResourceTableHandler(Map<String,String> props)
	{
		super(props);
		uriMode = this.getBooleanProperty("uriMode", true);
	}

	@Override
	public byte[] generateResourceTable() throws Exception 
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bo);
		this.writeWikiResourceHeader(pw);
		this.writeTableHeader(pw);
		List<Object> rows = this.getResourceTableRows();
		for(Object row : rows)
		{
			if(row instanceof ResourceTableRow)
			{
				this.writeTableRow((ResourceTableRow)row, pw);
			}
			else
			{
				this.writeTableSection((ResourceTableSection)row, pw);
			}
		}
		pw.flush();
		pw.close();
		return bo.toByteArray();
	}
	
	private void writeTableRow(ResourceTableRow row, PrintWriter pw)
	{
		String shortName = row.getPropertyShortName();
		String wikiRowText = "";
		
		//property name
		String uri = null;
		String propertyQName = row.getInvalidResourceURIMsg();
		if(row.getPropertyRes() != null)
		{
			uri = row.getPropertyRes().getURI();
			propertyQName = uriMode?"[`" + shortName + "`](" + uri + ")":"`"+shortName+"`";
		}
		wikiRowText += "| " +  propertyQName + " ";
		
		//Occurs
		Resource occurs = row.getOccurs();
		String propertyOccurs = row.getInvalidOccursURIMsg();
		if(occurs != null)
		{
			uri = occurs.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			propertyOccurs = uriMode?"[" + ncName + "](" + uri + ")":ncName;
		}
		wikiRowText += "| " + propertyOccurs + " ";
		
		//Reaonly
		Boolean readOnly = row.getReadOnly();
		String propertyReadOnly = row.getInvalidReadonlyMsg()==null?"Unspecified":row.getInvalidReadonlyMsg();
		if(readOnly != null)
		{
			propertyReadOnly = readOnly.toString();
		}
		wikiRowText += "| " + propertyReadOnly + " ";
		
		Resource valType = row.getValTypeRes();
		String propertyValueType = row.getInvalidValueTypeMsg();
		if(valType != null)
		{
			uri = valType.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			propertyValueType = uriMode?"[" + this.upperCaseFirstChar(ncName) + "](" + uri + ")":ncName;
		}
		wikiRowText += "| " + propertyValueType + " ";

		Resource representation = row.getRepresentationRes();
		String propertyRep = row.getInvalidRepresentationURIMsg()==null?"N/A":row.getInvalidRepresentationURIMsg();
		if(representation != null)
		{
			uri = representation.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			propertyRep = uriMode?"[" + ncName + "](" + uri + ")":ncName;
		}
		wikiRowText += "| " + propertyRep + " ";

		Resource range = row.getRangeRes();
		String propertyRange = row.getInvalidRangeURIMsg()==null?"N/A":row.getInvalidRangeURIMsg();
		if(range != null)
		{
			uri = range.getURI();
			String ncName = uri.substring(uri.indexOf('#')+1);
			propertyRange = uriMode?"[" + ncName + "](" + uri + ")":ncName;
		}
		wikiRowText += "| " + propertyRange + " ";
		
		String propertyDesc = row.getPropDesc();
		wikiRowText += "| " + propertyDesc + " ";
		
		pw.println(wikiRowText);

	}
	
	private void writeTableSection(ResourceTableSection row, PrintWriter pw)
	{
		pw.println("| "+row.getTitle()+"  |||||||");
	}

	private void writeTableHeader(PrintWriter pw)
	{
		pw.println("| **Prefixed Name** | **Occurs** | **Read-only** | **Value-type** | **Representation** | **Range** | **Description** ");
		pw.println("| ----------------- | ---------- | ------------- | -------------- | ------------------ | --------- | ----------------- ");
	}
	
	private String writeWikiResourceHeader(PrintWriter pw)
	{
		String uri = "<#Resource_Name_Missing>";
		String resDesc = this.getResourceDescription();
		if ( resDesc == null || "".equals(resDesc) ) resDesc = "OSLC Resource (defaulted - no description found in input)";
		String localName = "Resource_Name_Missing";
		Resource resType = this.getResourceType();
		if(resType != null)
		{
			uri = resType.getURI();
			localName = resType.getLocalName();
		}

		pw.println("### Resource: "+localName);
		pw.println();//Space line
		pw.println("* **Name:** `"+localName+"`");
		pw.println("* **Description:** "+resDesc);
		pw.println("* **Type URI:** [`"+uri+"`]("+uri+")");
		pw.println();//Space line
		return localName;
	}

	@Override
	public String formatURI2Text(String ncName, String uri) 
	{
		return uriMode?"[`" + ncName + "`](" + uri + ") ":ncName;
	}

}
