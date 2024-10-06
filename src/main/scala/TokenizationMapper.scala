import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.slf4j.LoggerFactory



class TokenizationMapper extends Mapper[LongWritable, Text, Text, IntWritable] {
  private val dataPrep = new DataPrep()
  val logger = LoggerFactory.getLogger(classOf[TokenizationMapper])


  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, IntWritable]#Context): Unit = {

    logger.info(s"Tokenization Mapper....${value.toString.take(100)}")
    val shards=dataPrep.splitCorpus(value.toString)
    logger.info(s"Shards generated: ${shards.size}")
    // Tokenize each shard and emit tokens
    val tokens = dataPrep.tokenize(shards)

    if (tokens != null) {
      tokens.flatten.foreach(token => context.write(new Text(token), IntWritable(1)))
    } // Emit each token
    else
      {
        logger.info("Tokens null in tokenization")
      }
  }
}
