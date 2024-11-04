import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}

class SparkTrainSpec extends AnyFlatSpec with Matchers {

  "SparkTrain" should "initialize and run training without error" in {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Test")
    val sparkContext = new SparkContext(sparkConf)

    val sparkTrain = new SparkTrain()
    val textPath = "test_text.txt"
    val embdPath = "test_embeddings.csv"

    // Create a test file
    val writer = new PrintWriter(textPath)
    writer.write("This is a test sentence.\n")
    writer.close()

    // Test the Trainer method
    noException should be thrownBy sparkTrain.Trainer(textPath, embdPath)

    // Clean up
    sparkContext.stop()
    new File(textPath).delete()
  }

  it should "create sliding windows correctly" in {
    val sparkTrain = new SparkTrain()
    val sentence = "This is a simple test"
    val windowSize = 3
    val result = sparkTrain.createSlidingWindows(sentence, windowSize)

    result should have size 2
    result(0).input should be (Array(0, 1, 2)) // Example token IDs
    result(0).target should be (3) // Example token ID
  }
}
