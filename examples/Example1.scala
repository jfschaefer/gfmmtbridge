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
  The pgf file contains the GF grammars. It can be generated with `gf -make Example1Eng.gf`.
  For technical reasons, only a relative path can be provided.
  The following path is relative to the `archivepath` above.
  (If you haven't done so yet, you should clone the LBS repository into the mmt content directory)
 */
val pgfpath = "Teaching/LBS/source/Example1/Example1.pgf"  // relative to archivepath


val language = "Example1Eng"    // name of the concrete grammar used for parsing
val dpath = DPath(URI.http colon "mathhub.info") / "Teaching" / "LBS"




// The sentences used as input
def inputIterator() : Iterator[String] = {
  List("Hello world",
       "if Prudence loathed Berti then Fiona loathed Berti",
       "Prudence loathed Berti or Chester loathed Berti",
       "it is not the case that Chester loathed Berti",
       "Fiona loathed Berti",
       "it is not the case that Prudence loathed Berti"
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
    val bridge = new GfMmtBridge(parser, language, dpath ? "Example1")
    Run.controller.extman.addExtension(bridge)


    // The knowledge is represented as a set of different possible sets of facts about the world.
    // For example, (A and not B) or C would be represented as {{(A, true), (B, false)}, {(C, true)}}
    // It is updated with new knowledge from every sentence.
    var knowledge : Set[Set[(Term, Boolean)]] = Set(Set())
    for (input <- inputIterator()) {
        println("You said '" + input + "'")
        val trees = bridge.gf2mmt(input, "Sentence")   // parse with the gf category 'Sentence'

        if (trees.isEmpty)   // no parse tree obtained
            println("I don't understand you!")
        else {
            val result = nextSentence(trees, knowledge)  // `nextSentence` returns updated knowledge and a comment.
            knowledge = result._1
            println(result._2)
        }

        // `bridge.present` can be used to get a nicer string representation of terms.
        println("I know already: " + knowledge.map(_.map(t => bridge.present(t._1) + (if (t._2) "ᵗ" else "ᶠ"))))
    }
}



// The update algorithm (`nextSentence`) adds the information of a sentence to the knowledge set.
// The knowledge is represented as a set of different possible sets of facts about the world.
// For example, (A and not B) or C would be represented as {{(A, true), (B, false)}, {(C, true)}}
// A, B and C would be 'atomic' facts like "loath(Fiona, Berti)", i.e. propositions without logical connectives.


// `addReading` is a helper function that recursively decomposes a term to add it to the knowledge set.
// The boolean value indicates, whether the sentence is supposed to be true or false.
def addReading(reading : (Term, Boolean), knowledge : Set[Set[(Term, Boolean)]]) : Set[Set[(Term, Boolean)]] = {
    reading match {
        case (not(x), b) => addReading((x, !b), knowledge)
        // if a AND b is true, then add first a to the knowledge set and then b.
        case (and(a, b), true) => addReading((b, true), addReading((a, true), knowledge))
        // if a AND b is false, then add a to the knowledge set and add b to a copy of the knowledge set and take the union
        case (and(a, b), false) => addReading((a, false), knowledge) ++ addReading((b, false), knowledge)
        // add atomic terms and the truth value to every set in the knowledge set. remove those sets that contain a contradiction
        case atomicterm => removeContradictions(knowledge.map(s => s + atomicterm))

        // Note: implication and disjunction are defined through conjunction and negation in the mmt theory.
        // As these definitions are expanded, we do not need to cover them here anymore.
    }
}

// Removes contradicting sets from the knowledge set.
// Contradictions occur, if both (A, true) and (A, false) occur in the same set in the knowledge set.
def removeContradictions(knowledge : Set[Set[(Term, Boolean)]]) : Set[Set[(Term, Boolean)]] = {
    knowledge.filter(s => !s.exists(t => s.contains(t._1, !t._2)))
}

// Returns a knowledge set that incorporates the readings of a sentence along with a comment.
def nextSentence(readings : List[Term], knowledge : Set[Set[(Term, Boolean)]]) : (Set[Set[(Term, Boolean)]], String) = {
    // for each reading an updated knowledge set is created and the union of all of them is taken.
    val newKnowledge = readings.flatMap(reading => addReading((reading, true), knowledge))
                               .toSet

    if (newKnowledge == knowledge) {  // no changes in knowledge
        (newKnowledge, "That's obvious.")
    } else if (newKnowledge.isEmpty) {    // there must have been a contradiction (no consistent set of facts remaining)
        (Set(Set()), "That doesn't make any sense! Let's start from the beginning.")
    } else {  // new knowledge obtained
        (newKnowledge, "That's interesting.")
    }
}


// These objects allow us to use `and` and `not` for the decomposition of terms as done in `addReading`.
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
