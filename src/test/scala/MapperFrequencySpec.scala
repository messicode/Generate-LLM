import org.apache.hadoop.mapreduce.Mapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MapperFrequencySpec extends AnyFlatSpec with Matchers {
  val mapper = new Mapper()

  "Mapper" should "map tokens to their frequency counts" in {
    val tokens = List("hello", "world", "hello")
    val expectedMap = Map("hello" -> 2, "world" -> 1)
    mapper.map(tokens) should equal(expectedMap)
  }
}
