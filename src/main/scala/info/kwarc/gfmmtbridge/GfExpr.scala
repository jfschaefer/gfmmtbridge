package info.kwarc.gfmmtbridge



sealed abstract class GfExpr {

}

case class GfFun(fun : String, args : List[GfExpr]) extends GfExpr {
    override def toString: String =
        fun + '(' + args.map(x => x.toString()).mkString(", ") + ')'
}
