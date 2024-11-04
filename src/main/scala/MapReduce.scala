import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, TextOutputFormat}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}

import scala.io.Source
import java.io.PrintWriter



object MapReduce{
  val config = ConfigFactory.load()

  def main(args: Array[String]): Unit = {

    val logger = LoggerFactory.getLogger(MapReduce.getClass)
    //Fetch consts from config

    val dynInputPath = new Path(args(0))
    val dynOutputPath = new Path(args(1))
    val dynEmbdPath = new Path(args(2))


    val windowSize = config.getInt("app.windowSize")
    val shift = config.getInt("app.shift")
    val embdDim = config.getInt("app.embdDim")

    logger.info(s"$dynInputPath")
    logger.info(s"$dynOutputPath")
    logger.info(s"$dynEmbdPath")
    //Setting Hadoop
//    logger.info("starting hconf...")
    val hconf = new Configuration()
    //    logger.info(s"hconf done $hconf")
    //    hconf.set("outputPath", outputPath)
    //    logger.info(s"hconf done2 $hconf")

    //Create MRJob for Tokenization
//    logger.info("Tokenizing....")
//    val tokenizationJob = Job.getInstance(hconf, "Tokenization Job")
//    tokenizationJob.setJarByClass(MapReduce.getClass)
//
//    tokenizationJob.setMapperClass(classOf[TokenizationMapper])
//    tokenizationJob.setReducerClass(classOf[TokenizationReducer])
//
//    tokenizationJob.setMapOutputKeyClass(classOf[Text])
//    tokenizationJob.setMapOutputValueClass(classOf[IntWritable])
//
//    tokenizationJob.setOutputKeyClass(classOf[Text])
//    tokenizationJob.setOutputValueClass(classOf[IntWritable])
//
//    tokenizationJob.setInputFormatClass(classOf[TextInputFormat])
//    tokenizationJob.setOutputFormatClass(classOf[TextOutputFormat[Text, IntWritable]])
//
//    FileInputFormat.addInputPath(tokenizationJob, new Path(dynInputPath.toString))
//    FileOutputFormat.setOutputPath(tokenizationJob, new Path(dynOutputPath.toString + "/tokenization_output"))
//
//    if (!tokenizationJob.waitForCompletion(true)) {
//      System.exit(1)
//    }
//    logger.info("Done")
    
    //Create MRJob for Embeddings
//    logger.info("Creating Embeddings....")
//    val embeddingJob = Job.getInstance(hconf, "Embedding Job")
//    embeddingJob.setJarByClass(MapReduce.getClass)
//
//    embeddingJob.setMapperClass(classOf[EmbeddingMapper])
//    embeddingJob.setReducerClass(classOf[EmbeddingReducer])
//
//    embeddingJob.setMapOutputKeyClass(classOf[Text])
//    embeddingJob.setMapOutputValueClass(classOf[Text])
//
//    embeddingJob.setOutputKeyClass(classOf[Text])
//    embeddingJob.setOutputValueClass(classOf[Text])
//
//    embeddingJob.setInputFormatClass(classOf[TextInputFormat]) //Mapper
//    embeddingJob.setOutputFormatClass(classOf[TextOutputFormat[Text, Text]]) //Reducer
//
//    FileInputFormat.addInputPath(embeddingJob, new Path(dynOutputPath.toString + "/shards"))
//    FileOutputFormat.setOutputPath(embeddingJob, new Path(dynOutputPath.toString + "/final_output"))
//

    //Exiting when all done
//    if (!embeddingJob.waitForCompletion(true)) {
//      System.exit(1)
//    }
//    logger.info("Done")
    //
    //    // Create instances of the classes
    //    val dataPrep = new DataPrep()
    //    val w2v = new W2Vec()
    //    val statsGen = new StatsGen()
    //
    //    // Step 1: Split the corpus into shards
    //    val shards = dataPrep.splitCorpus()
    //    logger.info(s"Shards: ${shards}")
    //    // Step 2: Tokenize the shards
    //    val tokenizedShards = dataPrep.tokenize(shards)
    //    logger.info(s"tokenizedShards: ${tokenizedShards}")
    //    // Step 3: Create sliding windows
    //    val slidingWindows = dataPrep.createSlidingWindows(tokenizedShards, windowSize, shift)
    //    logger.info(s"slidingWindows: ${slidingWindows}")
    //    // Step 4: Define vocabulary size 
    //    val vocab=tokenizedShards.flatten.distinct
    //    // Step 5: Train Word2Vec model to get embeddings
    //    val word2VecModel:Word2Vec=w2v.trainWord2Vec(slidingWindows,vocab)
    //    // Step 6: Save the embeddings
    //    val embeddings=vocab.map(word=>word2VecModel.getWordVector(word))
    //    statsGen.saveEmbeddings(embeddings, vocab, outputPath + "embeddings.csv")

//    logger.info("Embeddings saved successfully.")

//------------------------------------------------------------------------------------
    //Embeddings auto supplied and LLM model training

    val sparkTrain = new SparkTrain()
    //Fetch embeddings and train LLM on these
    sparkTrain.Trainer(dynInputPath.toString,dynEmbdPath.toString)

//    val prediction=new PredictNextWordApp()


  }
}


//  Redundant for now
//  def createShardsFromTokenCounts(tokenCountsPath: String, shardsOutputPath: String): Unit = {
//    val lines = Source.fromFile(tokenCountsPath).getLines().drop(1) // Skip heading
//    var shard = List[String]()
//    var shardCount = 0
//
//    val shardSize = config.getInt("app.shardSize") // Adjust this size as needed
//    val fs = FileSystem.get(new Configuration())
//
//    for (line <- lines) {
//      shard = shard :+ line.trim
//      if (shard.size >= shardSize) {
//        val shardPath = new Path(s"$shardsOutputPath/shard_$shardCount.txt")
//        val writer = new PrintWriter(fs.create(shardPath, true))
//        for (line <- shard) {
//          writer.println(line)
//        }
//        writer.close()
//        shard = List[String]()
//        shardCount += 1
//      }
//    }
//
//    // Edge case: Write remaining tokens to a shard
//    if (shard.nonEmpty) {
//      val shardPath = new Path(s"$shardsOutputPath/shard_$shardCount.txt")
//      val writer = new PrintWriter(fs.create(shardPath, true))
//      for (line <- shard) {
//        writer.println(line)
//      }
//      writer.close()
//    }
//  }
//}