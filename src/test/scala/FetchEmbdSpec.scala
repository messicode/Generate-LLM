import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{File, PrintWriter}

class FetchEmbdSpec extends AnyFlatSpec with Matchers {

  "FetchEmbd" should "load embeddings from a CSV file" in {
    val filePath = "test_embeddings.csv"
    val writer = new PrintWriter(new File(filePath))
    writer.write("0,0.1,0.2,0.3\n1,1.1,1.2,1.3\n")
    writer.close()

    val fetchEmbd = new FetchEmbd()
    val embeddings = fetchEmbd.loadEmbds(filePath)

    embeddings.size should be (2)
    embeddings(0) should be (Array(0.1, 0.2, 0.3))
    embeddings(1) should be (Array(1.1, 1.2, 1.3))

    new File(filePath).delete() // Clean up
  }

  it should "return default embeddings for missing tokens" in {
    val fetchEmbd = new FetchEmbd()
    val window = Array(0, 1, 2)
    val embeddings = Map(0 -> Array(0.1, 0.2, 0.3))

    val contextEmbeddings = fetchEmbd.getContextEmbeddings(window, embeddings)

    contextEmbeddings.length should be (3)
    contextEmbeddings(0) should be (Array(0.1, 0.2, 0.3))
    contextEmbeddings(1) should be (Array.fill(100)(0.0)) // Default for token 1
    contextEmbeddings(2) should be (Array.fill(100)(0.0)) // Default for token 2
  }
}
