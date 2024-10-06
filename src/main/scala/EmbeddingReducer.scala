import com.typesafe.config.ConfigFactory
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.api.ndarray.INDArray
import java.io.PrintWriter
import org.apache.hadoop.fs.{Path,FileSystem}
import org.apache.hadoop.conf.Configuration
import scala.jdk.CollectionConverters.*
import org.slf4j.LoggerFactory


class EmbeddingReducer extends Reducer[Text, Text, Text, Text] {
  private val outputPath=ConfigFactory.load().getString("app.outputPath")
  private val logger = LoggerFactory.getLogger(classOf[EmbeddingMapper])
  //Need to collect all output embeddings
  private val outputList = new scala.collection.mutable.ListBuffer[(Text, Text)]()

  override def reduce(key: Text, values: java.lang.Iterable[Text], context: Context): Unit = {

    val vectors: List[INDArray] = values.asScala.toList.map { value =>

      val vectorArray = value.toString.split(",").map(_.toFloat)
      Nd4j.create(vectorArray) // Create INDArray from the float array
    }

    // Calculate average vector
    if (vectors.nonEmpty) {
      val sumVector = vectors.reduce((v1,v2)=>v1.add(v2))
      val avgVector = sumVector.div(vectors.size) // Average by dividing by the number of vectors

      // Convert average vector back to CSV format
      val avgVectorCsv = avgVector.data().asFloat().mkString(",")
      context.write(key, new Text(avgVectorCsv)) // Write the token and its averaged vector
      outputList += ((key, new Text(avgVectorCsv)))
    }
  }

  def cleanupRes(context: Context): Unit = {
    logger.info("Cleanup for Embedding reducer")
    val fs = FileSystem.get(new Configuration())
    val outputFilePath = new Path(outputPath + "/final_embeddings.csv")
    val writer = new PrintWriter(fs.create(outputFilePath,true))
    writer.println("Token,Embeddings")

    outputList.foreach { case (key, value) =>
      writer.println(s"${key.toString},${value.toString}")
    }

    writer.close()
  }
}
