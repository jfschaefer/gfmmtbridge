package info.kwarc.gfmmtbridge

sealed abstract class GfExpr {

}

case class GfFun(fun : String, args : List[GfExpr]) {

}
