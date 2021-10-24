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
// import com.johnsnowlabs.nlp.base._
// import com.johnsnowlabs.nlp.annotator._
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
    lastUpdated: String,
    temperatureCelsius: Double,
    precipitationMm: Double,
    humidity: Integer,
    feelsLikeCelsius: Double,
    isDay: Integer
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
// val pipeline = new PretrainedPipeline("analyze_sentimentdl_use_twitter", lang = "en")

  def main(args: Array[String]) = {
    val read_topic = "tweets-enriched"
    val write_topic = "correlations"
    val spark = SparkSession
      .builder()
      .config("spark.master", "local")
      .config("spark.sql.streaming.checkpointLocation", "/tmp/")
      .getOrCreate()

    // spark.conf.set("spark.sql.streaming.checkpointLocation", "/tmp/")

    import spark.implicits._

    val schema =
      ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]

    def mapper(text: Column, wi: Column): Double = {
      // val sentiment = pipeline.fullAnnotate match {
      //   case "negative" => -1
      //   case "positive" => 1
      //   case _ => 0
      // }
      val correlation = 0.0
      correlation
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

    val correlated = df
      .withColumn(
        "correlated",
        lit(mapper(col("text"), col("weatherInformation")))
      )

    correlated.writeStream
    // df.writeStream
      .format("console")
      .outputMode("update")
      .start()
      .awaitTermination()

    // Write the results
    // df.writeStream
    // correlated.writeStream
      // .format("kafka")
      // .option("kafka.bootstrap.servers", "localhost:9092")
      // .option("topic", write_topic)
      // .start()
      // .awaitTermination()

  }
}
