package utils

import scala.util.parsing.combinator._

case class RestCase(message:String,method:String,status:Int,api:String,input:String,output:String)

object RestCases {
  def parse(path:String) = {
    val cases = CSV.parse(path)
    cases.map{c =>
      RestCase(c.getOrElse("message",""),c.getOrElse("method",""),c.getOrElse("status","").toInt,c.getOrElse("api",""),c.getOrElse("input",""),c.getOrElse("output",""))
    }
  }
}
// Original Code from http://stackoverflow.com/questions/5063022/use-scala-parser-combinator-to-parse-csv-files
object CSV extends RegexParsers {
  override val skipWhitespace = false   // meaningful spaces in CSV

  private def COMMA   = ","
  private def DQUOTE  = "\""
  private def DQUOTE2 = "\"\"" ^^ { case _ => "\"" }  // combine 2 dquotes into 1
  private def CRLF    = "\r\n" | "\n"
  private def TXT     = "[^\",\r\n]".r
  private def SPACES  = "[ \t]+".r

  private def file: Parser[List[List[String]]] = repsep(record, CRLF) <~ (CRLF?)

  private def record: Parser[List[String]] = repsep(field, COMMA)

  private def field: Parser[String] = escaped|nonescaped

  private def escaped: Parser[String] = {
    ((SPACES?)~>DQUOTE~>((TXT|COMMA|CRLF|DQUOTE2)*)<~DQUOTE<~(SPACES?)) ^^ {
      case ls => ls.mkString("")
    }
  }

  private def nonescaped: Parser[String] = (TXT*) ^^ { case ls => ls.mkString("") }

  private def parseIo(i: scala.io.BufferedSource): List[Map[String, String]] = parseString(i.getLines.mkString("\r\n"))
  private def parseString(s: String): List[Map[String, String]] = parseAll(file, s) match {
    case Success(alllines, _) =>
      val head = alllines.head
      alllines.drop(1) map { line =>
        var theMap = Map[String, String]()
        head.zipWithIndex.map {	e =>
          val fieldName = e._1.replaceAll("\\s+", "")
          if (fieldName != "") theMap = theMap ++ Map(fieldName -> line(e._2))
        }
        theMap
      }
    case _ => List[Map[String, String]]()
  }


  def parse(path:String):List[Map[String, String]] = parseIo(scala.io.Source.fromFile(path))
}