package info.kwarc.gfmmtbridge

import info.kwarc.mmt.api.MPath
import info.kwarc.mmt.api.frontend.{Extension, Run}
import info.kwarc.mmt.api.modules.DeclaredTheory
import info.kwarc.mmt.api.objects._
import info.kwarc.mmt.api.symbols.{Constant, FinalConstant}

import scala.collection.immutable.HashMap

class GfMmtBridge(gfParser: GfParser, language : String, mpath : MPath) extends Extension {
    println("MPATH: " + mpath)
    override def logPrefix: String = "gf"
    def present(tm : Term) = controller.presenter.asString(tm)

    lazy val theory : DeclaredTheory = controller.getO(mpath) match {
        case Some(th : DeclaredTheory) => th
        case None => ???
    }

    lazy val theorymap : Map[String, Constant] = {
        controller.simplifier(theory)
        val consts = theory.getConstants ::: theory.getIncludesWithoutMeta.map(controller.get).collect {
            case t : DeclaredTheory =>
                controller.simplifier(t)
                t.getConstants
        }.flatten
        HashMap(consts.map(c => (c.name.toString,c)):_*)
    }


    private val trav = new StatelessTraverser {
        override def traverse(t: Term)(implicit con: Context, state: State): Term = t match {
            case OMS(gn) => controller.getO(gn) match {
                case Some(fc : FinalConstant) if fc.df.isDefined => Traverser(this,fc.df.get)
                case _ => t
            }
            case _ => Traverser(this,t)
        }
    }

    def gf2mmt(sentence : String, cat : String) : List[Term] = {
        gfParser.parse(sentence, language, cat)
                .map(_.toOMDocRec(theorymap))
                .map(controller.simplifier.apply(_, theory.getInnerContext))
                .distinct
/*
        for (tree <- trees) {
            println("Tree: " + tree)
            println("  - simplified: " + controller.simplifier.apply(trav(tree, Context.empty), theory.getInnerContext))
        }
        */

    }
}
