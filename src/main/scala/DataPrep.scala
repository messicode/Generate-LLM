//Sharding, Tokenizing, Sliding window for context

import com.knuddels.jtokkit.Encodings

import java.nio.charset.StandardCharsets
import com.typesafe.config.ConfigFactory

import scala.io.Source
import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType}
import org.slf4j.LoggerFactory
import org.threeten.bp.format.TextStyle

class DataPrep {
  //Logger & config fetching
  private val logger = LoggerFactory.getLogger(classOf[DataPrep])
  private val config = ConfigFactory.load()

  //Fetch Encoding registry
  private val reg:EncodingRegistry= Encodings.newDefaultEncodingRegistry()
  private val enc:Encoding= reg.getEncoding(EncodingType.CL100K_BASE)

  //From config
  private val shardSize:Int=config.getInt("app.shardSize")
  val filePath:String= config.getString("app.inputPath")

  def splitCorpus(text:String): List[String] = {
    try{
      // Split text into shards
      logger.info("Splitting text into shards")
      //    val source = Source.fromFile(filePath)(scala.io.Codec(StandardCharsets.UTF_8))
      val shards = text.grouped(shardSize).toList.map(_.trim)
      shards
    }
    catch{
      case e: Exception=>
        logger.error("Error during splitCorpus", e)
        List.empty[String]
    }
  }

  def tokenize(text: List[String]): List[List[String]] = {

    logger.info(s"Tokenizing shards ${text}")
    text.map { t =>
      val shard=t.trim.replaceAll("\\s+", " ")
      logger.debug(s"Tokening shard $shard")
      val encd = enc.encode(shard)  //Int encodings
      logger.info(s"Encodings done: $encd")
      val tokens = encd.toArray.toList.map(_.toString) //Encodings converted to string
//      logger.debug(s"Enc Tokens: $tokens")
      tokens
    }
  }

//  def createSlidingWindows(tokenizedShards: List[List[String]], windowSize: Int, shift: Int): List[List[String]] = {
//    logger.info(s"Creating sliding windows with window size $windowSize and shift $shift.")
//    tokenizedShards.flatMap { tokens =>
//      tokens.sliding(windowSize, shift).toList
//    }
//  }
}
