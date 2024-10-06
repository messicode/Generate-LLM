import java.io.{PrintWriter,File}

class StatsGen {
  def saveEmbeddings(embeddings: List[Array[Double]], vocab: List[String], filePath: String): Unit = {
    val writer = new PrintWriter(new File(filePath))
    writer.println("Token,Embedding")
    for (i <- vocab.indices) {
      writer.println(s"${vocab(i)},${embeddings(i).mkString(",")}")
    }
    writer.close()
  }
}
