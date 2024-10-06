
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scala.jdk.CollectionConverters._

class W2Vec {
  def trainWord2Vec(tokenizedData: List[List[String]], vocab: List[String]): Word2Vec = {
    //Logger
    val logger = LoggerFactory.getLogger(classOf[DataPrep])

    //  Padding not required
    //    val mxLength=numData.map(_.length).max
    //    val padData=numData.map { seq =>
    //        val padded = seq.padTo(mxLength, 0.0) // Pad with zeros
    //        padded.take(mxLength) // Ensure the length is not exceeded
    //      }

    //Pick from config
    val configVal = ConfigFactory.load()
    val embdDim = configVal.getInt("app.embdDim")
    val numEpochs = configVal.getInt("app.numEpochs")
    val winSize= configVal.getInt("app.windowSize")
    val minFreq= configVal.getInt("app.MinWordFreq")

    //Map holds vocab to index pairs
    val vocabMap: Map[String, Int] = vocab.zipWithIndex.toMap
    logger.info(s"Vocab Size: ${vocab.size}")
    //    logger.info(s"VOCABBBB:${vocab}")

    val sentences = tokenizedData.map(_.mkString(" ")).asJava
    val sentenceIterator = new CollectionSentenceIterator(sentences)


    val word2Vec = new Word2Vec.Builder()
      .minWordFrequency(minFreq) // Adjust this depending on your corpus size
      .iterations(numEpochs)
      .layerSize(embdDim)
      .windowSize(winSize) // Adjust based on context window size
      .iterate(sentenceIterator) //No need for default tokenizer as data is jtokkit tokenized
      .build()

    //    logger.info("")
    word2Vec.fit()
    word2Vec



    //    Build Word 2 vec
    //    // Convert tokenized data to indices
    //    val indexedData = tokenizedData.map(_.map(vocabMap))
    //
    //    // Flatten the data and create input-output pairs
    //    val flatInput = indexedData.flatten
    //    val inputPairs = flatInput.zipWithIndex.collect {
    //      case (value, index) if index < flatInput.length - 1 => (value, flatInput(index + 1)) // Create pairs (current, next)
    //    }
    //
    //    // Prepare input and labels
    //    val inputFeatures: INDArray = Nd4j.create(inputPairs.map(_._1.toDouble).toArray,Array(inputPairs.length, 1))
    //    val opLabel = inputPairs.map { case (_, label) =>
    //      val oneHot = new Array[Float](vocab.size)
    //      oneHot(label) = 1.0f // Set the correct index to 1
    //      oneHot
    //    }
    //    val outputLabels:INDArray =Nd4j.create(opLabel.flatten.toArray, Array(inputPairs.length, vocab.size))
    //
    //    logger.info(s"Input Features Shape: ${inputFeatures.shape.mkString(", ")}")
    //    logger.info(s"Output Labels Shape: ${outputLabels.shape.mkString(", ")}")
    //
    //    // Configure the model
    //    val config: MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
    //      .list()
    //      .layer(new EmbeddingLayer.Builder().nIn(vocab.size).nOut(embdDim).activation(Activation.IDENTITY).build())
    //      .layer(new OutputLayer.Builder().nIn(embdDim).nOut(vocab.size).activation(Activation.SOFTMAX).build())
    //      .build()
    ////    logger.info("Herehere")
    //    // Initialize and train the model
    //    val model = new MultiLayerNetwork(config)
    //    model.init()
    //
    //
    //    for (_ <- 0 until numEpochs) {
    //      model.fit(inputFeatures, outputLabels)
    //    }
    //
    //    // Extract embeddings
    //    model.getLayer(0).getParam("W").toDoubleMatrix
  }
}
