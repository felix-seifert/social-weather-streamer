name := "spark-processor"

version := "0.1"

scalaVersion := "2.11.12"
val sparkVersion = "2.3.0"

Compile / scalaSource := baseDirectory.value / "spark-processor/src"


/* idePackagePrefix := Some("com.felixseifert.socialweatherstreamer") */

libraryDependencies ++= Seq(
    "org.apache.spark"  %% "spark-core"                 % sparkVersion,
    "org.apache.spark"  %% "spark-streaming"            % sparkVersion,
    "org.apache.spark"  %% "spark-sql"                  % sparkVersion,
    "org.apache.spark"  %% "spark-mllib"                % sparkVersion,
    "org.apache.spark"  %% "spark-streaming-kafka-0-10" % sparkVersion,
    "org.apache.spark"  %% "spark-sql-kafka-0-10"       % sparkVersion,
    "com.johnsnowlabs.nlp" %% "spark-nlp-spark23" % "3.3.1",
    )

