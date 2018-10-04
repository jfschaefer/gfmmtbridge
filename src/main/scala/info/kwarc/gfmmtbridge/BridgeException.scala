package info.kwarc.gfmmtbridge

class BridgeException(private val message: String = "",
                           private val cause: Throwable = None.orNull)
    extends Exception(message, cause)


final case class MmtTermMissing(private val message: String = "",
                                private val cause: Throwable = None.orNull)
    extends BridgeException(message, cause)
