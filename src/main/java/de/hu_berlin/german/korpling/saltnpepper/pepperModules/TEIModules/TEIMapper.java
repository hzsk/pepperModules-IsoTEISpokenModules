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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.dmt.Uri;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.AttributesImpl;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.impl.SaltFactoryImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SAudioDSRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SAudioDataSource;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STimeline;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STimelineRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.impl.STextualDSImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SWordAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;

public class TEIMapper extends PepperMapperImpl{
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		TEIImporterProperties props = ((TEIImporterProperties) getProperties());
		SDocumentGraph docGraph = getSDocument().getSDocumentGraph();
		TEIImporterReader reader = new TEIImporterReader(props);
		reader.setsDocGraph(docGraph);
		this.readXMLResource(reader, getResourceURI());
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	public static class TEIImporterReader extends DefaultHandler2 implements TEITagLibrary{
		//options
		private Boolean default_tokenization = false;
		private Boolean sub_tokenization = false;
		
		private Boolean surplus_removal = false;
		private Boolean unclear_as_token = false;
		private Boolean foreign_as_token = false;
		
		private Boolean use_tokenizer = false;
		private LanguageCode use_tokenizer_language = null;
		
		private Boolean del_redundant_metadata = null;
		private Boolean skip_default_annotations = false;
		private Boolean use_namespace = false;
		
		private Boolean generic_struct = false;
		private Boolean generic_span = false;
		private Boolean generic_attr = false;
		
		private Boolean token_anno_span = false;
		
		private Boolean lastPart = false;
		private Boolean excludeMetadata = false;
		
		private Set<String> excludeMetaSet = null;
		
		//naming config strings
		private String lb_name = "lb";
		private String pb_name = "pb";
		private String w_name = "w";
		private String phr_name = "phr";
		private String body_head_name = "head";
		private String div_name = "div";
		private String p_name = "p";
		private String foreign_name = "foreign";
		private String figure_name = "figure";
		private String unclear_name = "unclear";
		//other Strings to be added here in the future
		private String text_name = "text";
		
		private String slash = "_";
		private String at = "-";
		private String brack1 = "_";
		private String brack2 = "_";
		private String colon = "_";
		
		
		//annotation config values spans
		private String lb_anno_value = "lb";
		private String pb_anno_value = "pb";
		
		//annotation config values strucs
		private String phr_anno_value = "phr";
		private String body_head_anno_value = "head";
		private String div_anno_value = "div";
		private String p_anno_value = "p";
		private String figure_anno_value = "figure";
		private String text_anno_value = "text";
		
		
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setDEFAULT_TOKENIZATION(){
			default_tokenization = true;
		}
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setSUB_TOKENIZATION(){
			sub_tokenization = true;
		}
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_STRUCT(){
			generic_struct = true;
		}
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_SPAN(){
			generic_span = true;
		}
		
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_ATTR(){
			generic_attr = true;
		}
		
		/**
		 * true if the parser is inside <text>...</text>
		 */
		private Boolean insidetext = false;
		
		/**
		 * true if the parser is inside <TEIHeader>...<TEIHeader>
		 */
		private Boolean metadata = false;
		
		//stacks for unary break elementes creating spans
		/**
		 * stack for temporarily saving tokens later to be added to lbspan
		 */
		private Stack<SToken> lbSpanTokenStack = new Stack<SToken>();
		
		/**
		 * stack for temporarily saving tokens later to be added to pbspan
		 */
		private Stack<SToken> pbSpanTokenStack = new Stack<SToken>();
		//
		
		/**
		 * Map for storing tokens belonging to generic spans
		 */
		private Map<String,Stack<SToken>> genericSpanMap = null;
		
		private Map<String,Attributes> attrMap = null;
		
		/**
		 * stack that follows the parser in adding and removing certain elements that are also sNodes
		 */
		private Stack<SNode> sNodeStack= null;
		
		/**
		 * Method to retrieve sNodeStack and initialize if it is null
		 * @return sNodeStack
		 */
		private Stack<SNode> getSNodeStack(){
			if (sNodeStack== null)
				sNodeStack= new Stack<SNode>();
			return(sNodeStack);
		}
		
		/**
		 * stack that follows the parser in adding and removing all elements
		 */
		private Stack<String> TagStack = new Stack<String>();
		
		/**
		 * Method to retrieve TagStack and initialize if it is null
		 * @return TagStack
		 */
		private Stack<String> getTagStack(){
			if (TagStack== null)
				TagStack= new Stack<String>();
			return(TagStack);
		}
		
		/**
		 * stack that follows the parser in adding and removing all elements
		 */
		private Stack<SAnnotation> SAnnoStack = null;
		
		/**
		 * Method to retrieve SAnnoStack and initialize if it is null
		 * @return SAnnoStack
		 */
		private Stack<SAnnotation> getSAnnoStack(){
			if (SAnnoStack == null) 
				SAnnoStack= new Stack<SAnnotation>();
			return(SAnnoStack);
			}
		
		/**
		 * Stringbuilder used for collecting text between insidetext-tags
		 */
		StringBuilder txt = new StringBuilder();
		
		/**
		 * Stringbuilder used for collecting tags between metadata-tags
		 */
		StringBuilder meta_txt = new StringBuilder();
		
		/**
		 * SDocumentGraph variable
		 */
		private SDocumentGraph sDocGraph = null;
		
		/**
		 * Method to return the SDocumentGraph
		 * @return sDocGraph
		 */
		public SDocumentGraph getsDocGraph() {
			return sDocGraph;
		}
		
		/**
		 * Instance of metadata-class
		 */
		private TEIImporterMetaData tei_metadata = new TEIImporterMetaData(slash, at, brack1, brack2, colon);
		
		/**
		 * primary text variable
		 */
		private STextualDS primaryText = null;
		

		/**
		 * Sets the SDocGraph
		 * @param DocGraph SDocumentGraph of the reader
		 */
		public void setsDocGraph(SDocumentGraph DocGraph) {
			sDocGraph = DocGraph;
		}
		
		/**
		 * properties instance that influences the behavior of the mapper
		 */
		private TEIImporterProperties props= null;
		
		/**
		 * tokenizer variable
		 */
		de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer tokenizer = null;
		
		/**
		 * gets the properties instance
		 * @return props
		 */
		public TEIImporterProperties getProps() {
			return props;
		}
		
		/**
		 * sets the properties instance
		 * @param props the properties instance that is to be used by the mapper
		 */
		public void setProps(TEIImporterProperties props) {
			this.props = props;
		}
		
		/**
		 * The standard constructor that should always be used!
		 * @param props the properties instance that is to be used by the mapper
		 */
		public TEIImporterReader(TEIImporterProperties props){
			//get the parameter values
			super();
			setProps(props);
			default_tokenization = false;
			sub_tokenization = true;
			
			surplus_removal = true;
			unclear_as_token = true;
			foreign_as_token = true;
			
			use_tokenizer = false;
			use_tokenizer_language = LanguageCode.en;
			
			del_redundant_metadata = props.isDelMetadata();
			skip_default_annotations = false;
			
			generic_struct = true;
			generic_span = false;
			generic_attr = true;
			
			lastPart = props.isUseLastPart();
			
			token_anno_span = false;
			excludeMetadata = props.isUseExcludeMetadata();
			
			//fill metadata
			props.fillMappings();
			excludeMetaSet = props.retrieveExcludeMetadataSet();
			
			//annotation customization
			slash = props.getChar("slash");
			at = props.getChar("at");
			colon = props.getChar("colon");
			brack1 = props.getChar("bracket.open");
			brack2 = props.getChar("bracket.close");
			
			//naming config strings
			lb_name = TAG_LB;
			pb_name = TAG_PB;
			w_name = TAG_W;
			phr_name = TAG_PHR;
			body_head_name = TAG_HEAD;
			div_name = TAG_DIV;
			p_name = TAG_P;
			foreign_name = TAG_FOREIGN;
			figure_name = TAG_FIGURE;

			unclear_name = TAG_UNCLEAR;

			//other Strings to be added here in the future
			text_name = TAG_TEXT;
			
			//annotation config values spans
			lb_anno_value = TAG_LB;
			pb_anno_value = TAG_PB;
			
			//annotation config values strucs
			phr_anno_value = TAG_PHR;
			body_head_anno_value = TAG_HEAD;
			div_anno_value = TAG_DIV;
			p_anno_value = TAG_P;
			figure_anno_value = TAG_FIGURE;

			text_anno_value = TAG_TEXT;
		}
		
		/**
		 * Legacy constructor used by the reader-jUnit-test
		 */
		public TEIImporterReader(){
				//get the parameter values
				super();
				sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		}
		
		/**
		 * called when reader starts reading the input document
		 */
		public void startDocument () {

	    }
		
		/**
		 * adds token to sDocGraph dominated by SStructure on top of SNodeStack
		 * @param token token to be added
		 */
		private void setDominatingToken (SToken token) {
			SDominanceRelation sDominatingRelation= SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(token);
			sDocGraph.addSRelation(sDominatingRelation);
		}
		
		/**
		 * adds SStructure to sDocGraph dominated by SStructure on top of SNodeStack
		 * @param struc SStructure to be added
		 */
		private void setDominatingStruc (SStructure struc) {
			SDominanceRelation sDominatingRelation= SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(struc);
			sDocGraph.addSRelation(sDominatingRelation);
		}
		
		/**
		 * adds a single space character to a primary text
		 * @param text primary text that is to be manipulated
		 */
		private void addSpace (STextualDS text) {
			text.setSText(text.getSText()+" ");
		}
		Integer pausePoint=null;
		private void addPause (STextualDS text) {
			pausePoint = primaryText.getSEnd();
			text.setSText(text.getSText()+"Pause");
		}
		
		
		private String retrieveNamespace(Boolean prop, String name){
			String namespace = null;
			if(prop){
				namespace = name;
				return(namespace);
			}
			else if(!prop){
				return(namespace);
			}
			return("erratic namespace");
		}
		
		/**
		 * adds an empty token to the sDocGraph
		 */
		private void setEmptyToken(){
			SToken temp_tok = null;
			if (primaryText.getSEnd()==null){
				addSpace(primaryText);
			}
			
			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			setTimeRel(temp_tok, source);
			push_spans(temp_tok);
		}
		
		private void setEmptyTokenPause(String val){
			SToken temp_tok = null;
			addPause(primaryText);
			
			temp_tok = sDocGraph.createSToken(primaryText, pausePoint, primaryText.getSEnd());
			temp_tok.createSAnnotation(null, "duration", val);
			setTimeRel(temp_tok, source);
			push_spans(temp_tok);
		}
		
		/**
		 * creates a span for each token annotation
		 * 
		 */
		private void createTokenAnnoSpan(SToken token, SAnnotation anno){
			if (token_anno_span){
				SSpan span = sDocGraph.createSSpan(token);
				span.addSAnnotation(anno);
			}
		}
		
		/**
		 * adds an empty token to the sDocGraph. This token
		 * is dominated by an SStructure as required by the <gap>-tag
		 */
		private void setGapToken(){
			if (primaryText.getSText().length() > 0){
			
			SToken temp_tok = null;
			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			setTimeRel(temp_tok, source);
			while(!getSAnnoStack().isEmpty()){
				createTokenAnnoSpan(temp_tok, getSAnnoStack().peek());
				
				if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null){
					temp_tok.addSAnnotation(getSAnnoStack().pop());
				}
				else {
					getSAnnoStack().pop();
				}
			}
			
			
			push_spans(temp_tok);
			}
		}
		/**
		 * method to check whether a SNode has outgoing sRelations
		 * @param closingStructure the SNode to be checked
		 * @return return true if one or more outgoing sRelations exist, false alternatively
		 */
		private Boolean checkOutgoingRelations(SNode closingStructure){
			if (closingStructure.getOutgoingSRelations().size() < 1){
				return false;
			}
			else{
				return true;
			}
		}
		
		/**
		 * method to pop SNode, that checks whether there
		 * is at least one token assigned to the SNode; if
		 * not an empty token is added
		 */
		private void popNodeWithNoTokenCheck(){
			if (!getSNodeStack().empty()){
				if (checkOutgoingRelations(getSNodeStack().peek())){
					getSNodeStack().pop();
				}
				
				else{
					setEmptyToken();
					getSNodeStack().pop();
				}
			}
		}
		
		/**
		 * default method to add a token to the sDocGraph
		 * @param str Stringbuilder that contains the text
		 * to be used for the token
		 */
		private void setStandardToken (StringBuilder str) {
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					SToken temp_tok = null;
					/*in case primaryText is empty, but exists, initialize primaryText with temp
					 *to avoid "null" as part of the string; otherwise add temp to primaryText
					 */
					if (str.length() > 0  && primaryText.getSText()==null){
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
						setTimeRel(temp_tok, source);
						//setDominatingToken(temp_tok);
					}
				
					/*add a single space character to split the first and last word from 
					 *two neighboring chunks of text*
					 */
					else if (str.length() > 0 && !(primaryText.getSText()==null)){
						addSpace(primaryText);
						int oldposition = primaryText.getSEnd();
						
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(primaryText.getSText()+tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
						setTimeRel(temp_tok, source);
						//setDominatingToken(temp_tok);
					}
					while (!getSAnnoStack().isEmpty()) {
						createTokenAnnoSpan(temp_tok, getSAnnoStack().peek());
						if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null){
							temp_tok.addSAnnotation(getSAnnoStack().pop());
						}
						else {
							getSAnnoStack().pop();
						}
					}
					//add token to stack for sspans
					push_spans(temp_tok);
					
				}
				str.setLength(0);
			}
		}
		
		private void setStandardTokenId (StringBuilder str, String id) {
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					SToken temp_tok = null;
					/*in case primaryText is empty, but exists, initialize primaryText with temp
					 *to avoid "null" as part of the string; otherwise add temp to primaryText
					 */
					if (str.length() > 0  && primaryText.getSText()==null){
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
						setTimeRel(temp_tok, source);
						//setDominatingToken(temp_tok);
					}
				
					/*add a single space character to split the first and last word from 
					 *two neighboring chunks of text*
					 */
					else if (str.length() > 0 && !(primaryText.getSText()==null)){
						addSpace(primaryText);
						int oldposition = primaryText.getSEnd();
						
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(primaryText.getSText()+tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
						setTimeRel(temp_tok, source);
						//setDominatingToken(temp_tok);
					}
					while (!getSAnnoStack().isEmpty()) {
						createTokenAnnoSpan(temp_tok, getSAnnoStack().peek());
						if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null){
							temp_tok.addSAnnotation(getSAnnoStack().pop());
						}
						else {
							getSAnnoStack().pop();
						}
					}
					
					//add status annotations
					if (unclear){
						temp_tok.createSAnnotation(null, "unclear", "unclear Token");
					}
					
					//add token to stack for sspans
					push_spans(temp_tok);
					wInfo.put(id, temp_tok);
					timeparts.add(temp_tok);
				}
				str.setLength(0);
			}
		}
		
		/**
		 * adds a list of tokens to the sDocGraph
		 * @param tokenlist a list containing tokens
		 */
		private void setTokenList (List<String> tokenlist){
			for (String tokstring: tokenlist){
				SToken temp_tok = null;
				if (primaryText.getSText() != null){
					addSpace(primaryText);
				}
				if (primaryText.getSText()==null){
					primaryText.setSText("");
				}
				int oldposition = primaryText.getSEnd();
				if (primaryText.getSText() == null){
					primaryText.setSText(tokstring);
				}
				
				else if (primaryText.getSText() != null){
					primaryText.setSText(primaryText.getSText()+tokstring);
				}
					
				temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
				setTimeRel(temp_tok, source);
				setDominatingToken(temp_tok);
				push_spans(temp_tok);
			}
		}
		
		/**
		 * creates list of tokens and calls setTokenList to add
		 * these tokens to the sDocGraph
		 * @param str StringBuilder that contains text that is tokenized
		 * and subsequently added to the sDocGraph as a list of tokens
		 * that was returned by tokenizing
		 */
		private void setTokenizedTokens (StringBuilder str) {
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						List<String> tokenliste = tokenizer.tokenizeToString(tempstr, use_tokenizer_language);
						setTokenList(tokenliste);
				}	
				str.setLength(0);
			}
		}
		
		/**
		 * sets tokens generically by checking whether tokenization is set 
		 * by the user
		 * @param str StringBuilder that contains text that is to be added
		 * as one or more tokens
		 */
		private void setToken (StringBuilder str){
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					if (use_tokenizer){
						setTokenizedTokens(str);
					}
					else if (!use_tokenizer){
						setStandardToken(str);
					}
				}
			}
		}
		
		private void setTokenId (StringBuilder str, String id){
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					if (use_tokenizer){
						setTokenizedTokens(str);
					}
					else if (!use_tokenizer){
						setStandardTokenId(str,id);
					}
				}
			}
		}
		
		/**
		 *this is the generic method for unary elements creating spans
		 *in addition to calling this function, the tokens have to be
		 *added in setToken
		 * @param tag string that is used for annotating the span
		 * @param tokenStack stack containing the germane tokens
		 * @param annovalue annotation value that is to be annotated
		 */
		private SSpan generic_break(String tag, Stack<SToken> tokenStack, String annovalue){
			if (sub_tokenization){
				setToken(txt);
			}

			EList <SToken> overlappingTokens = new BasicEList<SToken>();
			while (!(tokenStack).isEmpty()){
				overlappingTokens.add(tokenStack.pop());
			}
			SSpan line = sDocGraph.createSSpan(overlappingTokens);
			if (line != null){
				line.createSAnnotation(null, tag, annovalue);
				if (generic_attr && attrMap != null){
					Attributes attributes = attrMap.get(tag);
					if (attributes!= null){
						int length = attributes.getLength();
						for(int i=0; i<length; i++){
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							name = name.replace(":",colon);
							line.createSAnnotation(null , name, value);
						}
					}
				}
			}
			Attributes attributes = attrMap.get(tag);
			String end = attributes.getValue("end");
			String start = attributes.getValue("start");
			
			setAudioSpan(line);
			
			return line;
		}
		
		
		
		/**
		 * pushes the spans to the added stacks
		 * @param tok token that is pushed
		 */
		private void push_spans(SToken tok){
			lbSpanTokenStack.push(tok);
			pbSpanTokenStack.push(tok);
			
			pushToGenerics(tok);
		}
		
		/**
		 * add a tag to the stack of spans
		 * @param name tag-name
		 */
		private void addToGenericSpans(String name, Attributes attr, Attributes segInfo){
			if (genericSpanMap==null){
				genericSpanMap = new Hashtable<>();
			}
			if (attrMap == null){
				attrMap = new Hashtable<>();
			}
		    
			attrMap.put(name, new AttributesImpl(attr));
			attrMap.put(name, new AttributesImpl(segInfo));
			
			Stack<SToken> gen_stack = new Stack<>();
			genericSpanMap.put(name, gen_stack);
		}
		
		/**
		 * pushes the tokens to the stacks of the
		 * generic spans
		 * @param token token that is pushed
		 */
		private void pushToGenerics(SToken token){
			if (genericSpanMap != null){
				Set<String> keySet = genericSpanMap.keySet();
				for (String s : keySet) {
				    Stack<SToken> tempStack = genericSpanMap.get(s);
				    tempStack.push(token);
				}
			}
		}
		
		/**
		 * method to collect characters between tags
		 * @param ch array of characters
		 * @param start starting position
		 * @param length length of the array
		 */
		@Override
		public void characters(char ch[], int start, int length) {
			//change tokenization to higher level
			if (insidetext){
				StringBuilder tempstr = new StringBuilder();
				for(int i=start; i<start+length; i++){
					tempstr.append(ch[i]);
				}
				txt.append(tempstr.toString().trim());
			}
			
			if (metadata){
				StringBuilder tempstr = new StringBuilder();
				for(int i=start; i<start+length; i++){
					tempstr.append(ch[i]);
				}
				meta_txt.append(tempstr.toString().trim());
			}
			
		}
		Boolean insideTimeline = false;
		Boolean body = false;
		
		private Map<String, Double> times= new Hashtable<>();
		private Attributes segInfo;
		private Map<String, SToken> wInfo = new Hashtable<>();
		private Map<String, String> spanInfo = new Hashtable<>();
		boolean spanAnno = false;
		SSpan currentSpan = null;
		String currentAnno = null;
		boolean wordAnno = false;
		String wordAnnoId = null;
		boolean wExist = false;
		
		Map<String, String> speakerMap = new Hashtable<>();
		
		STimeline timeline = null;
		
		String id = null;
		String url1 = "";
		SAudioDataSource source = null;
		
		Double tempTimeStart = null;
		Double tempTimeEnd = null;
		
		
		private int anchorStart = 0;
		private int anchorEnd = 0;
		private Set <SToken> timeparts = new HashSet<SToken>();
		private Map<SToken,List<Integer>> tokenTimes = new Hashtable<SToken, List<Integer>>();
		
		int spanStart = 0;
		int spanEnd = 0;
		
		public Map<String, Double> getTimes(){
			return times;
		}
		
		private Map<String,STextualDS> textuals = new Hashtable<String, STextualDS>();
		
		private void setTextual(String speaker, Boolean misc){
			if (textuals.containsKey(speaker)){
				primaryText = textuals.get(speaker);
			}
			else{
				STextualDS tempTDS = SaltFactory.eINSTANCE.createSTextualDS();
				textuals.put(speaker, tempTDS);
				tempTDS.setGraph(sDocGraph);
				tempTDS.setSText("");
				primaryText = tempTDS;
				tempTDS.setSName(speakerMap.get(speaker.substring(1)));
				
				if (misc){
					tempTDS.setSName(speaker);
				}
				
				
			}
		}
		
		
		
		private Map<String, String> getSpeakerName(Map<String, String> completedmappings){
			
			Map<String, String> speakerMap = new Hashtable<>();
			
			if (completedmappings.containsKey(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+slash+""+at+"n")){
				speakerMap.put(completedmappings.get(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+slash+""+at+"xml:id"), completedmappings.get(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+slash+""+at+"n"));
			}										  
			
			for (int i=2; true;i++ ){
				if (completedmappings.containsKey(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+brack1+""+String.valueOf(i)+""+brack2+""+slash+""+at+"n")){
					speakerMap.put(completedmappings.get(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+brack1+""+String.valueOf(i)+""+brack2+""+slash+""+at+"xml:id"), completedmappings.get(""+slash+"profileDesc"+slash+"particDesc"+slash+"person"+brack1+""+String.valueOf(i)+""+brack2+""+slash+""+at+"n"));
				}
				else{
					break;
				}
			}
			
			return speakerMap;
		}
		
		int gloTokenCounter = 0;
		double gloTimeStart = 0.0;
		double gloTimeEnd = 0.0;
		
		Boolean inInc = false;
		Boolean unclear = false;
		
		private void setTimeRel(SToken token, SAudioDataSource sound){
			STimelineRelation stest = SaltFactory.eINSTANCE.createSTimelineRelation();
			stest.setGraph(sDocGraph);
			stest.setSSource(token);
			
			
			stest.setSStart(gloTokenCounter);
			stest.setSEnd(gloTokenCounter+1);
			
			
			
			gloTokenCounter += 1;
			
		
		}
		
		private void setAudioSpan(SSpan span){
			SAudioDSRelation saudio = SaltFactory.eINSTANCE.createSAudioDSRelation();
			saudio.setGraph(sDocGraph);
			saudio.setSSource(span);
			saudio.setSStart(gloTimeStart);
			saudio.setSEnd(gloTimeEnd);
		}
		
		private void setAnchor(Attributes attributes, Boolean end){
			Integer tempTime = 0;
			
			if (end){
				tempTime = anchorEnd;
			}
			
			else{
				tempTime = Integer.parseInt(attributes.getValue("synch").replace("#T", ""));
			}
			
			
			for (SToken i : timeparts){
				List<Integer> tempList = new ArrayList<Integer>();
				tempList.add(anchorStart);
				tempList.add(tempTime);
				tokenTimes.put(i, tempList);
			}
			anchorStart = tempTime;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (TAG_TEIHEADER.equals(qName)){
				metadata = true;
			}
			
			else if (metadata){
				//put text between tags into a map
				tei_metadata.push(qName);
				
				//put attribute keys and values into a map
				for (int i = 0; i < attributes.getLength();i++){
					tei_metadata.push_attribute_XPathMap(attributes.getQName(i), attributes.getValue(i));
				}
			}
			
			
			
			else if (TAG_TEXT.equals(qName)) {
				getTagStack().push(TAG_TEXT);
				
				insidetext = true;
			}
			
			else if (insidetext){
				if (TAG_TIMELINE.equals(qName)){
					insideTimeline = true;
				}
				
				else if(TAG_WHEN.equals(qName) && insideTimeline){
					Attributes attr = attributes;
					int length = attr.getLength();
					String name = null;
					Double timeOffset = null;
					for(int i=0; i<length; i++){
						String tempName = attr.getQName(i);
						String value = attr.getValue(i);
						
						if (tempName.equals("xml:id")){
							name = value;
						}
						
						if (tempName.equals("absolute")){
							timeOffset = (Double) 0.0;
						}
						
						if (tempName.equals("interval")){
							if (times.get("T0")!=null){
							timeOffset = times.get("T0") + Double.parseDouble(value);
							}
							else{
								timeOffset = Double.parseDouble(value);
							}
						}
					}
					if(timeOffset != null){
					times.put(name,timeOffset);
					}
				}
				
				else if (TAG_BODY.equals(qName)){
					body = true;
				}
				
				else if (TAG_ANNOTATIONGRP.equals(qName)){
					
					
					segInfo = new AttributesImpl(attributes);
					String tempTimeStartstr = segInfo.getValue("start");
					tempTimeStartstr = tempTimeStartstr.replace("#", "");
					gloTimeStart = times.get(tempTimeStartstr);
					
					String tempTimeEndstr = segInfo.getValue("end");
					tempTimeEndstr = tempTimeEndstr.replace("#", "");
					gloTimeEnd = times.get(tempTimeStartstr);
					
					setTextual(segInfo.getValue("who"),false);
					
					anchorStart = Integer.parseInt(tempTimeStartstr.replace("T",""));
					anchorEnd = Integer.parseInt(tempTimeEndstr.replace("T",""));
				}
				
				else if (TAG_SEG.equals(qName)){
					addToGenericSpans(qName, attributes, segInfo);
				}
				
				
				else if (TAG_W.equals(qName)){
					id = attributes.getValue("xml:id");
				}
				
				else if (TAG_SPANGRP.equals(qName)){
					txt.setLength(0);
					currentAnno = new String(attributes.getValue("type").replace(":",colon));
				}
				
				
				
				else if (TAG_TEISPANGRP.equals(qName)){
					txt.setLength(0);
					currentAnno = new String(attributes.getValue("type").replace(":",colon));
				}
				
				else if (TAG_TEISPAN.equals(qName) || TAG_SPAN.equals(qName)){
					spanStart = Integer.parseInt(attributes.getValue("from").replace("#T", ""));
					spanEnd = Integer.parseInt(attributes.getValue("to").replace("#T", ""));
				}
				
				else if (TAG_INCIDENT.equals(qName)){
					inInc = true;
				}
				
				else if (TAG_DESC.equals(qName)){
					if (inInc){
						String speaker = segInfo.getValue("who");
						if (speakerMap.containsKey(speaker.substring(1))){
							speaker = speakerMap.get(speaker.substring(1));
						}
						setTextual(speaker +"_Miscellaneous",true);
					}
				}
				
				else if (TAG_PAUSE.equals(qName)){
					String speaker = segInfo.getValue("who");
					
					if (speakerMap.containsKey(speaker.substring(1))){
						speaker = speakerMap.get(speaker.substring(1));
					}
					
					setTextual(speaker +"_Miscellaneous",true);
					String val = attributes.getValue("dur");
					setEmptyTokenPause(val);
					wExist = true;
				}
				
				else if (TAG_UNCLEAR.equals(qName)){
					unclear = true;
				}
				
				else if (TAG_ANCHOR.equals(qName)){
					setAnchor(attributes, false);
				}
			}
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (TAG_TEIHEADER.equals(qName)){
				metadata = false;
				
				Map<String, String>	custommappings = props.getMappingTable();
				Map<String, String> united = tei_metadata.uniteMappings(custommappings);
				Map<String, String> sineonesmap = tei_metadata.remove_ones(tei_metadata.getXPathMap());
				Map<String, String> completedmappings = tei_metadata.mapToXpathMap(sineonesmap, united, del_redundant_metadata);
				
				tei_metadata.add_to_SDoc(sDocGraph.getSDocument(), completedmappings, lastPart, excludeMetaSet, excludeMetadata);
				url1 = completedmappings.get(""+slash+"fileDesc"+slash+"sourceDesc"+slash+"recordingStmt"+slash+"recording"+slash+"media"+slash+""+at+"url");
				
				speakerMap = getSpeakerName(completedmappings);
				
				Boolean matches = url1.startsWith("http") || url1.startsWith("www") || url1.startsWith("ftp");
				
				if (!matches){
					url1 = "";
				}
			}
			
			else if (TAG_SEG.equals(qName)){
				setAnchor(null, true);
			}
			
			else if (metadata){
				tei_metadata.push_to_XPathMap(meta_txt.toString());
				meta_txt.setLength(0);
					
				tei_metadata.pop();
			}
			
			else if(insidetext){
				if (TAG_TIMELINE.equals(qName)){
					boolean insideTimeline = false;
				}
				else if (TAG_BODY.equals(qName)){
					body = false;
				}
				
				else if (TAG_SEG.equals(qName)){
					if (!wExist){
						setGapToken();
					}
					currentSpan = generic_break(qName, genericSpanMap.get(qName), qName);
					wExist = false;
				}
				
				else if (TAG_W.equals(qName)){
					setTokenId(txt, id);
					id = null;
					wExist = true;
				}
				
				else if (TAG_TEXT.equals(qName)) {
					insidetext = false;
					
					ArrayList<STextualDS> toDel = new ArrayList<STextualDS>();
					for (STextualDS i : sDocGraph.getSTextualDSs()){
						if (i.getSText().length() < 1){
							toDel.add(i);
						}
					}
					
					for (STextualDS i: toDel){
						sDocGraph.removeNode(i);
					}
					
					timeline = sDocGraph.createSTimeline();
					timeline.setSDocumentGraph(sDocGraph);
					sDocGraph.getSTimeline().setSName("CommonTimeline");
					
					for (STimelineRelation i : sDocGraph.getSTimelineRelations()){
						i.setSTarget(timeline);
					}
					
					source = SaltFactory.eINSTANCE.createSAudioDataSource();
					
					if (url1 == ""){
						
					}
					else{
						URI myUri = URI.createFileURI(url1);
						source.setSAudioReference(myUri);
					}
					
					
					
					source.setSDocumentGraph(sDocGraph);
					
					for (SAudioDSRelation i : sDocGraph.getSAudioDSRelations()){
						i.setTarget(source);
					}
					//sDocGraph.getSTextualDSs().get(0).
				}
				
			
				
				else if (TAG_SPAN.equals(qName) || TAG_TEISPAN.equals(qName)){
					if (true && timeparts.size() > 0){
						EList <SToken> overlappingTokens = new BasicEList<SToken>();
						
						for (SToken i:timeparts){
							if (tokenTimes.get(i).get(0)>=spanStart && tokenTimes.get(i).get(1)<=spanEnd){
								overlappingTokens.add(i);
							}
						}
						
						SSpan timeSpan = sDocGraph.createSSpan(overlappingTokens);
						if (overlappingTokens.size() > 0){
							timeSpan.createSAnnotation(null, currentAnno, txt.toString());
						}
						
					}
					
					txt.setLength(0);
					spanAnno = false;
					wordAnno = false;
					//partSpan = false;
				}
				
				
				
				else if (TAG_INCIDENT.equals(qName)){
					inInc = false;
				}
				
				else if (TAG_DESC.equals(qName)){
					setToken(txt);
					if (inInc){
						setTextual(segInfo.getValue("who"), false);
					}
				}
				
				else if (TAG_PAUSE.equals(qName)){
					setTextual(segInfo.getValue("who"), false);
				}
				
				else if (TAG_UNCLEAR.equals(qName)){
					unclear = false;
				}
				
				else if (TAG_ANNOTATIONGRP.equals(qName)){
					timeparts.clear();
				}
				
				
			}
		}
	}
	
}
