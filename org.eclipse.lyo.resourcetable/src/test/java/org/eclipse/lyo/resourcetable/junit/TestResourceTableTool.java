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

package org.eclipse.lyo.resourcetable.junit;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import org.eclipse.lyo.utilities.ReadFileIntoNewModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;

public class TestResourceTableTool {

	@Test
	public void testWithNoArgs() {
		
		try
		{
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(null);
		}
		catch(Exception e)
		{
			fail("No args generates error");
		}
		
	}
	
	@Test
	public void testWithInvalidArgs() {
		
		try
		{
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-xAAAA","-yBBBB"});
		}
		catch(Exception e)
		{
			fail("bad args generates error");
		}
		
	}

	@Test
	public void testWithMissingWikiFile() {
		
		try
		{
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-fxyz.txt"});
			fail("File not found error not thrown");
		}
		catch(Exception e)
		{
			if(!(e instanceof FileNotFoundException))
			{
				fail("File not found error not thrown");
			}
		}
		
	}

	@Test
	public void testWithMediaWiki_URIFormat() {
		
		try
		{
			String file = getFile("/MediaWikiSyntax/OSLC-Core-Actions/actionwikiwithuri.txt");
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-f" + file});
			//fail("File not found error not thrown");
			try {
				file = getFile("/MediaWikiSyntax/OSLC-Core-Actions/actionwikiwithuri.ttl");
				Model shapes = ReadFileIntoNewModel.read(file,"TURTLE");
				//System.out.println("File is syntactically correct");
				if(this.getPropertyOrderSeq(shapes) == null)
				{
					fail("No Seq defined");
				}
			} catch (Exception e) {
				fail("Failed parsing medial wiki with URI format");
			}
		}
		catch(Exception e)
		{
			fail("Failed parsing medial wiki with URI format");
		}
		
	}
	
	@Test
	public void testWithMediaWikiFormat() {
		
		try
		{
			String file = getFile("/MediaWikiSyntax/OSLC-Core-Actions/actionwikiwithuri.txt");
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-f" + file});
			//fail("File not found error not thrown");
			try {
				file = getFile("/MediaWikiSyntax/OSLC-Core-Actions/actionwikiwithuri.ttl");
				Model shapes = ReadFileIntoNewModel.read(file,"TURTLE");
				//System.out.println("File is syntactically correct");
				if(this.getPropertyOrderSeq(shapes) == null)
				{
					fail("No Seq defined");
				}
			} catch (Exception e) {
				fail("Failed parsing medial wiki with URI format");
			}
			
			try {
				file = getFile("/MediaWikiSyntax/OSLC-Core-Actions/actionwikiwithuri.ttl");
				org.eclipse.lyo.resourcetable.ResourceShape2WikiText.main(new String[]{"-f" + file});
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail("Failed ResourceShape2WikiText Twiki format");
			}

		}
		catch(Exception e)
		{
			fail("Failed parsing medial wiki with URI format");
		}
		
	}
	
	@Test
	public void testWithTWikiFormat() {
		
		try
		{
			String file = getFile("/TwikiSyntax/OSLC-Core-AppendixA-Common-Properties/Oslc-Comment.txt"); 
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-f" + file});
			//fail("File not found error not thrown");
			try {
				file = getFile("/TwikiSyntax/OSLC-Core-AppendixA-Common-Properties/Oslc-Comment.ttl"); 
				Model shapes = ReadFileIntoNewModel.read(file,"TURTLE");
				//System.out.println("File is syntactically correct");
				if(this.getPropertyOrderSeq(shapes) == null)
				{
					fail("No Seq defined");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				fail("Failed parsing medial wiki with URI format");
			}
			try {
				file = getFile("/TwikiSyntax/OSLC-Core-AppendixA-Common-Properties/Oslc-Comment.ttl"); 
				org.eclipse.lyo.resourcetable.ResourceShape2WikiText.main(new String[]{"-f" + file});
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail("Failed ResourceShape2WikiText Twiki format");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Failed parsing medial wiki with URI format");
		}
		
	}
	
	@Test
	public void testWithInvalidTableHeaderTWikiFormat() {
		
		try
		{
			String file = getFile("/TwikiSyntax/OSLC-Core-AppendixA-Common-Properties/DublinCore.txt"); 
			org.eclipse.lyo.resourcetable.WikiText2ResourceShape.main(new String[]{"-f" + file});
			fail("Did not catch invalid table header");
		}
		catch(Exception e)
		{
			if(!e.getMessage().startsWith("Invalid property table header"))
			{
				fail("Failed with unexpected error");
			}
		}
		
	}

	private String getFile(String relativePath) {
		return getClass().getResource(relativePath).getFile();
	}

	
	private Seq getPropertyOrderSeq(Model shape)
	{
		ResIterator itr = shape.listSubjects();
		while(itr.hasNext())
		{
			Resource res = itr.nextResource();
			System.out.println(res);
			if(res.getURI().endsWith("_sections_"))
			{
				return shape.getSeq(res);
			}
		}
		return null;
	}


}
