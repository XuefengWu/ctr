package app

import org.specs2.mutable.Specification
import play.api.libs.ws.WS
import play.api.test.Helpers._
import scala.Some
import play.api.libs.json.{JsUndefined, Json}
import play.api.http.ContentTypeOf
import utils.{RestCase, RestCases}
import com.typesafe.config.ConfigFactory
import scala.io.Source

trait RestTest extends Specification {

  val root = {
    val conf = Source.fromFile("conf/application.conf").getLines().mkString("\n")
    ConfigFactory.parseString(conf).getString("url.root")
  }

  def testRests(file: String) = {

    RestCases.parse(file).map {
      c =>
        testRestApi(c)
    }
  }

  def testRestApi(c: RestCase) = {

    val (message, method, status, api, input, output) = (c.message.trim, c.method.trim.toLowerCase, c.status, c.api.trim, c.input.trim, c.output.trim)

    implicit val ctf = ContentTypeOf.contentTypeOf_JsValue
    api should {
      message in {
        println(s"$api-$message:$input")
        val response = method match {
          case "get" => WS.url(root + api).get
          case "post" => WS.url(root + api).post(Json.parse(input))
        }
        val result = await(response)
        result.status === status
        result.header(CONTENT_TYPE) match {
          case Some(ct) if ct.contains("text/html") => result.body must contain(output)
          case Some(ct) if ct.contains("text/plain") => result.body must contain(output)
          case Some(ct) if ct.contains("application/json") => {
            if (!output.isEmpty) {
              val text = result.body
              if (text.trim == output) {
                text === output
              } else {
                if(output.startsWith("{")){
                  result.json === Json.parse(output)
                } else {
                  output.split(",").map{ n =>
                    val node = n.trim
                    val json = result.json
                    (json \ node).isInstanceOf[JsUndefined] must beFalse
                  }.head
                }
              }
            } else {
              "" === ""
            }

          }
          case ct => 1 === 1
        }

      }
    }
  }

}
