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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Base resource table handler. Subclasses implement specific resource table format generation.
 * @author anamitra
 *
 */
public abstract class ResourceTableHandler 
{
	private static Map<String,ResourceTableHandler> handlers = new HashMap<String,ResourceTableHandler>();
	static 
	{
		try
		{
			Properties p = new Properties();
			InputStream inStream = ResourceTableHandler.class.getResourceAsStream("/org/eclipse/lyo/resourcetable/config/wikihandler.properties");
			p.load(inStream);
			Set<Map.Entry<Object,Object>> entrySet = p.entrySet();
			for(Map.Entry<Object,Object> entry : entrySet)
			{
				String handler = (String)entry.getValue();
				StringTokenizer strtk = new StringTokenizer(handler,",");
				String className = strtk.nextToken();
				Map<String,String> props = new HashMap<String,String>();
				while(strtk.hasMoreTokens())
				{
					StringTokenizer strtk2 = new StringTokenizer(strtk.nextToken(),"=");
					props.put(strtk2.nextToken(), strtk2.nextToken());
				}
				
				Constructor<ResourceTableHandler> c = (Constructor<ResourceTableHandler>) Class.forName(className).getConstructor(Map.class);
				ResourceTableHandler wh = c.newInstance(props);
				handlers.put(entry.getKey().toString(), wh);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static final ResourceTableHandler getInstance(String wikiType)
	{
		return handlers.get(wikiType);
	}
	
	public static final ResourceTableHandler getDefaultHandler()
	{
		return handlers.get("default");
	}

	
	private Map<String,String> props;
	private Resource resourceType;
	private String resourceDescription;
	private List<Object> resourceTableRows = new ArrayList<Object>();
	private Model shape;
	
	public boolean getBooleanProperty(String propName, boolean def)
	{
		String propVal = props.get(propName);
		if(propVal == null) return def;
		return Boolean.parseBoolean(propVal);
	}
	
	public static Map<String, ResourceTableHandler> getHandlers() {
		return handlers;
	}

	public Map<String, String> getProps() {
		return props;
	}

	public Resource getResourceType() {
		return resourceType;
	}

	public String getResourceDescription() {
		return resourceDescription;
	}

	public List<Object> getResourceTableRows() {
		return resourceTableRows;
	}

	public Model getShape() {
		return shape;
	}

	public ResourceTableHandler(Map<String,String> props)
	{
		this.props = props;
	}
	
	public void setResourceType(Resource resType)
	{
		this.resourceType = resType;
	}
	
	public void setModel(Model shape)
	{
		this.shape = shape;
	}
	
	public void setResourceDescription(String resDesc)
	{
		this.resourceDescription = resDesc;
	}
	
	public void addResourceTableRow(ResourceTableRow tableRow)
	{
		resourceTableRows.add(tableRow);
	}
	
	public void addResourceTableSection(ResourceTableSection tableSection)
	{
		resourceTableRows.add(tableSection);
	}
	
	public String upperCaseFirstChar(String ncName)
	{
		return String.valueOf(ncName.charAt(0)).toUpperCase()+ncName.substring(1);
	}

	
	public abstract byte[] generateResourceTable() throws Exception;
	
	public abstract String formatURI2Text(String ncName, String uri);

	

	public static class ResourceTableSection
	{
		private String title;
		public ResourceTableSection(String title)
		{
			this.title = title;
		}
		
		public String getTitle()
		{
			return title;
		}
	}
	
	public static class ResourceTableRow
	{
		private Resource propertyRes;
		private Resource occurs;
		private Boolean readOnly;
		private Resource valTypeRes;
		private Resource representationRes;
		private Resource rangeRes;
		private String propDesc;
		private Resource suggestedRange;
		private String invalidOccursURIMsg;
		private String invalidRepresentationURIMsg;
		private String invalidResourceURIMsg;
		private String invalidReadonlyMsg;
		private String invalidValueTypeMsg;
		private String invalidRangeURIMsg;
		private Model shape;

		
		public ResourceTableRow(Model shape)
		{
			this.shape = shape;
		}
		
		public Model getShape()
		{
			return shape;
		}

		public String getPropDesc() {
			return propDesc;
		}

		public void setPropDesc(String propDesc) {
			this.propDesc = propDesc;
		}

		public Resource getSuggestedRange() {
			return suggestedRange;
		}

		public void setSuggestedRange(Resource suggestedRange) {
			this.suggestedRange = suggestedRange;
		}

		public String getInvalidRangeURIMsg() {
			return invalidRangeURIMsg;
		}

		public void setInvalidRangeURIMsg(String invalidRangeURIMsg) {
			this.invalidRangeURIMsg = invalidRangeURIMsg;
		}

		public String getInvalidValueTypeMsg() {
			return invalidValueTypeMsg;
		}

		public void setInvalidValueTypeMsg(String invalidValueTypeMsg) {
			this.invalidValueTypeMsg = invalidValueTypeMsg;
		}

		public String getInvalidReadonlyMsg() {
			return invalidReadonlyMsg;
		}

		public void setInvalidReadonlyMsg(String invalidReadonlyMsg) {
			this.invalidReadonlyMsg = invalidReadonlyMsg;
		}

		public String getInvalidResourceURIMsg() {
			return invalidResourceURIMsg;
		}

		public void setInvalidResourceURIMsg(String invalidResourceURIMsg) {
			this.invalidResourceURIMsg = invalidResourceURIMsg;
		}

		public String getInvalidRepresentationURIMsg() {
			return invalidRepresentationURIMsg;
		}

		public void setInvalidRepresentationURIMsg(String invalidRepresentationURIMsg) {
			this.invalidRepresentationURIMsg = invalidRepresentationURIMsg;
		}

		public String getInvalidOccursURIMsg() {
			return invalidOccursURIMsg;
		}

		public void setInvalidOccursURIMsg(String invalidOccursURIMsg) {
			this.invalidOccursURIMsg = invalidOccursURIMsg;
		}

		public Resource getPropertyRes() {
			return propertyRes;
		}
		
		public void setPropertyRes(Resource res)
		{
			this.propertyRes = res;
		}
		
		public Resource getOccurs() {
			return occurs;
		}
		
		public String getPropertyShortName()
		{
			String uri = propertyRes.getURI();
			return shape.shortForm(uri);
		}

		public Boolean getReadOnly() {
			return readOnly;
		}

		public Resource getValTypeRes() {
			return valTypeRes;
		}

		public Resource getRepresentationRes() {
			return representationRes;
		}

		public Resource getRangeRes() {
			return rangeRes;
		}
		
		public void setOccurs(Resource occurs)
		{
			this.occurs = occurs;
		}
		
		public void setValueType(Resource valTypeRes)
		{
			this.valTypeRes = valTypeRes;
		}
		
		public void setRepresentation(Resource representationRes)
		{
			this.representationRes = representationRes;
		}
		
		public void setRange(Resource rangeRes)
		{
			this.rangeRes = rangeRes;
		}
		
		public void setReadOnly(Boolean readOnly)
		{
			this.readOnly = readOnly;
		}
		
		public Resource getPropertyResource()
		{
			return propertyRes;
		}
	}
}
