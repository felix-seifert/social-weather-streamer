package com.felixseifert.socialweatherstreamer
package sparkprocessor

import org.apache.spark.sql.{SparkSession, Row, Encoders}
import org.apache.spark.streaming.State
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.GroupState
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.catalyst.ScalaReflection

case class GeoInfo(
    id: String,
    fullName: String,
    country: String,
    cc: String,
    boxCords: List[Double],
    geotype: String
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
    geoinfo: GeoInfo,
    weather: WeatherInfo
)

object TweetProcessor {
  def main(args: Array[String]) = {
    val spark = SparkSession
      .builder()
      .config("spark.master", "local")
      .getOrCreate()

    spark.conf.set("spark.sql.streaming.checkpointLocation", "/tmp/")

    import spark.implicits._

    val read_topic = "tweets-enriched"
    val write_topic = "correlations"

    // // Read the stream
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", read_topic)
      .load

    // df.printSchema()

    val raw = df.selectExpr("CAST(value AS STRING)").as[String]
    val schema =
      ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]
    val extracted = raw
      .select(from_json(col("value"), schema).as("data"))
      .select("data.*")

    extracted.show

    // Write the results
    val stream = extracted.writeStream
      .format("console")
      .outputMode("update")
      .start()
      .awaitTermination()

    // Write the results
    // val stream = extracted.writeStream
    //   .format("kafka")
    //   .option("kafka.bootstrap.servers", "localhost:9092")
    //   .option("topic", write_topic)
    //   .start()

    // stream.awaitTermination()

  }
}
