package info.kwarc.gfmmtbridge

import info.kwarc.mmt.api.MPath
import info.kwarc.mmt.api.frontend.{Extension, Run}
import info.kwarc.mmt.api.modules.DeclaredTheory
import info.kwarc.mmt.api.objects.Term

class GfMmtBridge(gfParser: GfParser, language : String, mpath : MPath) extends Extension {
    override def logPrefix: String = "gf"
    private def present(tm : Term) = controller.presenter.asString(tm)

    lazy val theory : DeclaredTheory = controller.getO(mpath) match {
        case Some(th : DeclaredTheory) => th
        case None => ???
    }

    def gf2mmt(sentence : String, cat : String) : List[Term] = {
        gfParser.parse(sentence, language, cat)
                .map(_.toOMDocRec(theory))
                .map(controller.simplifier.apply(_, theory.getInnerContext))
                .distinct
    }
}
