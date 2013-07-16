package app


import org.specs2.mutable.Specification
import play.api.libs.ws.WS
import play.api.test.Helpers._
import scala.Some
import play.api.libs.json.{JsValue, Json}
import play.api.http.{Writeable, ContentTypeOf}
import utils.{RestCase, RestCases}


trait RestTest extends Specification  {

  def testRests(file:String) = {
    RestCases.parse(file).map{ c =>
      testRestApi(c)
    }
  }

  def testRestApi(c:RestCase) ={

    val (message,method,status,api,input,output) = (c.message,c.method,c.status,c.api,c.input,c.output)

    implicit val ctf = ContentTypeOf.contentTypeOf_JsValue
    api should {
      message in {
        println(s"$api-$message:$input")
        val response = method match {
          case "get" => WS.url("http://localhost:9000"+api).get
          case "post" => WS.url("http://localhost:9000"+api).post(Json.parse(input))
        }
        val result = await(response)
        result.status === status
        result.header(CONTENT_TYPE) match {
          case Some(ct) if ct.contains("text/html") => result.body must contain(output)
          case Some(ct) if ct.contains("text/plain") => result.body must contain(output)
          case Some(ct) if ct.contains("application/json") => {
            if(!output.isEmpty){
              val text = result.body
              if(text.trim == output.trim){
                text === output
              }else {
                result.json === Json.parse(output)
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

