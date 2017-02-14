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

import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.TEIImporterMetaData;



public class TEIMetadataTest {
	TEIImporterMetaData metadata = new TEIImporterMetaData("-","_","-","-", "_");
	
	@Test
	public void pathStackTest(){
		metadata.push("fileDesc");
		metadata.push("titleStmt");
		metadata.push("title");
		metadata.pop();
		metadata.push("title");
		
		assertEquals("-fileDesc-1--titleStmt-1--title-2-", metadata.getcurrentpath());
		
		metadata.push_to_XPathMap("tag");
		
	}
	
	@Test
	public void unifyTest(){
		Map<String,String> map1 = new Hashtable<>();
		map1.put("-fileDesc-titleStmt-author", "A Great Author");
		metadata.uniteMappings(map1);
		
		assertEquals("A Great Author", metadata.getMappingMap().get("-fileDesc-titleStmt-author"));
	}
	
	@Test
	public void mapToXpathMapNullTest(){
		Map<String,String> map1 = new Hashtable<>();
		Map<String, String> map = metadata.mapToXpathMap(map1, metadata.getMappingMap(), true);
	}
	
	@Test
	public void mapToXpathMapTest(){
		Map<String,String> map1 = new Hashtable<>();
		map1.put("-fileDesc-titleStmt-author", "Joseph Addison");
		assertEquals("Joseph Addison", map1.get("-fileDesc-titleStmt-author"));
		Map<String, String> map = metadata.mapToXpathMap(map1, metadata.getMappingMap(), true);
		assertEquals("Joseph Addison", map1.get("author"));
	}
}