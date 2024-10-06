import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer
class TokenizerPunctuationSpec extends AnyFlatSpec with Matchers {
  val tokenizer = new Tokenizer()

  "Tokenizer" should "ignore punctuation and split correctly" in {
    val text = "Tokenize, this: sentence; properly."
    val expectedTokens = List("tokenize", "this", "sentence", "properly")
    tokenizer.tokenize(text) should equal(expectedTokens)
  }
}
