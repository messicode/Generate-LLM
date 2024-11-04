
import com.typesafe.config.ConfigFactory
import org.apache.spark.rdd.RDD
import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType}
import com.knuddels.jtokkit.Encodings
import org.nd4j.linalg.schedule.{ExponentialSchedule, ScheduleType}
import scala.util.matching.Regex
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer
import org.deeplearning4j.nn.conf.{MultiLayerConfiguration, NeuralNetConfiguration}
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Adam
import org.deeplearning4j.spark.parameterserver.training.SharedTrainingMaster
import org.apache.spark.{SparkConf, SparkContext}
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration
import org.slf4j.LoggerFactory

import java.io.File
import scala.jdk.CollectionConverters._

// Define a case class to hold input-target pairs for each sliding window all token ids returned
case class WindowedData(input: Array[Int], target: Int)


class SparkTrain extends Serializable {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def Trainer(textPath:String, embdPath:String=""): Unit = {
    // Set up Spark configuration and context

//    val conf = new SparkConf().setAppName("Sliding Window Dataset").setMaster("local[*]")
    val conf = new SparkConf().setAppName("Sliding Window Dataset").setMaster("yarn")
    val sc = new SparkContext(conf)

    val voidConfig = new VoidConfiguration()
    voidConfig.setControllerAddress("127.0.0.1")

    // input data (sentences as strings) and Parallelize (convert array to an RDD)
    val textRDD: RDD[String] = sc.textFile(textPath)
    val sentenceRDD: RDD[String] = textRDD.flatMap(line => splitIntoSentences(line))

    // Set window size (number of tokens in each input window)
    val configVal = ConfigFactory.load()
    val windowSize = configVal.getInt("app.windowSize")
    val batchSize = configVal.getInt("app.batchSize")
    val embdDim = configVal.getInt("app.embdDim")
    val lr = configVal.getDouble("app.learningRate")
    val miniBatch = configVal.getInt("app.miniBatch")
    val hiddenLayerSize = configVal.getInt("app.hiddenLayerSize")
    val workersPerNode = configVal.getInt("app.workersPerNode")
    val numEpochs = configVal.getInt("app.numEpochs")
    val modelName = configVal.getString("app.modelName")
    val decayRate = configVal.getDouble("app.decayRate")
    logger.info("Generating sliding windows --------------------DATASET-------------------------")
    // Apply the sliding window logic to create the dataset
    val slidingWindowDataset: RDD[WindowedData] = sentenceRDD.flatMap(sentence => createSlidingWindows(sentence, windowSize))
//    logger.info(s"DATASET:${slidingWindowDataset.count()}")

    //TEST

    val slidingWindows = slidingWindowDataset.collect()

    if (slidingWindows.isEmpty) {
      logger.info("slidingWindowDataset is empty.")
    } else {
      logger.info("Contents of slidingWindowDataset:")
      slidingWindows.foreach { window =>
        // Print the entire window or specific properties
        logger.info(s"Input: ${window.input.mkString(", ")}, Target: ${window.target}") // Adjust this if you want specific formatting
        // If WindowedData has specific properties you want to print:
        // println(s"Input: ${window.input.mkString(", ")}, Target: ${window.target.mkString(", ")}")
      }
    }
    //OVER

//    // Collect and print the results (for demonstration)
//    slidingWindowDataset.collect().foreach { window =>
//      println(s"Input: ${window.input.mkString(" ")}")
//      println(s"Target: ${window.target}")
//    }

    //Check if embeddings passed
    if(embdPath != "")
      {
       logger.info("Fetching embeddings.........")
        //Fetch embds from file
        val fetchEmbds=new FetchEmbd()
        val embeddings: Map[Int, Array[Double]] = fetchEmbds.loadEmbds(embdPath)
//        logger.info(s"Fetched embeddings of shape: [ ${embeddings.size}, ${if (embeddings.nonEmpty) embeddings.head._2.length else 0}")
//        embeddings.foreach { case (id, embedding) =>
//          logger.info(s"$id -> ${embedding.mkString("[", ", ", "]")}")
//        }
        logger.info("Generating sliding windows of embeddings for training")
        // Convert sliding windows to RDD[DataSet] for training and skip empty sets using flatmap seq


        val dataSetList: RDD[DataSet] = slidingWindowDataset.flatMap { window =>
            //Input/target window embeddings
            print(s"\nINSIDE DATASET:$window\n")
            val inputEmbds=fetchEmbds.getContextEmbeddings(window.input,embeddings).flatten
            val targetEmbedding = embeddings.getOrElse(window.target, Array.fill(100)(0.0))
            //SHapes
//            print(s"Shape of inputEmbds: (${inputEmbds.length})\n")
//            print(s"Shape of targetEmbedding: ${targetEmbedding.length}\n")

            if (inputEmbds.isEmpty || targetEmbedding.isEmpty) {
              logger.info("Skipping empty dataset")
              Seq.empty[DataSet]
            }
            else
            {
              logger.info(s"Embedding for ${window.input.mkString("Array(", ", ", ")")} is : ${inputEmbds.mkString("Array(", ", ", ")")}")
              val input = Nd4j.create(inputEmbds).reshape(1,embdDim*windowSize) // Replace with embeddings if available
              val target = Nd4j.create(Array(targetEmbedding)) // Replace with embeddings if available
              logger.info(s"INPUT: $input AND its TARGET: $target")
              Seq(new DataSet(input, target))
            }

        }

//        Needs to be updated for batch iteration over RDDdataset
//        val dataSetIterator = new ListDataSetIterator(dataSetList, batchSize)
        logger.info("Creating model...........")
        val lrSchedule = new ExponentialSchedule(ScheduleType.EPOCH, lr, decayRate)//Decaying learning rate
        val adamWithExponentialDecay = new Adam(lrSchedule)
        val nnModel: MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
          .updater(adamWithExponentialDecay)//Optimizer with lr
          .seed(42)//Random seed value
          .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
          .weightInit(WeightInit.XAVIER)//Initial weights
          .list()
          .layer(new DenseLayer.Builder()
            .nIn(windowSize * embdDim) // Input size: windowSize * embedding size
            .nOut(hiddenLayerSize) // Hidden layer size
            .activation(Activation.RELU)
            .build())
          .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE) // Mean Squared Error for regression
            .nIn(hiddenLayerSize)
            .nOut(embdDim) // Output size: embedding vector size
            .activation(Activation.IDENTITY) // No activation, output is a vector
            .build())
          .build()

        logger.info("Configuring training Master")
        val trainingMaster = new SharedTrainingMaster.Builder(voidConfig,miniBatch)
          .batchSizePerWorker(miniBatch)
          .workersPerNode(workersPerNode) // Define number of workers per node
          .build()

        // Create SparkDl4jMultiLayer model
        val model = new SparkDl4jMultiLayer(sc, nnModel, trainingMaster)
        model.setListeners(new ScoreIterationListener(10))
        logger.info("Training the LLM....")
        for (epoch <- 1 to numEpochs) {

          val startEpochTime = System.currentTimeMillis()
          logger.info(s"Starting epoch $epoch")
          model.fit(dataSetList)//Using RDD directly for now
          val endEpochTime = System.currentTimeMillis()
          logger.info(s"Epoch $epoch complete.")
          // Print current learning rate
          val currentLearningRate = model.getNetwork.getLearningRate(0)
          logger.info(s"Learning rate for epoch $epoch: $currentLearningRate")
          logger.info(s"Time for $epoch:${endEpochTime-startEpochTime}ms")
        }
        logger.info(s"Save the model to location: $textPath")
//        Save the trained llm model
        model.getNetwork.save(new File(textPath+modelName), true)
      }

    // Stop the Spark context
    sc.stop()
  }

  def splitIntoSentences(line: String): Array[String] = {
    val sentenceEnd: Regex = "[.!?]".r
    sentenceEnd.split(line).map(_.trim).filter(_.nonEmpty)
  }

  //Funcn to create sliding window set
  def createSlidingWindows(sentence: String, windowSize: Int): Seq[WindowedData] = {
    // Tokenize the sentence into words

    logger.info("HELLLLOOOOO CREATE SLIDESSSSSSSSSSSSSSSSSSSSS-------------")

    val reg: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    val enc: Encoding = reg.getEncoding(EncodingType.CL100K_BASE)

    val tokens = sentence.split(" ")
    // Buffer to hold windowed data
    val encodedTokenIds = tokens.flatMap(word => enc.encode(word).toArray)

    val windowedDataList = scala.collection.mutable.ListBuffer[WindowedData]()


    logger.info("Going in input windows:")
    // Generate sliding windows
    for (i<-0 until encodedTokenIds.length-windowSize) {

      logger.info("In input windows")
      val inputWindow = encodedTokenIds.slice(i, i + windowSize) // Window of token as input
      val target = encodedTokenIds(i + windowSize)               // Next word as target

//      print(s"Input window: ${inputWindow.mkString("Array(", ", ", ")")}, Target:$target")
      windowedDataList += WindowedData(inputWindow, target)
//      print("\nSKDBKAJSDJHAKSJDKABSK: \n{")
//      windowedDataList.foreach { windowData =>
//        println(s"Inputssss: ${windowData.input.mkString(", ")}, Target: ${windowData.target}")
//      }
//      print("}\n")
    }
    windowedDataList
  }

}
