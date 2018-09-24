package info.kwarc.gfmmtbridge

import info.kwarc.mmt.api.MPath
import info.kwarc.mmt.api.frontend.{Extension, Run}
import info.kwarc.mmt.api.modules.DeclaredTheory
import info.kwarc.mmt.api.objects.Term
import info.kwarc.mmt.api.symbols.Constant

import scala.collection.immutable.HashMap

class GfMmtBridge(gfParser: GfParser, language : String, mpath : MPath) extends Extension {
    override def logPrefix: String = "gf"
    private def present(tm : Term) = controller.presenter.asString(tm)

    lazy val theory : DeclaredTheory = controller.getO(mpath) match {
        case Some(th : DeclaredTheory) => th
        case None => ???
    }

    lazy val theorymap : Map[String, Constant] = {
        // controller.simplifier(theory)
        val consts = theory.getConstants ::: theory.getIncludesWithoutMeta.map(controller.get).collect {
            case t : DeclaredTheory =>
                // controller.simplifier(t)
                t.getConstants
        }.flatten
        HashMap(consts.map(c => (c.name.toString,c)):_*)
    }



    def gf2mmt(sentence : String, cat : String) : List[Term] = {
        gfParser.parse(sentence, language, cat)
                .map(_.toOMDocRec(theorymap))
                // .map(controller.simplifier.apply(_, theory.getInnerContext))
                .distinct
    }
}
