import info.kwarc.gfmmtbridge
import info.kwarc.gfmmtbridge.{GfMmtBridge, Runner, ServerGfParser}
import info.kwarc.mmt.api.DPath
import info.kwarc.mmt.api.frontend.Run
import info.kwarc.mmt.api.utils.URI
import info.kwarc.mmt.api.objects.{OMS, Term}
import info.kwarc.mmt.lf.ApplySpine


val archivepath = "/home/jfs/kwarc/mmt/content/MathHub"
val pgfpath = "Teaching/LBS/source/Example1/Example1.pgf"  // relative to archivepath
val language = "Example1Eng"
val dpath = DPath(URI.http colon "mathhub.info") / "Teaching" / "LBS"


def run() : Unit = {
    val server = new gfmmtbridge.GfServer(archivepath)
    val parser = new ServerGfParser(server, pgfpath)
    val bridge = new GfMmtBridge(parser, language, dpath ? "Example1")
    Run.controller.extman.addExtension(bridge)


    var knowledge : Set[Set[(Term, Boolean)]] = Set(Set())
    while (true) {
        Thread.sleep(300)
        println("Please tell me something")
        val input = scala.io.StdIn.readLine().stripLineEnd
        println("You said '" + input + "'")
        val trees = bridge.gf2mmt(input, "Sentence")
        if (trees.isEmpty)
            println("I don't understand you!")
        else {
            val result = nextSentence(trees, knowledge)
            knowledge = result._1
            println(result._2)
        }
      println("I know already: " + knowledge.map(_.map(t => bridge.present(t._1) + (if (t._2) "ᵗ" else "ᶠ"))))
    }
}


def addReading(reading : (Term, Boolean), knowledge : Set[Set[(Term, Boolean)]]) : Set[Set[(Term, Boolean)]] = {
    reading match {
        case (not(x), b) => addReading((x, !b), knowledge)
        case (and(a, b), true) => addReading((b, true), addReading((a, true), knowledge))
        case (and(a, b), false) => addReading((a, false), knowledge) ++ addReading((b, false), knowledge)
        case newterm => removeContradictions(knowledge.map(s => s + newterm))
    }
}

def removeContradictions(knowledge : Set[Set[(Term, Boolean)]]) : Set[Set[(Term, Boolean)]] = {
    knowledge.filter(s => !s.exists(t => s.contains(t._1, !t._2)))
}

def nextSentence(readings : List[Term], knowledge : Set[Set[(Term, Boolean)]]) : (Set[Set[(Term, Boolean)]], String) = {
    val newKnowledge = readings.flatMap(reading => addReading((reading, true), knowledge))
                               .toSet

    if (newKnowledge == knowledge) {
        (newKnowledge, "That's obvious.")
    } else if (newKnowledge.isEmpty) {
        (Set(Set()), "That doesn't make any sense! Let's start from the beginning.")
    } else {
        (newKnowledge, "That's interesting.")
    }
}


  object not {
      val path = dpath ? "Example1Logic" ? "negation"

      def unapply(tm: Term) = tm match {
          case ApplySpine(OMS(`path`), List(tm1)) => Some(tm1)
          case _ => None
      }

      def apply(tm1: Term) = ApplySpine(OMS(path), tm1)
  }

  object and {
      val path = dpath ? "Example1Logic" ? "conjunction"
      def unapply(tm : Term) = tm match {
          case ApplySpine(OMS(`path`),List(tm1,tm2)) => Some((tm1,tm2))
          case _ => None
      }
      def apply(tm1 : Term, tm2 : Term) = ApplySpine(OMS(path),tm1,tm2)
  }


val runner = new Runner(() => run(),
    archivepath,
    List("gf"),
    "",
    Some(8080),
    true,
    Some("/tmp/mmtlog.html")
)

runner.launch()
