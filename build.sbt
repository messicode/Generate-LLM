ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "Cloud LLM",

    libraryDependencies ++= Seq(
      // DeepLearning4J and Jtokkit dependencies
      "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-M2.1",
      "org.nd4j" % "nd4j-native-platform" % "1.0.0-M2.1",
      "org.deeplearning4j" % "deeplearning4j-nlp" % "1.0.0-M2.1",
      "com.knuddels" % "jtokkit" % "1.1.0",

      //For logging
      "org.slf4j" % "slf4j-api" % "1.7.32",
      "ch.qos.logback" % "logback-classic" % "1.2.6",

      //Hadoop dep
      "org.apache.hbase" % "hbase-client" % "2.6.0" exclude("org.apache.hbase", "hbase-protocol-shaded"),
      "org.apache.hadoop" % "hadoop-common" % "3.3.6",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.6",
      "org.apache.hadoop" % "hadoop-mapreduce-client-app" % "3.3.6",

      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "com.typesafe" % "config" % "1.4.3",

    ),


    assembly / assemblyMergeStrategy  := {
      case PathList("META-INF", xs@_*) =>
        xs match {
          case "MANIFEST.MF" :: Nil => MergeStrategy.discard
          case "services" :: _      => MergeStrategy.concat
          case _                    => MergeStrategy.discard
        } // Discard META-INF files
      case "reference.conf" => MergeStrategy.concat
      case x if x.endsWith(".proto") => MergeStrategy.rename
      case x if x.contains("hadoop") => MergeStrategy.first
      case _ => MergeStrategy.first
    }
  )

