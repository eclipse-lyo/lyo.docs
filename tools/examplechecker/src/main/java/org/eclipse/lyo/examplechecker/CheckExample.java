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
package org.eclipse.lyo.examplechecker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.lyo.tools.common.util.CheckForCommonMistakes;
import org.eclipse.lyo.tools.common.util.CheckRequiredArgs;
import org.eclipse.lyo.tools.common.util.ClassifiedErrorMessage;
import org.eclipse.lyo.tools.common.util.FileSuffixToJenaLanguage;
import org.eclipse.lyo.tools.common.util.FilenameParser;
import org.eclipse.lyo.tools.common.util.InvalidPredicateCheck;
import org.eclipse.lyo.tools.common.util.InvalidURILinkCheck;
import org.eclipse.lyo.tools.common.util.OSLCToolLogger;
import org.eclipse.lyo.tools.common.util.PotentialEnumCheck;
import org.eclipse.lyo.tools.common.util.ReadFileIntoNewModel;
import org.eclipse.lyo.tools.common.util.StatementCheck;
import org.eclipse.lyo.tools.common.util.UnusualPredicateandObjectCheck;

import com.hp.hpl.jena.rdf.model.Model;


public class CheckExample {

	private static final String ERROR_CAPTURED_EXCEPTION = "Error: Captured exception = ";
	private static final String UNABLE_TO_READ_THE_INPUT_STREAM = "Error: Unable to read the input example (stream) to be checked - no checking was done.";

	/**
	 * @param args the input file name or file URL
	 *
	 */
	public static void main(String[] args) {
		CheckRequiredArgs.exactlyN(args, 1);

		String infilename = args[0];
		FilenameParser fp = new FilenameParser(infilename);

		String[] filelist = fp.getFileNames();
		for (String i: filelist) {
			fp = new FilenameParser(i);
			String inFile = fp.getFullyQualifiedName();
			String in_FileNameSuffix = fp.getSuffix();
			String inModelLanguage = FileSuffixToJenaLanguage.toLang(in_FileNameSuffix);

			OSLCToolLogger.info("");
			OSLCToolLogger.info( "Input example:\t" + inFile + " format:" + inModelLanguage);
			Model instance = ReadFileIntoNewModel.read(inFile,inModelLanguage);
			if ( instance == null ) System.exit(1);
			CheckExample.doCheck(instance, null);
		}
	}

	/**
	 * @param example Jena Model to be checked
	 * @param hostsSuppressedForURLCheck the array list of host name that should be ignored
	 * during invalid URL check
	 *
	 */
	private static ArrayList<ClassifiedErrorMessage> doCheck(Model example, ArrayList<String> hostsSuppressedForURLCheck) {
		Map<String, String> nspm = example.getNsPrefixMap();
		ArrayList<ClassifiedErrorMessage> errorMsgList = null;

		errorMsgList = CheckForCommonMistakes.check(example);
		errorMsgList.addAll(StatementCheck.checkStatements(example, nspm));
		errorMsgList.addAll(InvalidPredicateCheck.CheckInvalidPredicate(example, nspm));
		errorMsgList.addAll(PotentialEnumCheck.CheckPotentialEnum(example, nspm));
		errorMsgList.addAll(UnusualPredicateandObjectCheck.CheckUnusualPredicateandObject(example, nspm));
		errorMsgList.addAll(InvalidURILinkCheck.CheckURILink(example, nspm, hostsSuppressedForURLCheck));

		return errorMsgList;
	}

	/**
	 * @param in Jena Model to be checked
	 * @param hostsSuppressedForURLCheck the array list of host name that should be ignored
	 * during invalid URL check
	 * @return An array list of error messages
	 *
	 */
	public static ArrayList<String> runExampleCheckerAndReturnString(Model in, ArrayList<String> hostsSuppressedForURLCheck) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ArrayList<String> outputList = new ArrayList<String>();
		try {
			errorMsgList = CheckExample.doCheck(in, hostsSuppressedForURLCheck);
			for (ClassifiedErrorMessage i: errorMsgList) {
				outputList.add(i.toString());
			}

		} catch (Exception e) {
			String errorMsg = ERROR_CAPTURED_EXCEPTION + e.getLocalizedMessage();
			OSLCToolLogger.error(errorMsg);
			outputList.add(errorMsg);
		}
		return outputList;
	}

	/**
	 * @param in input stream of the file to be checked
	 * @param inFormat the language of the data in the input stream
	 * @param hostsSuppressedForURLCheck the array list of host name that should be ignored
	 * during invalid URL check
	 * @return An array list of error messages
	 *
	 */
	public static ArrayList<String> runExampleCheckerAndReturnString(InputStream in, String inFormat, ArrayList<String> hostsSuppressedForURLCheck) {
		ArrayList<String> outputList = new ArrayList<String>();
		try {
			Model instance = ReadFileIntoNewModel.read(in,inFormat);
			if ( instance == null ) {
				OSLCToolLogger.error(UNABLE_TO_READ_THE_INPUT_STREAM);
				outputList.add(UNABLE_TO_READ_THE_INPUT_STREAM);
			} else {
				outputList = runExampleCheckerAndReturnString(instance, hostsSuppressedForURLCheck);
			}
		}catch (Exception e) {
			OSLCToolLogger.error(e.getLocalizedMessage());
			outputList.add(ERROR_CAPTURED_EXCEPTION + e.getLocalizedMessage());
		}
		return outputList;
	}

	/**
	 * @param in Jena model to be checked
	 * @param hostsSuppressedForURLCheck the array list of host name that should be ignored
	 * during invalid URL check
	 * @return An array list of classified error messages
	 *
	 */
	public static ArrayList<ClassifiedErrorMessage> runExampleChecker(Model in, ArrayList<String> hostsSuppressedForURLCheck) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;

		try {
			OSLCToolLogger.addLogOutput(out);
			errorMsgList = CheckExample.doCheck(in, hostsSuppressedForURLCheck);

		} catch (Exception e) {
			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "",
						ERROR_CAPTURED_EXCEPTION + e.getLocalizedMessage());
			errorMsgList.add(errMsg);
			OSLCToolLogger.error(errMsg.toString());

		}
		return errorMsgList;
	}

	/**
	 * @param in input stream of the file to be checked
	 * @param inFormat the language of the data in the input stream
	 * @param hostsSuppressedForURLCheck the array list of host name that should be ignored
	 * during invalid URL check
	 * @return An array list of classified error messages
	 *
	 */
	public static ArrayList<ClassifiedErrorMessage> runExampleChecker(InputStream in, String inFormat, ArrayList<String> hostsSuppressedForURLCheck) {
		ArrayList<ClassifiedErrorMessage> errorMsgList = new ArrayList<ClassifiedErrorMessage>();
		ClassifiedErrorMessage errMsg = null;
		try {
			Model instance = ReadFileIntoNewModel.read(in,inFormat);
			if ( instance == null ) {
				errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "",
						UNABLE_TO_READ_THE_INPUT_STREAM);
				errorMsgList.add(errMsg);
				OSLCToolLogger.error(errMsg.toString());
			} else {
				errorMsgList = runExampleChecker(instance, hostsSuppressedForURLCheck);
			}
		}catch (Exception e) {
			errMsg = new ClassifiedErrorMessage(ClassifiedErrorMessage.PRIORITY_ERROR, "",
			ERROR_CAPTURED_EXCEPTION + e.getLocalizedMessage());
			errorMsgList.add(errMsg);
			OSLCToolLogger.error(errMsg.toString());
		}
		return errorMsgList;
	}
}
