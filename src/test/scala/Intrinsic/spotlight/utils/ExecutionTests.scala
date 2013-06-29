/* Copyright 2012 Intrinsic Ltda.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Check our project website for information on how to acknowledge the
* authors and how to contribute to the project:
* http://spotlight.dbpedia.org
*/

package Intrinsic.spotlight.utils

import java.io.InputStream

import scala.io.Source

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.scalatest.Assertions

import intrinsic.spotlight.utils.DataConn
import intrinsic.spotlight.utils.RDFContextExtractor
import intrinsic.spotlight.utils.util.ExtractData

/**
 * Tests the functions of the project.
 *
 * @author reinaldo
 */
class ExecutionTests extends Assertions {

  /** TDB Input file*/
  val labelsNT: String = "http://dl.dropboxusercontent.com/u/10940054/pt/3.8_sl_en_sl_labels_en.nt.bz2"  //Smaller dataset in EN
		  				 //"http://downloads.dbpedia.org/3.8/pt/labels_pt.nt.bz2"						//Bigger dataset in Pt
    
  val typesNT: String = "http://dl.dropboxusercontent.com/u/10940054/pt/3.8_sl_en_sl_instance_types_en.nt.bz2"  //Smaller dataset in EN
    					//"http://downloads.dbpedia.org/3.8/pt/instance_types_pt.nt.bz2"						//Bigger dataset in Pt
    
  val propertiesNT: String = "http://dl.dropboxusercontent.com/u/10940054/pt/3.8_sl_en_sl_mappingbased_properties_en.nt.bz2" //Smaller dataset in EN
    	 					 //"https://dl.dropboxusercontent.com/u/10940054/mappingbased_properties_pt.nt.bz2" 			 //Bigger dataset in Pt
  
  /** TDB Input file*/
  val labelsOWL: String = "http://dl.dropboxusercontent.com/u/10940054/pt/dbpedia_3.8.owl.bz2"
    
  /** TDB and Model Formats**/
  val formatNamed: String = "N-TRIPLE"
  val formatDefault: String = "RDF/XML"
    
  /** TDB Location **/
  val datasetNL: String = "files/outputs/tdb_nt"
  val datasetOWL: String = "files/outputs/tdb_owl"
  
  /** Extraction params */
  val extractionObject: String = "object"
  val outputFormatTSV: String = "TSV"
  val extractionProperty: String = "property"
  val outputFormatJSON: String = "JSON"
      
  def TDBCreation {
    /** Test the conversion of input file (*.bz2) into InputStrem **/
    val labelsInputNT: InputStream = ExtractData.convert(labelsNT)
    val labelsInputOWL: InputStream = ExtractData.convert(labelsOWL)
    
    assert(labelsInputNT != null)
    assert(labelsInputOWL != null)
   
    /** Test NT and OWL databases creation **/
    DataConn.createTDBFilesystem(datasetNL, labelsInputNT, formatNamed)
    DataConn.createTDBFilesystem(datasetOWL, labelsInputOWL, formatDefault)
    
    /** Retrieves the database just created. **/
    DataConn.getTDBFilesystem(datasetNL)
    assert("AlbaniaHistory".equals(DataConn.executeQuery("http://dbpedia.org/resource/AlbaniaHistory")))
    
    DataConn.getTDBFilesystem(datasetOWL)
    assert("anatomical structure".equals(DataConn.executeQuery("http://dbpedia.org/ontology/AnatomicalStructure")))
  }
  
  @Before
  def createDatabases {
    //TDBCreation
  }

  def extracTromRDF {
    val file: String = RDFContextExtractor.extractFromConfig
    asserts(file, "")
  }
  
  @Test
  def execution {
    extracTromRDF
    //extractObjectsFromMapBasPropertiesAndNtDatabase
    //extractObjectsFromTypesAndNtDatabase
    //extractObjectsFromTypesAndOwlDatabase
    //extractPropertiesFromMapBasPropertiesAndOwlDatabase
  }
 
  def asserts(outputFile: String, assertPattern: String) {
    val source = Source.fromFile(outputFile)
    val lines = source.mkString
    println(lines)
    //assert(lines.equals(assertPattern))
    source.close
  }
  
  def extractObjectsFromMapBasPropertiesAndNtDatabase {
	  
    val outputFile: String = "files/outputs/objectsFromMapBasPropertiesAndNtDatabase.tsv"
   
    RDFContextExtractor.extract(
      false, 
      labelsNT, 
      datasetNL, 
      formatDefault, 
      propertiesNT, 
      formatNamed, 
      extractionObject, 
      outputFormatTSV, 
      outputFile)
      
      asserts(outputFile, "")

    
    /*"""Aristotle	Metaphysics Theatre Biology  Galileo_Galilei List of writers influenced by Aristotle Alexander_the_Great Albertus_Magnus Christian_philosophy Socrates , Aristot√©lƒìs Science Music Ethics Parmenides Politics Democritus Zoology -0384 Duns_Scotus Rhetoric Peripatetic_school Western_philosophy Thomas_Aquinas Physics Government Avicenna Reason Logic Nicolaus_Copernicus Western_philosophy Aristotelianism Syllogism Heraclitus Ptolemy -0322 Poetry Jewish_philosophy Maimonides Plato Ancient_philosophy Islamic_philosophy Averroes
		Animal_Farm	Animal Farm: A Fairy Story George_Orwell 53163540 112 Nineteen_Eighty-Four ISBN 0-452-28424-4 (present) ISBN 978-0-452-28424-1 Harvill_Secker 823/.912 20 _Socialism_and_the_English_Genius PR6029.R8 A63 2003b Animal Farm
		Autism	Autism D001321 3202 med 001526 209850 299.00 1142
		Alabama	_Alabama English_American Alabamian State of Alabama United_States
		"""
    */

  }

  def extractObjectsFromTypesAndNtDatabase {

    val outputFile: String = "files/outputs/objectsFromTypesAndNtDatabase.tsv"
   
    RDFContextExtractor.extract(
      false, 
      labelsNT, 
      datasetNL, 
      formatDefault, 
      typesNT, 
      formatNamed, 
      extractionObject, 
      outputFormatTSV, 
      outputFile)
      
     asserts(outputFile, "")
    
    /*"""Allan_Dwan	Agent Person Thing Person Person
		Allan_Dwan__2	Thing PersonFunction
		Academy_Award_for_Best_Art_Direction	Thing Award
		Actrius	Thing CreativeWork Work Movie Film
		Animal_Farm	CreativeWork Work WrittenWork Thing Book Book Book
		Abraham_Lincoln	Agent Person Thing Person Person OfficeHolder
		Allan_Dwan__1	Thing PersonFunction
		Alain_Connes	Agent Person Thing Person Person Scientist
		Academy_Award	Thing Award
		Alabama	Thing Place Place PopulatedPlace AdministrativeArea AdministrativeRegion
		Autism	Thing Disease
		Aristotle	Agent Person Thing Person Person Philosopher
		America_the_Beautiful	Thing CreativeWork Work Musical Work MusicRecording Song
		Ayn_Rand	Artist Writer
		Allan_Dwan__3	Thing PersonFunction
		"""*/
 
  }

  def extractObjectsFromTypesAndOwlDatabase {

    val outputFile: String = "files/outputs/objectsFromTypesAndOwlDatabase.tsv"
   
    RDFContextExtractor.extract(
      false, 
      labelsOWL, 
      datasetOWL, 
      formatDefault, 
      typesNT, 
      formatNamed, 
      extractionObject, 
      outputFormatTSV, 
      outputFile)
      
     asserts(outputFile, "")
    
    /* assertEquals("""Allan_Dwan	agent Person Thing Person Person
		Allan_Dwan__2	Thing person function
		Academy_Award_for_Best_Art_Direction	Thing Auszeichnung
		Actrius	Thing CreativeWork Werk Movie Film
		Animal_Farm	CreativeWork Werk written work Thing Book Book Buch
		Abraham_Lincoln	agent Person Thing Person Person Amtsinhaber
		Allan_Dwan__1	Thing person function
		Alain_Connes	agent Person Thing Person Person Wissenschaftler
		Academy_Award	Thing Auszeichnung
		Alabama	Thing Place Ort populated place AdministrativeArea administrative region
		Autism	Thing Krankheit
		Aristotle	agent Person Thing Person Person Philosoph
		America_the_Beautiful	Thing CreativeWork Werk musical work MusicRecording song
		Ayn_Rand	K√ºnstler ÏûëÍ∞Ä
		Allan_Dwan__3	Thing person function
		""", lines)*/
  
  }

  def extractPropertiesFromMapBasPropertiesAndOwlDatabase {

    val outputFile: String = "files/outputs/propertiesFromMapBasPropertiesAndOwlDatabase.tsv"

    RDFContextExtractor.extract(
      false, 
      labelsOWL, 
      datasetOWL, 
      formatDefault, 
      propertiesNT, 
      formatNamed, 
      extractionProperty, 
      outputFormatJSON, 
      outputFile)
      
     asserts(outputFile, "")
    
    /* assertEquals("""Aristotle	{(death,1),(œÄŒµœÅŒπŒøœáŒÆ,1),(birth,1),(nam,1),(interest,11),(by,5),(year,2),(notableide,4),(main,11),(era,1),(influenced,21),(philosophicalschool,2)}
Animal_Farm	{(nam,2),(decimal,1),(author,1),(dewey,1),(previous,1),(classification,1),(work,2),(of,1),(subsequent,1),(numb,1),(pag,1),(isbn,1),(oclc,1),(herausgeb,1),(lcc,1)}
Autism	{(nam,1),(medlineplus,1),(subject,1),(icd9,1),(mesh,1),(omim,1),(id,2),(emedicin,2),(diseasesdb,1),(topic,1)}
Alabama	{(nam,1),(hauptstadt,1),(demonym,1),(œáœéœÅŒ±,1),(sprach,1)}
""", lines)*/
  }

  @After
  def removeOutputFilesOfTest {

    //assert(new File("files/outputs/propertiesFromMapBasPropertiesAndOwlDatabase.tsv").delete)
    //assert(new File("files/outputs/objectsFromTypesAndNtDatabase.tsv").delete)
    //assert(new File("files/outputs/objectsFromTypesAndOwlDatabase.tsv").delete)
    //assert(new File("files/outputs/objectsFromMapBasPropertiesAndNtDatabase.tsv").delete)

  }

}
