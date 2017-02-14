/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.TEIMapper.TEIImporterReader;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

public class ReaderTest {
	String filePath = new File("").getAbsolutePath();
	
	
	private TEIImporterReader fixture = null;

	public TEIImporterReader getFixture() {
		return fixture;
	}

	public void setFixture(TEIImporterReader fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		setFixture(new TEIImporterReader());
		filePath = filePath.concat("/src/test/resources/");
	}

	@Test
	public void initialize() throws 
			FileNotFoundException, UnsupportedEncodingException {
		
		fixture.setSUB_TOKENIZATION();


		File outFile = new File (filePath.concat("HamaTest/Janis.xml"));
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		assertNotNull(getFixture().getsDocGraph());
	}
	
	@Test
	public void timeline(){
		
		File outFile = new File (filePath.concat("HamaTest/Janis.xml"));
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		
		assertEquals(2,getFixture().getsDocGraph().getSTextualDSs().size());
		Double temp = fixture.getTimes().get("T0");
		assertEquals((Double)0.0,temp);
		
	}

	@Test
	public void rufus(){
		
		File outFile = new File (filePath.concat("HamaTest/rufus.xml"));
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		
		assertEquals(4,getFixture().getsDocGraph().getSTextualDSs().size());
		Double temp = fixture.getTimes().get("T0");
		//assertEquals((Double)0.0,temp);
		System.out.println(getFixture().getsDocGraph().getSTextualDSs().get(0));
		System.out.println(getFixture().getsDocGraph().getSTextualDSs().get(1));
	}
	
	protected void readXMLResource(DefaultHandler2 contentHandler,
			URI documentLocation) {
		if (documentLocation == null)
			throw new RuntimeException(
					"Cannot load a xml-resource, because the given uri to locate file is null.");

		File exmaraldaFile = new File(documentLocation.toFileString());
		if (!exmaraldaFile.exists())
			throw new RuntimeException(
					"Cannot load a xml-resource, because the file does not exist: "
							+ exmaraldaFile);

		if (!exmaraldaFile.canRead())
			throw new RuntimeException(
					"Cannot load a xml-resource, because the file can not be read: "
							+ exmaraldaFile);

		SAXParser parser;
		XMLReader xmlReader;

		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			parser = factory.newSAXParser();
			xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(contentHandler);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Cannot load a xml-resource '"
					+ exmaraldaFile.getAbsolutePath() + "'.", e);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load a xml-resource '"
					+ exmaraldaFile.getAbsolutePath() + "'.", e);
		}
		try {
			InputStream inputStream = new FileInputStream(exmaraldaFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			xmlReader.parse(is);
		} catch (SAXException e) {

			try {
				parser = factory.newSAXParser();
				xmlReader = parser.getXMLReader();
				xmlReader.setContentHandler(contentHandler);
				xmlReader.parse(exmaraldaFile.getAbsolutePath());
			} catch (Exception e1) {
				throw new RuntimeException("Cannot load a xml-resource '"
						+ exmaraldaFile.getAbsolutePath() + "'.", e1);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Cannot read xml-file'"
						+ documentLocation
						+ "', because of a nested exception. ", e);
		}
	}
}