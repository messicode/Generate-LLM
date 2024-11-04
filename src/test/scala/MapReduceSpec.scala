import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MapReduceSpec extends AnyFlatSpec with Matchers {

  "MapReduce" should "initialize correctly with valid arguments" in {
    val args = Array("inputPath", "outputPath", "embeddingPath")

    // Execute the main method
    noException should be thrown by MapReduce.main(args)
  }

  it should "throw an exception with insufficient arguments" in {
    val args = Array("inputPath") // Insufficient arguments

    val thrown = intercept[ArrayIndexOutOfBoundsException] {
      MapReduce.main(args)
    }
    thrown.getMessage should include("Array index out of bounds")
  }
}
