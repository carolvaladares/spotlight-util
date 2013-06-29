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

package intrinsic.spotlight.utils

import java.io.InputStream
import java.io.PrintStream

import scala.Option.option2Iterable

import com.hp.hpl.jena.rdf.model.Model
import com.typesafe.config.ConfigFactory

import intrinsic.spotlight.utils.util.ExtractData


/**
 * Context Extraction from RDF
 * Used to process the properties and labels and group them by the subject
 * The output is formatted as TSV or JSON
 *
 * @author reinaldo
 */
object RDFContextExtractor extends App {

  /**
   * Choose the format of the output
   * Choose the relevant part to be extracted: object, property or type
   * Fill the input and output file names
   * Check the output to see the extraction
   */

  /** Use it to create databases at filesystem*/
  /** DataConn.create_TDB_Filesystem(
   *  config.getString("dataSet.location"), 
   *  config.getString("dataSet.inputFile"));*/

  
  /** Get file extension */
  def extensor(file: String) = file.split('.').drop(1).lastOption
  
  /** Extracting from Config File */
  def extractFromConfig(): String = {
    
    /* Get properties from application.conf */
    val config = ConfigFactory.load
    
    /** Use it to load a database from filesystem*/
    val loadTDB: Boolean = config.getBoolean("dataSet.reload")
    val tdbFile: String = config.getString("dataSet.inputFile")
    val tdbLocation: String = config.getString("dataSet.location")
    val tdbFormat: String = config.getString("dataSet.format")
    
    val extraction: String = config.getString("execution.extraction")
    val outputFormat: String = config.getString("execution.outputFormat")
    val outputFile: String = config.getString("execution.outputFile")
    
    /** Supports only *.bz2 files for now*/
    val modelFile: String = config.getString("execution.inputFile")
    val modelFormat: String = config.getString("execution.inputFormat")
    
    extract(loadTDB, 
    		tdbFile, 
    		tdbLocation,
    		tdbFormat,
    		modelFile, 
    		modelFormat,
    		extraction, 
    		outputFormat,
    		outputFile)
    		
    outputFile
  }
  
  /** Extracting */
  def extract(loadtdb: Boolean, 
		  	    tdbFile: String, 
		  	    tdbLocation: String, 
		  	    tdbFormat: String,
		  	    modelFile:String, 
		  	    modelFormat:String, 
		  	    extraction: String, 
		  	    outformat: String, 
		  	    outputFile: String) {
    
    if (loadtdb) {
      /** Convert TDB input file into InputStream*/
      val tdbModel: InputStream = if (extensor(tdbFile).equals(Some("bz2"))) ExtractData.convert(tdbFile) 
    		  					    else ExtractData.toInputStream(tdbFile)
      /** Creates and populates TDB */
      DataConn.createTDBFilesystem(tdbLocation, tdbModel, tdbFormat)
    }
   
    /** converts Model input file into InputStream**/
    var input: InputStream = if (extensor(modelFile).equals(Some("bz2"))) ExtractData.convert(modelFile) 
    						   else ExtractData.toInputStream(modelFile)
    		
    /** Get TDB */
    DataConn.getTDBFilesystem(tdbLocation)
    /** Add Model into TDB **/
    DataConn.addModelToTDB("properties", input, modelFormat)
    
    /**Execute the extraction itself */
    RDFContextExtractor.labelExtraction(
      extraction,
      outformat,
      "properties",
      outputFile)
  }
  
  /**
   * (CV: No longer used) Reads in files into a Jena Model.
   * Performs context extraction, formatting and outputs context into a file.
   * Context extraction can focus on property labels, object labels or object type labels.
   */
  def labelExtraction(partToBeExtracted: String, format: String, namedModel: String, outputFile: String) {
    
    /** creating the output*/
    val output = new PrintStream(outputFile)
    
    /** choosing output format*/
    val formatter = if (format.equals("TSV")) new TSVOutputFormatter else new PigOutputFormatter
    
    /** choosing extraction strategy*/
    val extractor = if (partToBeExtracted.equals("object")) new ObjectExtractor else new PropertyExtractor
    
    /** applying over input*/
    val model: Model = DataConn.model//dataSet.getNamedModel(namedModel)
   
    var source: JenaStatementSource =  new JenaStatementSource( model)
    
    source.groupBy(e => e.getSubject).flatMap {
      case (subject, statements) => {
        val context = extractor.extract(statements).mkString(" ")
        output.println(formatter.format(subject.getLocalName, context))
        Some((subject, context))
      }
      case _ => None
    }.toSeq
     .sortBy(e => e._1.getLocalName)
   
    output.close
    source = null
    
    
  }
}