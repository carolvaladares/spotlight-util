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

import java.io.IOException
import java.io.InputStream

import org.apache.jena.iri.impl.IRIImplException

import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.ParameterizedSparqlString
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.util.FileManager


/**
 * Object to create and to load a database connection
 * It provides a database connection (variable dataSet) that is used in the whole project to execute queries
 *
 * @author reinaldo
 */
object DataConn {

  /* TDB, in which the DefaultModel holds Labels model and 
   * NamedModel holds either properties or types models*/
  var dataSet: Dataset = null
  
  /* Extraction Model: Types or Properties */
  var model: Model  = null
  
  /**
   * TODO: Carol: Model is a temporary solution as I have been facing an error while dealing with named model from TDB.
   * Ideally, model should be stored in the TDB as a named Model (addNAmedModel) 
   * Error: Quad cannot be null (Think it's a Statement issue.)
   * Refer to: http://www.developpez.net/forums/d1237360/webmasters-developpement-web/web-semantique/tdb-probleme-lecture-graphe-quad-object-cannot-be-null-resolu/
   */

  /**
   * Creates a Jena dataset populating both Labels and (Properties/Types) Models
   */
  def createTDB(
    labelData: String, 
    labelFormat: String, 
    labelInput: InputStream, 
    modelName: String, 
    modelInput: InputStream, 
    modelFormat: String) = {
    
    createTDBFilesystem(labelData, labelInput, labelFormat)
    addModelToTDB(modelName, modelInput, modelFormat)
  }
  
  /**
   * Creates a Jena Dataset, and adds a Labels Model from InputStream file 
   */
  def createTDBFilesystem(outputData: String, is: InputStream, format: String) = {
    /* Creates tdb */
    dataSet = TDBFactory.createDataset(outputData)
    /* Creates default Model for labels dataset */
    val tdb: Model = dataSet.getDefaultModel
    /* Populates model*/
    readIntoModel(tdb, is, format)
    tdb.close
  }

  /**
   * Adds a new NamedModel into dataSet. 
   * It might be a properties or a type dataset.
   */
  def addModelToTDB(modelName: String, is: InputStream, format: String) = {
    /* Creates a Model for Properties or Types dataset */
    model = ModelFactory.createDefaultModel//TDBFactory.createModel()
    /* Populates model*/
    readIntoModel(model, is, format)
    /* Adding model into dataset*/
    //dataSet.addNamedModel(modelName, m) 
    //Carol:ERROR:http://www.developpez.net/forums/d1237360/webmasters-developpement-web/web-semantique/tdb-probleme-lecture-graphe-quad-object-cannot-be-null-resolu/
  }
  
  /**
   * Reads InputStrem into Model
   */
   def readIntoModel(model: Model, is: InputStream, format: String): Model = {
    /* Reading inputStrem file with a specific format */
    if (is != null) {
      model.read(is, null, format)
      is.close
    } else {
      throw new IOException("Cannot open input file %s") //.format(input))
    }
    model
  }

  /** 
   *  Creates a Jena Dataset, and adds a Labels Model from a local file 
   *  CV: Old method.
   *
  def createTDBFilesystem(outputData: String, inputFile: String) = {
    dataSet = TDBFactory.createDataset(outputData)
    val tdb: Model = dataSet.getDefaultModel
    FileManager.get.readModel(tdb, inputFile)
    tdb.close
  }
  * 
  */

  /** 
   *  Grab tdb file
   */
  def getTDBFilesystem(outputData: String) = {
    dataSet = TDBFactory.createDataset(outputData)
  }

  /** 
   *  Query data from Labels Model 
   */
  def executeQuery(uri: String): String = {
    val queryStr: ParameterizedSparqlString = new ParameterizedSparqlString("SELECT * WHERE { ?s ?p ?o }")
    queryStr.setIri("s", uri)
    queryStr.setIri("p", "http://www.w3.org/2000/01/rdf-schema#label")
    var query: Query = null
    var result: String = null
    
    /**
     * Carol: To avoid the whole program to stop extracting from database, 
     * we catch those cases in which there is a data corruption. 
     */
    try{
       query  = QueryFactory.create(queryStr.toString)
       val queryExec: QueryExecution = QueryExecutionFactory.create(query, dataSet)
       val results: ResultSet = queryExec.execSelect
       
       if (results.hasNext) {
    	   val querySolution = results.next
    	   result = querySolution.getLiteral("o").getString
       }
    }
    catch {
      case e: IRIImplException => {
        println("Error 01: IRIImplException at URI " + uri );
        println(e.toString())
        //println(queryStr.toString);
        //throw new RuntimeException(e)
      }
      case e: com.hp.hpl.jena.query.QueryParseException => {
        println("Error 02: com.hp.hpl.jena.query.QueryParseException at URI " + uri );
        println(e.toString())
        //println(queryStr.toString);
        //throw new RuntimeException(e)
      }
    }
   result
    
  }
}