package intrinsic.spotlight.utils.util

import java.io.InputStream
import java.net.URL

import scala.io.Source
import scala.sys.process.ProcessLogger
import scala.sys.process.stringSeqToProcess

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

import com.hp.hpl.jena.util.FileManager

/**
 * Input File handler - it converts a file from *.bz2 to InputString
 * TODO: May need some changes to handle exceptions.
 * 
 * @author Carol Valadares
 */
object ExtractData {
  
  /**
   * Converts an InputString to String: Do not Use it for large files
   */
  def inputStreamToString(is: InputStream): String = {
    Source.fromInputStream(is).getLines.reduceLeft(_ + _)
  }
    
  /**
   * Gets an URL (Http) or local file (:file) of a *.bz2 file, 
   * and converts it to InputStream
   */
  def convert(path: String) = {
    val url: URL = new URL(path)
    unzipped(url)
  }
  
  def toInputStream(path: String): InputStream = {
    FileManager.get.open(path)
  }
  
  /**
   * Performs the conversion itself
   */
  private def unzipped(url: URL): InputStream = {
    new BZip2CompressorInputStream(url.openStream(), true)
  } 
  
}

object ExtractLinux {
  
  def tdbloader2(httpLink: String, datasetLocation : String, tdbLocation: String) : Array[String] =  {
    var tdbName: String = httpLink.split("/").last.replace(".bz2", "")
    println("tdbName: " + tdbName);
    var bz2File:String = wget(datasetLocation, httpLink)
    println("bz2File: " + bz2File);
    var unzipedFile:String = unzip(bz2File)
    println("unzipedFile: " + unzipedFile);
    tdbLoad2(tdbLocation, unzipedFile)
    Array(unzipedFile, tdbLocation + "/" + unzipedFile)
  }
  
  def process(args: Array[String]) {
    var lines = Vector.empty[ String ]
    println(stringSeqToProcess (args))
    val log = ProcessLogger( lines :+= _ )
    val process   = ( stringSeqToProcess (args)).run( log )
    /** blocks until process is finished and returns the exit value **/
    val response = process.exitValue 
    /** the resulting output **/
    println(lines.mkString( "\n" ))
  }
 
  def wget(path: String, link: String) : String = {

    /** Download file **/
    var command: Array[String] = Array( "/usr/local/bin/wget",  "-P" , path, link)
    process(command)
    
    /**File name**/
    path + "/" + link.split("/").last
  }
  
  def unzip(file: String) : String = {
    var command: Array[String] = Array("bzip2", "-d" , file)
    process(command)
    
    /**File name**/
    file.replace(".bz2", "")
  }
  
  def tdbLoad2(location: String, datafile: String) {
    var command: Array[String] = Array("/Users/carol/Documents/dev/apache-jena-2.10.1/bin/tdbloader2", "--loc" , location, datafile)
    process(command)
  }
  
  
}