import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WindowedDataSpec extends AnyFlatSpec with Matchers {

  "WindowedData" should "hold input and target correctly" in {
    val input = Array(1, 2, 3)
    val target = 4

    val windowData = WindowedData(input, target)

    windowData.input should be (Array(1, 2, 3))
    windowData.target should be (4)
  }
}
