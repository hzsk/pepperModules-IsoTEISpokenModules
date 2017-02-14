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

/**
* This interface is a dictionary for files following the model of 'Besa'.
*
* @author XMLTagExtractor
**/
public interface TEITagLibrary{

		/** constant to address the xml-element 'summary'. **/
		public static final String TAG_SUMMARY= "summary";
		/** constant to address the xml-element 'body'. **/
		public static final String TAG_BODY= "body";
		/** constant to address the xml-element 'origDate'. **/
		public static final String TAG_ORIGDATE= "origDate";
		/** constant to address the xml-element 'msName'. **/
		public static final String TAG_MSNAME= "msName";
		/** constant to address the xml-element 'langUsage'. **/
		public static final String TAG_LANGUSAGE= "langUsage";
		/** constant to address the xml-element 'textLang'. **/
		public static final String TAG_TEXTLANG= "textLang";
		/** constant to address the xml-element 'change'. **/
		public static final String TAG_CHANGE= "change";
		/** constant to address the xml-element 'surname'. **/
		public static final String TAG_SURNAME= "surname";
		/** constant to address the xml-element 'placeName'. **/
		public static final String TAG_PLACENAME= "placeName";
		/** constant to address the xml-element 'msDesc'. **/
		public static final String TAG_MSDESC= "msDesc";
		/** constant to address the xml-element 'titleStmt'. **/
		public static final String TAG_TITLESTMT= "titleStmt";
		/** constant to address the xml-element 'phr'. **/
		public static final String TAG_PHR= "phr";
		/** constant to address the xml-element 'revisionDesc'. **/
		public static final String TAG_REVISIONDESC= "revisionDesc";
		/** constant to address the xml-element 'licence'. **/
		public static final String TAG_LICENCE= "licence";
		/** constant to address the xml-element 'text'. **/
		public static final String TAG_TEXT= "text";
		/** constant to address the xml-element 'forename'. **/
		public static final String TAG_FORENAME= "forename";
		/** constant to address the xml-element 'fileDesc'. **/
		public static final String TAG_FILEDESC= "fileDesc";
		/** constant to address the xml-element 'listBibl'. **/
		public static final String TAG_LISTBIBL= "listBibl";
		/** constant to address the xml-element 'country'. **/
		public static final String TAG_COUNTRY= "country";
		/** constant to address the xml-element 'pb'. **/
		public static final String TAG_PB= "pb";
		/** constant to address the xml-element 'biblScope'. **/
		public static final String TAG_BIBLSCOPE= "biblScope";
		/** constant to address the xml-element 'language'. **/
		public static final String TAG_LANGUAGE= "language";
		/** constant to address the xml-element 'idno'. **/
		public static final String TAG_IDNO= "idno";
		/** constant to address the xml-element 'series'. **/
		public static final String TAG_SERIES= "series";
		/** constant to address the xml-element 'creation'. **/
		public static final String TAG_CREATION= "creation";
		/** constant to address the xml-element 'encodingDesc'. **/
		public static final String TAG_ENCODINGDESC= "encodingDesc";
		/** constant to address the xml-element 'collection'. **/
		public static final String TAG_COLLECTION= "collection";
		/** constant to address the xml-element 'date'. **/
		public static final String TAG_DATE= "date";
		/** constant to address the xml-element 'publisher'. **/
		public static final String TAG_PUBLISHER= "publisher";
		/** constant to address the xml-element 'msIdentifier'. **/
		public static final String TAG_MSIDENTIFIER= "msIdentifier";
		/** constant to address the xml-element 'author'. **/
		public static final String TAG_AUTHOR= "author";
		/** constant to address the xml-element 'authority'. **/
		public static final String TAG_AUTHORITY= "authority";
		/** constant to address the xml-element 'msContents'. **/
		public static final String TAG_MSCONTENTS= "msContents";
		/** constant to address the xml-element 'title'. **/
		public static final String TAG_TITLE= "title";
		/** constant to address the xml-element 'pubPlace'. **/
		public static final String TAG_PUBPLACE= "pubPlace";
		/** constant to address the xml-element 'TEI'. **/
		public static final String TAG_TEI= "TEI";
		/** constant to address the xml-element 'repository'. **/
		public static final String TAG_REPOSITORY= "repository";
		/** constant to address the xml-element 'msPart'. **/
		public static final String TAG_MSPART= "msPart";
		/** constant to address the xml-element 'sourceDesc'. **/
		public static final String TAG_SOURCEDESC= "sourceDesc";
		/** constant to address the xml-element 'profileDesc'. **/
		public static final String TAG_PROFILEDESC= "profileDesc";
		/** constant to address the xml-element 'publicationStmt'. **/
		public static final String TAG_PUBLICATIONSTMT= "publicationStmt";
		/** constant to address the xml-element 'locus'. **/
		public static final String TAG_LOCUS= "locus";
		/** constant to address the xml-element 'availability'. **/
		public static final String TAG_AVAILABILITY= "availability";
		/** constant to address the xml-element 'origin'. **/
		public static final String TAG_ORIGIN= "origin";
		/** constant to address the xml-element 'msItem'. **/
		public static final String TAG_MSITEM= "msItem";
		/** constant to address the xml-element 'm'. **/
		public static final String TAG_M= "m";
		/** constant to address the xml-element 'incipit'. **/
		public static final String TAG_INCIPIT= "incipit";
		/** constant to address the xml-element 'w'. **/
		public static final String TAG_W= "w";
		/** constant to address the xml-element 'history'. **/
		public static final String TAG_HISTORY= "history";
		/** constant to address the xml-element 'ref'. **/
		public static final String TAG_REF= "ref";
		/** constant to address the xml-element 'bibl'. **/
		public static final String TAG_BIBL= "bibl";
		/** constant to address the xml-element 'teiHeader'. **/
		public static final String TAG_TEIHEADER= "teiHeader";
		/** constant to address the xml-element 'p'. **/
		public static final String TAG_P= "p";
		/** constant to address the xml-element 'ab'. **/
		public static final String TAG_AB= "ab";
		/** constant to address the xml-element 'objectType'. **/
		public static final String TAG_OBJECTTYPE= "objectType";
		/** constant to address the xml-element 'origPlace'. **/
		public static final String TAG_ORIGPLACE= "origPlace";
		/** constant to address the xml-element 'lb'. **/
		public static final String TAG_LB= "lb";
		/** constant to address the xml-element 'head'. **/
		public static final String TAG_HEAD= "head";
		/** constant to address the xml-element 'div'. **/
		public static final String TAG_DIV= "div";
		/** constant to address the xml-element 'foreign'. **/
		public static final String TAG_FOREIGN= "foreign";
		/** constant to address the xml-element 'figure'. **/
		public static final String TAG_FIGURE= "figure";
		/** constant to address the xml-element 'unclear'. **/
		public static final String TAG_UNCLEAR= "unclear";
		/** constant to address the xml-element 'surplus'. **/
		public static final String TAG_SURPLUS= "surplus";
		/** constant to address the xml-element 'gap'. **/
		public static final String TAG_GAP= "gap";
		/** constant to address the xml-element 'lem'. **/
		public static final String TAG_LEM= "lem";
		/** constant to address the xml-element 'supplied'. **/
		public static final String TAG_SUPPLIED= "supplied";
		/** constant to address the xml-element 'app'. **/
		public static final String TAG_APP= "app";
		/** constant to address the xml-element 'incident'. **/
		public static final String TAG_INCIDENT= "incident";
		/** constant to address the xml-element 'desc'. **/
		public static final String TAG_DESC= "desc";
		/** constant to address the xml-element 'pause'. **/
		public static final String TAG_PAUSE= "pause";
		
		public static final String TAG_TIMELINE= "timeline";
		public static final String TAG_WHEN= "when";
		public static final String TAG_ANNOTATIONGRP="annotationGrp";
		public static final String TAG_SEG="seg";
		public static final String TAG_SPANGRP="spanGrp";
		public static final String TAG_SPAN="span";
		public static final String TAG_TEISPANGRP="tei:spanGrp";
		public static final String TAG_TEISPAN="tei:span";
		public static final String TAG_ANCHOR="anchor";

		/** constant to address the xml-attribute 'to'. **/
		public static final String ATT_TO= "to";
		/** constant to address the xml-attribute 'lemma'. **/
		public static final String ATT_LEMMA= "lemma";
		/** constant to address the xml-attribute 'precision'. **/
		public static final String ATT_PRECISION= "precision";
		/** constant to address the xml-attribute 'mainLang'. **/
		public static final String ATT_MAINLANG= "mainLang";
		/** constant to address the xml-attribute 'n'. **/
		public static final String ATT_N= "n";
		/** constant to address the xml-attribute 'xml:id'. **/
		public static final String ATT_XML_ID= "id";
		/** constant to address the xml-attribute 'xmlns'. **/
		public static final String ATT_XMLNS= "xmlns";
		/** constant to address the xml-attribute 'from'. **/
		public static final String ATT_FROM= "from";
		/** constant to address the xml-attribute 'notBefore-custom'. **/
		public static final String ATT_NOTBEFORE_CUSTOM= "notBefore-custom";
		/** constant to address the xml-attribute 'type'. **/
		public static final String ATT_TYPE= "type";
		/** constant to address the xml-attribute 'who'. **/
		public static final String ATT_WHO= "who";
		/** constant to address the xml-attribute 'level'. **/
		public static final String ATT_LEVEL= "level";
		/** constant to address the xml-attribute 'scheme'. **/
		public static final String ATT_SCHEME= "scheme";
		/** constant to address the xml-attribute 'ident'. **/
		public static final String ATT_IDENT= "ident";
		/** constant to address the xml-attribute 'when'. **/
		public static final String ATT_WHEN= "when";
		/** constant to address the xml-attribute 'target'. **/
		public static final String ATT_TARGET= "target";
		/** constant to address the xml-attribute 'xml:lang'. **/
		public static final String ATT_XML_LANG= "xml:lang";
		/** constant to address the xml-attribute 'notAfter-custom'. **/
		public static final String ATT_NOTAFTER_CUSTOM= "notAfter-custom";
		/** constant to address the xml-attribute 'reason'. **/
		public static final String ATT_REASON= "reason";
		/** constant to address the xml-attribute 'extent'. **/
		public static final String ATT_EXTENT= "extent";
		/** constant to address the xml-attribute 'unit'. **/
		public static final String ATT_UNIT= "unit";
		/** constant to address the xml-attribute 'rend'. **/
		public static final String ATT_REND= "rend";
		/** constant to address the xml-attribute 'resp'. **/
		public static final String ATT_RESP= "resp";
		/** constant to address the xml-attribute 'atLeast'. **/
		public static final String ATT_ATLEAST= "atLeast";
		/** constant to address the xml-attribute 'atMost'. **/
		public static final String ATT_ATMOST= "atMost";
}
