import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.functions._

object WordCount{
    def main(arg: Array[String]): Unit = {
        val spark = SparkSession.builder.appName("wordCount").master("local[*]").getOrCreate()
        val lines = spark.readStream.format("socket").option("host", "localhost").option("port", 9999).load()
        import spark.implicits._
        val words = lines.as[String].flatMap(_.split(" "))
        val wordCount = words.groupBy("value").count()
        val query = wordCount.writeStream.outputMode("complete").format("console").start()
        query.awaitTermination()
    }
}