package info.kwarc.gfmmtbridge

import info.kwarc.mmt.api.LocalName
import info.kwarc.mmt.api.modules.DeclaredTheory
import info.kwarc.mmt.api.objects.Term
import info.kwarc.mmt.api.symbols.Constant
import info.kwarc.mmt.lf.ApplySpine


sealed abstract class GfExpr {
    def toOMDocRec(th : Map[String, Constant]) : Term
}

case class GfFun(fun : String, args : List[GfExpr]) extends GfExpr {
    override def toString: String =
        fun + '(' + args.map(x => x.toString()).mkString(", ") + ')'

    override def toOMDocRec(theorymap : Map[String, Constant]): Term = {
        if (args.isEmpty) {
            getTerm(fun, theorymap)
        } else {
            ApplySpine(getTerm(fun, theorymap), args.map(_.toOMDocRec(theorymap)):_*)
        }
    }

    private def getTerm(s : String, theorymap : Map[String, Constant]) : Term= {
        println("Term: " + s)
        theorymap.get(s) match {
            case Some(c) => c.toTerm
            case None => ???
        }
        /*match {
            case c : Constant if c.df.isDefined => c.df.get
            case c : Constant => c.toTerm
        }*/
    }

//     private def toOMDocRec(expr : GFExpr, th : DeclaredTheory) : Term = expr match {
//         case GFStr(s) => get(s,th)
//         case GFA(fun,args) => ApplySpine(get(fun,th),args.map(toOMDocRec(_,th)):_*)
//     }

}
