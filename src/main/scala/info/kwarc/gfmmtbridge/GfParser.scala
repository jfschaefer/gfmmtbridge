package info.kwarc.gfmmtbridge

trait GfParser {
    def parse(sentence : String, language : String) : List[GfExpr]
    def linearize(expr : GfExpr, language : String) : String
}
