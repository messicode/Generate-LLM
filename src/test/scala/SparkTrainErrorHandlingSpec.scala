import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SparkTrainErrorHandlingSpec extends AnyFlatSpec with Matchers {

  "SparkTrain" should "throw an error for invalid paths" in {
    val sparkTrain = new SparkTrain()

    val invalidTextPath = "invalid_path.txt"
    val invalidEmbPath = "invalid_embeddings.csv"

    val thrown = intercept[Exception] {
      sparkTrain.Trainer(invalidTextPath, invalidEmbPath)
    }
    thrown.getMessage should include("No such file or directory")
  }
}
