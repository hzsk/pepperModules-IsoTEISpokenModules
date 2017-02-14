![SaltNPepper project](./md/img/SaltNPepper_logo2010.png)

# pepperModules-IsoTEIModules
This project provides an importer to import data coming from the ISO standard "Transcription of spoken language" based on the [TEI P5 format](http://www.tei-c.org/Guidelines/P5/) (2.8.0) for the linguistic converter framework Pepper (see [https://github.com/korpling/pepper](https://github.com/korpling/pepper)). A detailed description of that mapping can be found in section [IsoTEIImporter](#details).

Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see [https://github.com/korpling/salt](https://github.com/korpling/salt)), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. That means converting data from a format _A_ to format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle a number of n formats.

![n:n mappings via SaltNPepper](./md/img/puzzle.png)

In Pepper there are three different types of modules:
- importers (to map a format _A_ to a Salt model)
- manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
- exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter.

## Requirements
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note: Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from [https://www.oracle.com/java/index.html](https://www.oracle.com/java/index.html) or [http://openjdk.java.net/](http://openjdk.java.net/) .

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: <importer/>, <manipulator/>, <exporter/>. The IsoTEIImporter is an importer module, which can be addressed by one of the following alternatives. A detailed description of the Pepper workflow can be found on the [Pepper project site](https://github.com/korpling/pepper).

### a) Identify the module by name

```xml
<importer name="IsoTEIImporter" path="PATH_TO_CORPUS"/>
```

### b) Identify the module by formats

```xml
<importer formatName="xml" formatVersion="1.0" path="PATH_TO_CORPUS"/>
```

### c) Use properties

```xml
<importer name="IsoTEIImporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</importer>
```


## Funders
The work on the IsoTEIImporter has been funded by Hamburger Zentrum für Sprachkorpora (HZSK). It is based on the [TEIImporter](https://github.com/korpling/pepperModules-TEIModules).

## License
  Copyright 2014 Humboldt-Universität zu Berlin.

  Licensed under the Apache License, Version 2.0 (the "License");   you may not use this file except in compliance with the License.   You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

  Unless required by applicable law or agreed to in writing, software   distributed under the License is distributed on an "AS IS" BASIS,   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   See the License for the specific language governing permissions and   limitations under the License.

# <a name="details">IsoTEIImporter</a>
The IsoTEIImporter imports data coming from IsoTEISpoken files to a [Salt](https://github.com/korpling/salt) model. This importer provides a range of customization possibilities via the here described set of properties. Before we talk about the possibility of customizing the mapping, we describe the general and default mapping from IsoTEISpoken to a Salt model.

## Mapping to Salt
Two data structures are used primarily when mapping IsoTEISpoken to Salt: Primary texts and spans. As IsoTEISpoken is also meant to be used
when dialogue is on hand, multiple primary texts can be created using the IsoTEIImporter. For annotations, including those
coming from standoff markup, spans (SSpan) are used on the tokens that are generated from the w-tag in IsoTEISpoken.

For each speaker up to two primary texts (STextualDS) are created. One for the transcribed text found in the w-tags, one
(when needed) for other events caused by the speaker.

## Metadata
Metadata in Salt are represented by attribute-value pairs (having a name and a value). Since metadata in IsoTEISpokenTEI can occur in very deep structures like

```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <titleStmt>
                <title>Gospel According to Mark</title>
                <author>Mark the Evangelist</author>
            </titleStmt>
        </fileDesc>
    </teiHeader>
    ...
```

they need to be flattened, e.g. to

```
fileDesc_titleStmt_title = "Gospel According to Mark"
fileDesc_titleStmt_author = "Mark the Evangelist"
```

A metadata key in Salt like that can only be used once. If for some reason (e.g. by using a property) a metadata name is used for a second time, the IsoTEIImporter will ignore the second usage and warn the user.

### Character Limitations
When the export functionality to the RelANNIS format is used, certain restrictions to the characters used for the metadata
have to be considered. The regex for valid metadata(and other) annotation names and namespaces is:

```
[a-zA-Z_][a-zA-Z0-9_-]*.
```
Further explanations and reasoning for this can be found [here](https://github.com/korpling/ANNIS/issues/400).



## Properties
Because IsoTEISpoken is a very complex format the behaviour of the IsoTEIImporter depends to a great extent on the properties that you can use to customize the behaviour of the IsoTEIImporter. The following table contains an overview of all usable properties to customize the behaviour of the IsoTEIImporter. The following section contains a close description to each single property and describes the resulting differences in the mapping to the Salt model.

Name of property                      | Type of property | optional/mandatory | default value
------------------------------------- | ---------------- | ------------------ | -------------
[metadata.lastpartonly](#ml)          | Boolean          | optional           | false
[metadata.redundant.remove](#mrr)     | Boolean          | optional           | false
[metadata.remove](#mr1)               | Boolean          | optional           | false
[metadata.remove.list](#mrl)          | String           | optional           |
[metadata.rename](#mr2)               | String           | optional           |
[annotation.slash](#ml)               | String           | optional           | " _ "
[annotation.at](#mrr)                 | String           | optional           | " - "
[annotation.bracket.open](#mr1)       | String           | optional           | " _ "
[annotation.bracket.close](#mrl)      | String           | optional           | " _ "
[annotation.colon](#mr2)              | String           | optional           | " - "

<a name="adr"></a>


## metadata.lastpartonly
Enabling this flag triggers the deletion of everything from metadata keys aside from the part after the last '/'. The same goes for attributes, only the attribute name is kept.

For example, this

```
fileDesc_publicationStmt_date
```

would become:

```
date
```

<a name="mrr"></a>

## metadata.redundant.remove
When handling metadata, the TEIImporter uses mappings you set. This flag decides whether more than one SMetaAnnotation can contain the same information when metadata mappings are used. If set to "false", redudant metadata will not be deleted. By default redundant metadata are removed.

In case of a mapping like:

```
annotation.element.rename=fileDesc_titleStmt_author:author
```

and TEI-XML like:

```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <titleStmt>
                <author>Joseph Addison</author>
            </titleStmt>
        </fileDesc>
    </teiHeader>
    ...
```

By default only this metadate would be created:

```
author:Joseph Addison
```

If this property is set to "false", the following would be the result:

```
fileDesc_titleStmt_author:Joseph Addison
author:Joseph Addison
```

<a name="mr1"></a>

## metadata.remove
This flag enables the mechanism to exclude certain metadata defined by the names in metadata.remove.list .

<a name="mrl"></a>

## metadata.remove.list
Here you can define a list of names to be omitted. Names have to be separated by ";", e.g.:

```
metadata.remove.list = bibl;date;fileDesc_publicationStmt_pubPlace
```

<a name="mr2"></a>

## metadata.rename
To add metadata name mappings you can set your own metadata name mappings with this flag. The following example illustrates this:

```xml
<TEI xmlns="http://www.tei-c.org/ns/1.0">
    <teiHeader>
        <fileDesc>
            <sourceDesc>
                <pubPlace>Berlin</pubPlace>
            </sourceDesc>
        </fileDesc>
    </teiHeader>
    ...
```

```
metadata.rename = fileDesc_sourceDesc_pubPlace:Place
```

this would lead to this created metadate:

```
Place = Berlin
```

<a name="mrl"></a>

## annotation.slash
The "/"-character from the Xpath-like format for metadata should be replaced
for converting IsoTEISpoken to RelANNIS with pepper. This property sets the
character(s) for the replacement. By default it is "\_".

```
/fileDesc/sourceDesc/pubPlace -> fileDesc_sourceDesc_pubPlace
```

<a name="mrl"></a>
## annotation.at
The "@"-character from the Xpath-like format for metadata should be replaced
for converting IsoTEISpoken to RelANNIS with pepper. This property sets the
character(s) for the replacement. By default it is "-".

```
/fileDesc/sourceDesc/pubPlace@n -> fileDesc_sourceDesc_pubPlace-n
```

<a name="mrl"></a>
## annotation.bracket.open
The "["-character from the Xpath-like format for metadata should be replaced
for converting IsoTEISpoken to RelANNIS with pepper. This property sets the
character(s) for the replacement. By default it is "\_".

```
/fileDesc/sourceDesc/pubPlace[2] -> fileDesc_sourceDesc_pubPlace_2]
```

<a name="mrl"></a>
## annotation.bracket.close
The "]"-character from the Xpath-like format for metadata should be replaced
for converting IsoTEISpoken to RelANNIS with pepper. This property sets the
character(s) for the replacement. By default it is "\_".

```
/fileDesc/sourceDesc/pubPlace[2] -> fileDesc_sourceDesc_pubPlace[2_
```

<a name="mrl"></a>
## annotation.colon
The ":"-character from the Xpath-like format for metadata should be replaced
for converting IsoTEISpoken to RelANNIS with pepper. This property sets the
character(s) for the replacement. By default it is "-".

```
/fileDesc/sourceDesc/pubPlace@xml:id -> fileDesc_sourceDesc_pubPlace@xml-id
```

## Versions
This is Version 1.0 of IsoTEIImporter.

Version 1.0 of the IsoTEIImporter is based on Version 1.0.5 of the TEIImporter.

IsoTEIImporter was developed and tested with Pepper 2.1.2 .

IsoTEIImporter was tested with ANNIS 3.3.5 .

IsoTEISpoken is based on TEI P5 2.8.0 .

## Author
Developed by [André Röhrig](andre.roehrig@gmail.com) in 2015.
