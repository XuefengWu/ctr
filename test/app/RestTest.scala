package app

import org.specs2.mutable.Specification
import play.api.libs.ws.WS
import play.api.test.Helpers._
import scala.Some
import play.api.libs.json.{JsUndefined, JsNull, Json}
import play.api.http.ContentTypeOf
import play.api.libs
import utils.{RestCase, RestCases}


trait RestTest extends Specification {

  def testRests(file: String) = {
    RestCases.parse(file).map {
      c =>
        testRestApi(c)
    }
  }

  def testRestApi(c: RestCase) = {

    val (message, method, status, api, input, output) = (c.message, c.method, c.status, c.api, c.input, c.output.trim)

    implicit val ctf = ContentTypeOf.contentTypeOf_JsValue
    api should {
      message in {
        println(s"$api-$message:$input")
        val response = method match {
          case "get" => WS.url("http://localhost:9000" + api).get
          case "post" => WS.url("http://localhost:9000" + api).post(Json.parse(input))
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
