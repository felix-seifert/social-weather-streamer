name := "spark-processor"

version := "0.1"

scalaVersion := "2.12.15"
val sparkVersion = "3.1.2"

libraryDependencies ++= Seq(
//  "com.johnsnowlabs.nlp" %% "spark-nlp" % "3.3.1",  // Add for ML model
  "org.apache.spark"  %% "spark-core"                 % sparkVersion,
  "org.apache.spark"  %% "spark-streaming"            % sparkVersion,
  "org.apache.spark"  %% "spark-sql"                  % sparkVersion,
  "org.apache.spark"  %% "spark-streaming-kafka-0-10" % sparkVersion,
  "org.apache.spark"  %% "spark-sql-kafka-0-10"       % sparkVersion
)
