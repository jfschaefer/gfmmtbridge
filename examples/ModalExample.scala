import info.kwarc.gfmmtbridge
import info.kwarc.gfmmtbridge.{GfMmtBridge, Runner, ServerGfParser}
import info.kwarc.mmt.api.DPath
import info.kwarc.mmt.api.frontend.Run
import info.kwarc.mmt.api.utils.URI
import info.kwarc.mmt.api.objects.{OMS, Term}
import info.kwarc.mmt.lf.ApplySpine


/*
  The `archivepath` specifies, where mmt theories are stored locally.
  Please it adjust it accordingly.
 */
val archivepath = "/home/jfs/kwarc/mmt/content/MathHub"

/*
  The pgf file contains the GF grammars. It can be generated with `gf -make LifeLexEng.gf`.
  For technical reasons, only a relative path can be provided.
  The following path is relative to the `archivepath` above.
  (If you haven't done so yet, you should clone the repository into the mmt content directory)
 */
val pgfpath = "COMMA/gfbridge/source/lfmtp2019/ModalLex.pgf"  // relative to archivepath


val language = "ModalLexEng"    // name of the concrete grammar used for parsing
val dpath = DPath(URI.http colon "mathhub.info") / "COMMA" / "GfBridge" / "lfmtp2019"




// The sentences used as input
def inputIterator() : Iterator[String] = {
  List("it is possible that it is obligatory that Mary is happy",
    "it isn't permitted that it is possible that John is happy"
  ).iterator
}


// effectively the `main` function
def run() : Unit = {
  // The GF server is started with the working directory `archivepath`.
  // If necessary, a port can also be specified.
  val server = new gfmmtbridge.GfServer(archivepath)

  // The parser uses the server to parse sentences.
  // The `pgfpath` specifies where the pgf grammar is.
  // `pgfpath` has to be relative to the server's working directory.
  val parser = new ServerGfParser(server, pgfpath)

  // The GfMmmtBridge uses the parser to translate sentences into
  // terms using an mmt theory (dpath ? "Example1")
  val bridge = new GfMmtBridge(parser, language, dpath ? "ModalLex", Some(dpath ? "ModalLexSemantics"))
  Run.controller.extman.addExtension(bridge)

  for (input <- inputIterator()) {
    println("You said '" + input + "'")
    val trees = bridge.gf2mmt(input, "S")   // parse with the gf category 'S'

    println("I got the following interpretations:")
    for (tree <- trees) {
      println(bridge.present(tree))
    }

    val trees2 = bridge.gf2mmt(input, "S", false)   // parse with the gf category 'S'
    println("I got the following unsimplified interpretations:")
    for (tree <- trees2) {
      println(bridge.present(tree))
    }
  }
}


// The code below sets up mmt and then calls the `run` method.
// Hopefully, you won't have to change anything here.
val runner = new Runner(() => run(),
  archivepath,
  List("gf"),
  "",
  Some(8080),
  false,
  None
)

runner.launch()
