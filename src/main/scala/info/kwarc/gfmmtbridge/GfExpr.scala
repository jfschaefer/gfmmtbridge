package info.kwarc.gfmmtbridge

import info.kwarc.mmt.api.LocalName
import info.kwarc.mmt.api.modules.DeclaredTheory
import info.kwarc.mmt.api.objects.Term
import info.kwarc.mmt.api.symbols.Constant
import info.kwarc.mmt.lf.ApplySpine


sealed abstract class GfExpr {
    def toOMDocRec(th : DeclaredTheory) : Term
}

case class GfFun(fun : String, args : List[GfExpr]) extends GfExpr {
    override def toString: String =
        fun + '(' + args.map(x => x.toString()).mkString(", ") + ')'

    override def toOMDocRec(th: DeclaredTheory): Term = {
        if (args.isEmpty) {
            getTerm(fun, th)
        } else {
            ApplySpine(getTerm(fun, th), args.map(_.toOMDocRec(th)):_*)
        }
    }

    private def getTerm(s : String,th : DeclaredTheory) = th.get(LocalName(s)) match {
        case c : Constant if c.df.isDefined => c.df.get
        case c : Constant => c.toTerm
    }

//     private def toOMDocRec(expr : GFExpr, th : DeclaredTheory) : Term = expr match {
//         case GFStr(s) => get(s,th)
//         case GFA(fun,args) => ApplySpine(get(fun,th),args.map(toOMDocRec(_,th)):_*)
//     }

}
