package main.scala.info.kwarc.gfmmtbridge
import info.kwarc.gfmmtbridge.GfExpr
import scala.sys.process.Process


class GfServer(location : String, port : Int = 41296) {
    val process = Process(List("gf", "--server=" + port, "--document-root=" + location))
    process.run

    def getRequest(pgfPath : String, options : Map[String, String]) = ???
}


class ServerGfParser extends GfParser {
    override def linearize(expr: GfExpr, language: String): String = ???

    override def parse(sentence: String, language: String): List[GfExpr] = ???
}
