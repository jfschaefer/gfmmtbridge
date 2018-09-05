package info.kwarc.gfmmtbridge

import scala.sys.process.Process
import scalaj.http.Http
import net.liftweb.json._  //https://alvinalexander.com/scala/scala-lift-json-array-list-strings-example


class GfServer(location : String, port : Int = 41296) {
    val process = Process(List("gf", "--server=" + port, "--document-root=" + location))
    process.run

    def getRequest(pgfPath : String, params : Map[String, String]) : String = {
        var request = Http("http://localhost:" + port + "/" + pgfPath)
        for (param <- params) {
            request = request.param(param._1, param._2)
        }
        val response = request.asString
        response.body
    }
}


class ServerGfParser extends GfParser {
    override def linearize(expr: GfExpr, language: String): String = ???

    override def parse(sentence: String, language: String): List[GfExpr] = ???
}
