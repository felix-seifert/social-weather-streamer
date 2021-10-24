package com.felixseifert.socialweatherstreamer
package sparkprocessor

import org.apache.spark.sql.{SparkSession, Row, Encoders}
import org.apache.spark.streaming.State
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.functions._
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.Column

case class GeoInfo(
    id: String,
    fullName: String,
    country: String,
    countyCode: String,
    boxCords: List[Double],
    geoType: String
)
case class WeatherInfo(
    last_updated: String,
    temp_c: Double,
    precip_mm: Double,
    humidity: Integer,
    cloud: Integer,
    feelslike_c: Double,
    is_day: Integer
)
case class Tweet(
    id: Long,
    text: String,
    createdAt: String,
    geoInformation: GeoInfo,
    weatherInformation: WeatherInfo
)

object TweetProcessor {
  // Load a sentiment anaysis ml pipeline
  val pipeline = new PretrainedPipeline(
    "analyze_sentimentdl_use_twitter",
    lang = "en"
  )

  def main(args: Array[String]) = {
    val read_topic = "tweets-enriched"
    val write_topic = "correlations"
    val spark = SparkSession
      .builder()
      .config("spark.master", "local")
      .config("spark.sql.streaming.checkpointLocation", "/tmp/")
      .getOrCreate()

    import spark.implicits._

    val schema =
      ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]

    def correlator(sentiment: Double, temp: Double): Double = {
      var t = temp
      t -= -5.0
      t /= 25.0
      t = t match {
        case x if (x < -1.0) => -1.0
        case x if (x > 1.0)  => 1.0
        case x               => x
      }

      t * sentiment
    }

    def mapper(text: Column, wi: Column): Double = {
      val feels_like = 15.0
      // val value = spark.createDataFrame(text);
      // val sentiment = pipeline.transform() match {
      //   case "negative" => -1.0
      //   case "positive" => 1.0
      //   case _          => 0
      // }
      val r = scala.util.Random
      val sentiment = r.nextDouble() * 2 - 1.0
      correlator(sentiment, feels_like)
    }

    // // Read the stream
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", read_topic)
      .load()
      .selectExpr("CAST(value AS STRING)")
      .select(from_json(col("value"), schema).as("data"))
      .select("data.*")

    val correlation = df
      .withColumn(
        "correlation",
        lit(mapper(col("text"), col("weatherInformation")))
      )

    // correlated.writeStream
    // // df.writeStream
    //   .format("console")
    //   .outputMode("update")
    //   .start()
    //   .awaitTermination()

    // Write the results
    correlation.toJSON.writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", write_topic)
      .start()
      .awaitTermination()

  }
}
