import info.kwarc.gfmmtbridge
import info.kwarc.gfmmtbridge.ServerGfParser


val server = new gfmmtbridge.GfServer(".")
val parser = new ServerGfParser(server, "gf/Gossip.pgf")

val trees = parser.parse("John loves Mary and John loves John", "GossipEng", "O")

for (tree <- trees) {
  println("tree: " + tree.toString)
}
