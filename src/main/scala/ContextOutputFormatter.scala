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

import java.io.{IOException, StringReader}
import java.lang.RuntimeException
import org.apache.lucene.analysis.br.BrazilianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.util.Version

/*
Prints the output in a given format to an output print stream.
Uses System.out by default.
 */
trait ContextOutputFormatter {
  def format(subject: String, context: String) : String
}

class TSVOutputFormatter extends ContextOutputFormatter {
  def format(subject: String, context: String) = {
    List(subject,context).mkString("\t")
  }
}

class PigOutputFormatter extends ContextOutputFormatter {

  def format(subject: String, context: String) = {
    List(subject,tokenize(context)).mkString("\t")
  }

  /*
  Performs tokenization and counting (frequency of tokens in the input)
  */
  def tokenize(text: String): String = {
    val result = new StringBuilder("{")
    val map = scala.collection.mutable.Map.empty[String, Int]
    val analyzer = new BrazilianAnalyzer(Version.LUCENE_34)
    val stream: TokenStream = analyzer.tokenStream("field", new StringReader(text))
    try  {
      var key: String = null
      stream.reset()
      while (stream.incrementToken) {
        key = stream.getAttribute(classOf[CharTermAttribute]).toString
        if(map.contains(key)) map(key) = map(key)+1
        else map(key) = 1
      }
      stream.end()
      stream.close()
    }catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
    var toRead = map.size
    map foreach {case (key, value) => result.append("("+key+","+value+")"+(if({toRead-=1;toRead}>0)"," else ""))}
    result.append("}")
    result.toString()
  }
}


