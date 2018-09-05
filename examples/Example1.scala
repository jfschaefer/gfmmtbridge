import info.kwarc.gfmmtbridge


val server = new gfmmtbridge.GfServer(".")
println("Hello")
println("I'm at: " + System.getProperty("user.dir"))

val trees = server.getRequest("gf/Gossip.pgf", Map("command" -> "parse", "input" -> "John loves Mary and John loves John", "cat" -> "O"))

for (tree <- trees) {
  println("tree: " + tree)
}
