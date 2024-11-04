import org.slf4j.LoggerFactory

import scala.io.Source


class FetchEmbd extends Serializable{
  private val logger = LoggerFactory.getLogger(this.getClass)
  //Pick emds from file
  def loadEmbds(path: String) : Map[Int, Array[Double]] = {
    logger.info(s"Fetching Embeddings from: $path/embeddings.csv")
    val source=Source.fromFile(path)
    try{
      val lines = source.getLines().drop(1)
      lines.map { line =>
        val parts = line.split(",")
        val tokenId = parts.head.toInt
        val embedding = parts.tail.map(_.toDouble)
        (tokenId, embedding)
      }.toMap
    }
    finally {
      source.close()
    }
  }

  def getContextEmbeddings(window: Array[Int], embeddings: Map[Int, Array[Double]]): Array[Array[Double]] = {
//    print("\nWINDOW vs embedding-------------------------\n")
//    println(s"Window: ${window.mkString(", ")}")
//    println(s"Embeddings: ${embeddings.map { case (key, value) => s"$key -> ${value.mkString("[", ", ", "]")}" }.mkString(", ")}")
//    print("\nenddddddddddd/d-----------------------------\n")
    window.map(token => embeddings.getOrElse(token, Array.fill(100)(0.0)))
  }
}
