import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FetchEmbdEdgeCasesSpec extends AnyFlatSpec with Matchers {

  "FetchEmbd" should "handle empty embeddings gracefully" in {
    val fetchEmbd = new FetchEmbd()
    val window = Array(1, 2, 3)
    val embeddings = Map.empty[Int, Array[Double]]

    val contextEmbeddings = fetchEmbd.getContextEmbeddings(window, embeddings)

    contextEmbeddings should have size 3
    contextEmbeddings.forall(_.sameElements(Array.fill(100)(0.0))) should be (true)
  }
}
