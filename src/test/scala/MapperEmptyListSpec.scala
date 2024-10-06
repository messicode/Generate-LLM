import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.hadoop.mapreduce.Mapper
class MapperEmptyListSpec extends AnyFlatSpec with Matchers {
  val mapper = new Mapper()

  "Mapper" should "handle empty token lists" in {
    val tokens = List()
    val expectedMap = Map.empty[String, Int]
    mapper.map(tokens) should equal(expectedMap)
  }
}
