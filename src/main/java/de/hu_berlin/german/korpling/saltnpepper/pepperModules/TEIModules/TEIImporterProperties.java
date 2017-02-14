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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TEIImporterProperties extends PepperModuleProperties{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2871690146180228706L;
	//String values for properties
	
	public static final String PROP_METADATA_REDUNDANT_REMOVE = "metadata.redundant.remove";
	public static final String PROP_METADATA_RENAME = "metadata.rename";
	
	
	public static final String PROP_METADATA_LASTPARTONLY = "metadata.lastpartonly";
	public static final String PROP_METADATA_REMOVE = "metadata.remove";
	public static final String PROP_METADATA_REMOVE_LIST = "metadata.remove.list";
	
	public static final String PROP_ANNOTATION_SLASH = "annotation.slash";
	public static final String PROP_ANNOTATION_AT = "annotation.at";
	public static final String PROP_ANNOTATION_BRACKET_OPEN = "annotation.bracket.open";
	public static final String PROP_ANNOTATION_BRACKET_CLOSE = "annotation.bracket.close";
	public static final String PROP_ANNOTATION_COLON = "annotation.colon";

	
	
	/**
	 * constructor that also adds the properties 
	 */
	public TEIImporterProperties(){
		
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_REDUNDANT_REMOVE, Boolean.class, "Do you want metadata with a custom mapping to appear only once?", true, false));
		addProperty(new PepperModuleProperty<String>(PROP_METADATA_RENAME, String.class, "String containing the metadata mappings set by the user", "", false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_LASTPARTONLY, Boolean.class, "Do you want to remove everything from metadata but what is after the last '/'?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_REMOVE, Boolean.class, "Do you want to exclude metadata with keys defined in ExcludeMetadataList?", false, false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_SLASH, String.class, "Character(s) to replace the / with", "_", false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_AT, String.class, "Character(s) to replace the @ with", "-", false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_BRACKET_OPEN, String.class, "Character(s) to replace the [ with", "_", false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_BRACKET_CLOSE, String.class, "Character(s) to replace the ] with", "_", false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_COLON, String.class, "Character(s) to replace the : with", "-", false));
	}
	
	public String getChar(String charIns){
		String retVal = "_";
		String prop = getProperty("annotation."+charIns).getValue().toString();
		retVal = prop;
		return retVal;
	}
	
	/**
	 * method to retrieve value of delete redundant metadata property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isDelMetadata(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_REDUNDANT_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of LastPartOnlyMetadata
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseLastPart(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_LASTPARTONLY).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of LastPartOnlyMetadata
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseExcludeMetadata(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of the custom annotation string
	 * @return values set by the user(or default)
	 */
	public String customAnnotationString(String param){
		String retVal = "";
		Object propO = getProperty(param).getValue();
		String prop= null;
		if (propO!= null){
			prop= propO.toString();
		}
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = prop;
		}
		return retVal;
	}
	
	/**
	 * map containing tag renamings
	 */
	private Map<String, String> tagRenameTable= null;
	
	/**
	 * map containing tag-value renamings
	 */
	private Map<String, String> tagRenameValuesTable= null;
	
	/**
	 * list containing the mappings set by the user
	 */
	private Map<String,String> mappingTable = null;
	
	/**
	 * gets the mappingTable
	 * @return mappingTable
	 */
	public Map<String, String> getMappingTable() {
		return mappingTable;
	}

	/**
	 * gets the name in the mapping mappingTable fitting the wanted lookup
	 * @param mappingString name to look up
	 * @return the demanded string from the mappingTable
	 */
	public String getMappings(String mappingString){
		String retVal= mappingTable.get(mappingString);

		return(retVal);
	}
	
	/**
	 * uses the customized mapping set by the user to fill mappingTable
	 */
	public void fillMappings(){
		if (mappingTable== null){
			mappingTable= new Hashtable<>();
			Object propO = null;
			
			if (getProperty(PROP_METADATA_RENAME)!= null){
				propO = getProperty(PROP_METADATA_RENAME).getValue();
			}
			else{
				propO = "";
			}
			
			String prop= null;
			if (propO.toString().trim()!= ""){
				prop= propO.toString();
				String[] renameParts= prop.split(";");
				for (String part: renameParts){
					String[] attVal= part.split(":");
					mappingTable.put(attVal[0], attVal[1]);
				}
			}
		}
		
	}
	
	public Set<String> retrieveExcludeMetadataSet(){
		Object propO = null;
		Set<String> excludeSet = null;
		if (getProperty(PROP_METADATA_REMOVE_LIST)!= null){
			propO = getProperty(PROP_METADATA_REMOVE_LIST).getValue();
			String prop = propO.toString();
			String[] propList = prop.split(";");
			excludeSet = new HashSet<>(Arrays.asList(propList));
		}
		return(excludeSet);
	}
}
