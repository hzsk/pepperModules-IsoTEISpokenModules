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

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.CorpusDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperImporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.TEIImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.TEIImporterProperties;

/**
 * This is a dummy implementation of a JUnit test for testing the {@link TEIImporter} class.
 * Feel free to adapt and enhance this test class for real tests to check the work of your importer.
 * If you are not confirm with JUnit, please have a look at <a href="http://www.vogella.com/tutorials/JUnit/article.html">
 * http://www.vogella.com/tutorials/JUnit/article.html</a>.
 * <br/>
 * Please note, that the test class is derived from {@link PepperImporterTest}. The usage of this class
 * should simplfy your work and allows you to test only your single importer in the Pepper environment.
 * @author Florian Zipser
 *
 */
public class TEIImporterTest extends PepperImporterTest{
	/**
	 * This method is called by the JUnit environment each time before a test case starts. 
	 * So each time a method annotated with @Test is called. This enables, that each method 
	 * could run in its own environment being not influenced by before or after running test 
	 * cases. 
	 */
	
	String filePath = new File("").getAbsolutePath();
	
	@Before
	public void setUp(){
		setFixture(new TEIImporter());
		getFixture().setProperties(new TEIImporterProperties());
		
		filePath = filePath.concat("/src/test/resources/");
		
		FormatDesc formatDef= new FormatDesc();
		formatDef.setFormatName("Isotei");
		formatDef.setFormatVersion("P5 2.6.0");
		this.supportedFormatsCheck.add(formatDef);
		
	}

	/**
	 * This is a test to check the correct work of our dummy implementation. This test is supposed to 
	 * show the usage of JUnit and to give some impressions how to check simple things of the created 
	 * salt model.
	 * <br/>
	 * You can create as many test cases as you like, just create further methods having the annotation
	 * "@Test". Note that it is very helpful, to give them self explaining names and a short JavaDoc explaining
	 * their purpose. This could help very much, when searching for bugs or extending the tests.
	 * <br/>
	 * In our case, we just test, if correct number of corpora and documents was created, if all corpora have
	 * got a meta-annotation and if each document-structure contains the right number of nodes and relations.
	 */
	
	@Test
	public void testPrimaryText() throws XMLStreamException, FileNotFoundException {
		File outFile = new File (filePath.concat("w_token_test"));
		outFile.getParentFile().mkdirs();
		
		
		this.getFixture().setCorpusDesc(new CorpusDesc());
		getFixture().getCorpusDesc().setCorpusPath(URI.createFileURI(outFile.getAbsolutePath())).setFormatDesc(new FormatDesc());
		getFixture().getCorpusDesc().getFormatDesc().setFormatName("tei").setFormatVersion("2.6.0");
		this.start();
	}
}