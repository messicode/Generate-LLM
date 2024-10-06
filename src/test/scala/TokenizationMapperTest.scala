import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TokenizerBasicSpec extends AnyFlatSpec with Matchers {
  val tokenizer = new Tokenizer()

  "Tokenizer" should "tokenize a simple sentence correctly" in {
    val text = "Hello world!"
    val expectedTokens = List("hello", "world")
    tokenizer.tokenize(text) should equal(expectedTokens)
  }
}
