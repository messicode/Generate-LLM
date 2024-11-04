import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator
import scala.jdk.CollectionConverters._
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory

class EmbeddingMapper extends Mapper[LongWritable, Text, Text, Text] {
  private val logger = LoggerFactory.getLogger(classOf[EmbeddingMapper])
  private val config = ConfigFactory.load()
  private val embdDim = config.getInt("app.embdDim")
  private val minFreq = config.getInt("app.MinWordFreq")
  private val numEpochs = config.getInt("app.numEpochs")
  private val winSize = config.getInt("app.windowSize")

  private val sentences = scala.collection.mutable.ListBuffer[String]()


  def map(key: LongWritable, value: Text, context: Context): Unit = {
    val tokens = value.toString.split("\\s+")
    sentences ++= tokens.toList

  }

  def cleanupRes(context: Context): Unit = {
    logger.info("Cleanup for Embedding mapper")
    val word2Vec = new Word2Vec.Builder()
      .minWordFrequency(minFreq)
      .iterations(numEpochs)
      .layerSize(embdDim)
      .windowSize(winSize)
      .iterate(new CollectionSentenceIterator(sentences.asJava))
      .build()
    word2Vec.fit() // Train the Word2Vec model with all collected tokens

    // Emit word vectors for each token
    for (word <- sentences) {
      val vector = word2Vec.getWordVector(word).mkString(" ")
      context.write(new Text(word), new Text(vector))
    }
  }

}
