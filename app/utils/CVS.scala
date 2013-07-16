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
  override protected val whiteSpace = """[ \t]""".r

  val COMMA = ","
  val DQUOTE  = "\""
  val DQUOTE2 = "\"\"" ^^ { case _ => "\"" }
  val CR      = "\r"
  val LF      = "\n"
  val CRLF    = "\r\n"
  val TXT     = "[^\"%s\r\n]".format(COMMA).r

  private def file: Parser[List[List[String]]] = repsep(record, CRLF) <~ opt(CRLF)
  private def record: Parser[List[String]] = rep1sep(field, COMMA)
  private def field: Parser[String] = (escaped|nonescaped)
  private def escaped: Parser[String] = (DQUOTE~>((TXT|COMMA|CR|LF|DQUOTE2)*)<~DQUOTE) ^^ { case ls => ls.mkString("")}
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


  def parse(path:String) = parseIo(scala.io.Source.fromFile(path))
}