package utils

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification


class CSVSpec extends Specification {

  "csv parser" should {

    "parse hello world" in {

      val text =
        """message,api,method,input ,status,output
          hello world,/,get,,200,hello world"""

      val excepted = List(Map("method" -> "get", "status" -> "200", "message" -> "          hello world", "api" -> "/", "output" -> "hello world", "input" -> ""  ))

      CSV.parseText(text) === excepted

    }

  }
}
