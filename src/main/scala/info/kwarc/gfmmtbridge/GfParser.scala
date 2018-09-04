package main.scala.info.kwarc.gfmmtbridge

import info.kwarc.gfmmtbridge.GfExpr

trait GfParser {
    def parse(sentence : String, language : String) : List[GfExpr];
    def linearize(expr : GfExpr, language : String) : String;
}
