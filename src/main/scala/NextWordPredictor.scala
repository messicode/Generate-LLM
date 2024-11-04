import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType, IntArrayList}
import com.knuddels.jtokkit.Encodings
import scala.io.Source
import java.io.File
import scala.util.Try

class NextWordPredictor(modelPath: String, embdPath: String) {

  // Load the pre-trained model from disk
  private val model: MultiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(new File(modelPath))

  // Load embeddings using FetchEmbd
  private val fetchEmbd = new FetchEmbd()
  private val embeddings: Map[Int, Array[Double]] = fetchEmbd.loadEmbds(embdPath)

  // Set up tokenization and encoding
  private val encodingRegistry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
  private val encoder: Encoding = encodingRegistry.getEncoding(EncodingType.CL100K_BASE)

  /**
   * Tokenize input text, fetch embeddings, and convert to INDArray for model input.
   */
  private def tokenizeAndEmbed(context: Array[String]): INDArray = {
    val tokenIds = context.flatMap(word => encoder.encode(word).toArray)
    val contextEmbeddings = fetchEmbd.getContextEmbeddings(tokenIds, embeddings)
    Nd4j.create(contextEmbeddings)
  }

  /**
   * Generate the next word based on the context input using the loaded model.
   *
   * @param context Array of words representing the input context.
   * @return Predicted next word as a String.
   */
  def generateNextWord(context: Array[String]): String = {
    val contextEmbedding = tokenizeAndEmbed(context)  // Get context embeddings
    val output = model.output(contextEmbedding)       // Predict the next word embedding

    // Get the predicted token ID with the highest probability
    val predictedTokenId = Nd4j.argMax(output, 1).getInt(0)

    val intArrayList = new IntArrayList()
    intArrayList.add(predictedTokenId)
    // Convert token ID back to the word using encoder
    encoder.decode(intArrayList)
  }

  /**
   * Generate a full sentence based on the seed text and model predictions.
   *
   * @param seedText Initial input string to start the sentence.
   * @param maxWords Maximum number of words to generate.
   * @return Generated sentence as a String.
   */
  def generateSentence(seedText: String, maxWords: Int): String = {
    val generatedText = new StringBuilder(seedText)
    var context = seedText.split(" ")

    for (_ <- 1 to maxWords) {
      val nextWord = generateNextWord(context)
      generatedText.append(" ").append(nextWord)
      context = generatedText.toString().split(" ")

      // Break if end token or punctuation is generated
      if (nextWord == "." || nextWord == "END") return generatedText.toString()
    }

    generatedText.toString()
  }
}

// Sample usage of NextWordPredictor
class PredictNextWordApp {
  def predictNextWordApp(modelPath: String, embdPath: String): Unit = {

    val predictor = new NextWordPredictor(modelPath, embdPath)

    // Test sentence generation
    val seedText = "The cat"
    val generatedSentence = predictor.generateSentence(seedText, maxWords = 5)
    println(s"Generated Sentence: $generatedSentence")
  }
}
