import org.apache.hadoop.io.{Text, IntWritable}
import org.apache.hadoop.mapreduce.Reducer
import scala.compiletime.uninitialized
import com.typesafe.config.ConfigFactory
import java.io.PrintWriter
import org.apache.hadoop.fs.{Path,FileSystem}
import org.apache.hadoop.conf.Configuration
import org.slf4j.LoggerFactory
import scala.jdk.CollectionConverters._

class TokenizationReducer extends Reducer[Text, IntWritable, Text, IntWritable] {

  private val config = ConfigFactory.load()
  private val outputPath = config.getString("app.outputPath")
  private val logger=LoggerFactory.getLogger(classOf[TokenizationReducer])
  //Need to collect all output tokens
  private val outputList = new scala.collection.mutable.ListBuffer[(Text, IntWritable)]()

  override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
//    logger.info("In Tokenization Reduce....")
    val count = values.asScala.foldLeft(0)((total, value) => total + value.get())
    context.write(key, new IntWritable(count))   // Emit token and its aggregated count

    val fs = FileSystem.get(new Configuration())
    val outputFilePath = new Path(outputPath + "/token_counts.csv")
    val writer = new PrintWriter(fs.create(outputFilePath, true))
    writer.println("Token,Count") //Headings
    writer.println(s"${key.toString},${count.toString}")
    writer.close() // Close the writer

  }



}
