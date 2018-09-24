import info.kwarc.gfmmtbridge
import info.kwarc.gfmmtbridge.{GfMmtBridge, Runner, ServerGfParser}
import info.kwarc.mmt.api.DPath
import info.kwarc.mmt.api.frontend.Run
import info.kwarc.mmt.api.utils.URI


val archivepath = "/home/jfs/kwarc/mmt/content/MathHub"


// Run.controller.handleLine("log console")
// // if (logfile.isDefined) controller.handleLine("log html " + logfile.get)// /home/raupi/lmh/mmtlog.txt")
// // ("test" :: logprefixes) foreach (s => controller.handleLine("log+ " + s))
// Run.controller.handleLine("extension info.kwarc.mmt.lf.Plugin")
// Run.controller.handleLine("extension info.kwarc.mmt.odk.Plugin")
// Run.controller.handleLine("extension info.kwarc.mmt.pvs.Plugin")
// // controller.handleLine("extension info.kwarc.mmt.metamath.Plugin")
// Run.controller.handleLine("mathpath archive " + archivepath)
// // controller.handleLine("extension info.kwarc.mmt.api.ontology.AlignmentsServer " + alignmentspath)

def run() : Unit = {
    val server = new gfmmtbridge.GfServer(".")
    // val parser = new ServerGfParser(server, "gf/Gossip.pgf")
    val parser = new ServerGfParser(server, "../mmt/content/MathHub/Teaching/LBS/source/Frag1/frag1Syn.pgf")
    val bridge = new GfMmtBridge(parser, "frag1SynEN", (DPath(URI.http colon "mathhub.info") / "Teaching" / "LBS") ? "Frag4")
    Run.controller.extman.addExtension(bridge)

    val trees = bridge.gf2mmt("the student ate the dog .", "S")

    // val trees = parser.parse("John loves Mary and John loves John", "GossipEng", "O")
    //
    for (tree <- trees) {
        println("tree: " + tree.toString)
    }
}


val runner = new Runner(() => run(),
    "~/kwarc/mmt/content/MathHub",
    List("gf"),
    "",
    Some(8080),
    true,
    Some("/tmp/mmtlog.html")
)

runner.launch()
