package intrinsic.spotlight.utils.util

import java.io.InputStream
import java.net.URL

import scala.io.Source

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

import com.hp.hpl.jena.util.FileManager

/**
 * Input File handler - it converts a file from *.bz2 to InputString
 * TODO: May need some changes to handle exceptions.
 * TODO: CV:I have tested it with larger datasets from DBPedia (3.8) and it encountered
 * some errors regarding encoding issues.
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