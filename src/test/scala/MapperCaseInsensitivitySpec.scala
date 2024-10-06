import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import org.apache.hadoop.mapreduce.Mapper
class MapperCaseInsensitivitySpec extends AnyFlatSpec with Matchers {
  val mapper = new Mapper()

  "Mapper" should "map case-insensitive tokens correctly" in {
    val tokens = List("Hello", "hello", "world")
    val expectedMap = Map("hello" -> 2, "world" -> 1)
    mapper.map(tokens) should equal(expectedMap)
  }
}