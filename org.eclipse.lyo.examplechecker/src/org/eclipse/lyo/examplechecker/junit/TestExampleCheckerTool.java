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
package org.eclipse.lyo.examplechecker.junit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

import org.eclipse.lyo.examplechecker.CheckExample;
import org.eclipse.lyo.utilities.ClassifiedErrorMessage;
import org.eclipse.lyo.utilities.OSLCToolLogger;
import org.eclipse.lyo.utilities.ReadFileIntoNewModel;
import org.junit.Before;
import org.junit.Test;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;


public class TestExampleCheckerTool {
	
	private ArrayList<String> fileList;
	private ArrayList<String> urisToSuppress = new ArrayList<String>(
    		Arrays.asList( new String[]{"127.0.0.1", "ibm.com", 
    				"HOST:PORT", "9.128.106.126"}));

	@Before
	public void setup() {	
		fileList = new ArrayList<String>();
		
		try {
		    URL resource = this.getClass().getResource("/org/eclipse/lyo/examplechecker/junit/testdata");
		    File filetmp = new File(resource.toURI());
		    String[] filenames=filetmp.list();
		    ArrayList<String> tmpList = new ArrayList<String>(Arrays.asList(filenames));
		    Collections.sort(tmpList);
			if (tmpList.size() > 0) {
			    for(String i:tmpList) {
			    	String shortname = i.substring(0, i.indexOf("."));
			    	if (i.endsWith("txt")) {
				    	fileList.add("testdata/" + shortname); 
			    	}
			    	
			    }
			}
		} catch (Exception e) {
			OSLCToolLogger.debug("getRources failed");
			OSLCToolLogger.error(e.getLocalizedMessage());
		}		
	}	
	
	@Test
	public void testAgaisntrunExampleCheckerwithInputStreamInput() {	
		OSLCToolLogger.info("================================================================================");
    	OSLCToolLogger.info("Testing API: CheckExample.runExampleChecker; " + "Interface: InputStream; ");
    	OSLCToolLogger.info("================================================================================");
		for(String fileName: fileList) {
			String inputFilePath = fileName + ".rdfxml";
			String outputFilePath = fileName + ".txt";
			Loader myloader = new Loader(inputFilePath, outputFilePath );
			String expectedOutput = myloader.getOutputData();
			String[] exps = expectedOutput.split("\n"); 
			ArrayList<ClassifiedErrorMessage> actList;
			ArrayList<String> expList = new ArrayList<String>(Arrays.asList(exps));		
	        boolean matched = false;
	        try {       	
	        	OSLCToolLogger.info("===== Input file: " + inputFilePath + " =====");
	        	InputStream in = myloader.getInputData();	        	
	        	actList = CheckExample.runExampleChecker(in, FileUtils.langXML, urisToSuppress);	        		        		
	        	ArrayList<String> concatedList = new ArrayList<String>();
	        	for (ClassifiedErrorMessage i:actList) {
	        		if (i.getSubject() != "") {
	        			concatedList.add(i.getSubject() + "\t" + i.getMessge());
	        		}
	        		else {
	        			concatedList.add(i.getMessge());
	        		}        		
	        	}
		        matched = compareOutput(expList, concatedList, inputFilePath);
		        assertTrue(matched);      
		        } catch (Exception e){
		        	OSLCToolLogger.error(e.getLocalizedMessage());
		        	assertTrue(matched);
		        } 
			}
        }
	    
	@Test
    public void testAgaisntrunExampleCheckerwithModelInput() {	
		OSLCToolLogger.info("================================================================================");
    	OSLCToolLogger.info("Testing API: CheckExample.runExampleChecker; " + "Interface: Model; ");
    	OSLCToolLogger.info("================================================================================");
    	for(String fileName: fileList) {
    		String inputFilePath = fileName + ".rdfxml";
    		String outputFilePath = fileName + ".txt";
			Loader myloader = new Loader(inputFilePath, outputFilePath );
			String expectedOutput = myloader.getOutputData();
			String[] exps = expectedOutput.split("\n"); 
			ArrayList<String> expList = new ArrayList<String>(Arrays.asList(exps));		
	        ArrayList<ClassifiedErrorMessage> actList;
	        ArrayList<String> concatedList;

	        boolean matched = false;
	        try {    
		        OSLCToolLogger.info("===== Input file: " + inputFilePath + " =====");
		        Loader myloader2 = new Loader(inputFilePath, outputFilePath );
		        InputStream in2 = myloader2.getInputData();
	            Model instance = ReadFileIntoNewModel.read(in2, FileUtils.langXML);
	        	actList = CheckExample.runExampleChecker(instance, urisToSuppress);
	            concatedList = new ArrayList<String>();
	        	for (ClassifiedErrorMessage i:actList) {
	        		if (i.getSubject() != "") {
	        			concatedList.add(i.getSubject() + "\t" + i.getMessge());
	        		}
	        		else {
	        			concatedList.add(i.getMessge());
	        		}        		
	        	}
		        matched = compareOutput(expList, concatedList, inputFilePath);
		        assertTrue(matched);	        
	        } catch (Exception e){
	        	OSLCToolLogger.error(e.getLocalizedMessage());
	        	assertTrue(matched);
	        } 
    	}
	}
	
	@Test
	public void testAgainstStringInterface() {
		OSLCToolLogger.info("================================================================================");
    	OSLCToolLogger.info("Testing API: CheckExample.runExampleCheckerAndReturnString; " + "Interface: InputStream; ");
    	OSLCToolLogger.info("================================================================================");
		for(String fileName: fileList) {
			String inputFilePath = fileName + ".rdfxml";
			String outputFilePath = fileName + ".txt";
			Loader myloader = new Loader(inputFilePath, outputFilePath );					
	        ArrayList<String> actList = new ArrayList<String>();
			String expectedOutput = myloader.getOutputData();
			String[] exps = expectedOutput.split("\n"); 
			ArrayList<String> expList = new ArrayList<String>(Arrays.asList(exps));	
	        boolean matched = false;
	        try {     
	        	OSLCToolLogger.info("===== Input file: " + inputFilePath + " =====");
	        	InputStream in = myloader.getInputData();
	        	actList = CheckExample.runExampleCheckerAndReturnString(in, FileUtils.langXML, null);
	        	matched = compareOutput(expList, actList, inputFilePath);
		        assertTrue(matched);	  
	        }catch (Exception e){
	        	OSLCToolLogger.error(e.getLocalizedMessage());
	        	assertTrue(matched);
	        }
		}
	}
	
	private static boolean compareOutput(ArrayList<String> expList, ArrayList<String> actList, String inputFilePath) {
		// Sort the output
		Collections.sort(expList);
		Collections.sort(actList);

		String [] exps = expList.toArray(new String[expList.size()]);
		String [] acts = actList.toArray(new String[actList.size()]);
		
		boolean matched = true;
		int len = exps.length;
		
		if (exps.length > acts.length) {
			len = acts.length;
			int extralen = exps.length - acts.length;
			OSLCToolLogger.error( "Expected number of entry: " + exps.length 
					+ " Actual number of entry: " + acts.length );
			for (int i=0; i<extralen; i++) {
				OSLCToolLogger.error( "Expected:[" + exps[i+len].replace("\n", "").replace("\r", "") + "]" );
				OSLCToolLogger.error( "Actual:[]");
			}
			matched = false;
		} 
		else if (exps.length < acts.length) {
			len = exps.length;
			int extralen = acts.length - exps.length;
			OSLCToolLogger.error( inputFilePath + ": Expected number of entry: " + exps.length 
					+ " Actual number of entry: " + acts.length );
			for (int i=0; i<extralen; i++) {
				OSLCToolLogger.error( "Expected:[]" );
				OSLCToolLogger.error( "Actual:[" + acts[i+len].replace("\n", "").replace("\r", "") + "]");
			}
			matched = false;
		}			 
		else {
			for (int i=0; i<len; i++) {
				String expTmp = exps[i].replace(" ", "").replace("\n", "").replace("\r", "").replace("\t","");
				String actTmp = acts[i].replace(" ", "").replace("\n", "").replace("\r", "").replace("\t","");
				// Since the error messages on the same line are sorted, we don't need to sort during comparison.
				if (!expTmp.equals(actTmp)) {	
					OSLCToolLogger.error( "Expected:[" + expTmp + "]" );
					OSLCToolLogger.error( "Actual:["+ actTmp + "]");
					matched = false;
				}
			}
		}
		return matched;
	}

	
}
