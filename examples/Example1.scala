import info.kwarc.gfmmtbridge


val server = new gfmmtbridge.GfServer(".")
println("Hello")
print(server.getRequest("gf/Gossip.pgf", Map("command" -> "random", "cat" -> "O")))
