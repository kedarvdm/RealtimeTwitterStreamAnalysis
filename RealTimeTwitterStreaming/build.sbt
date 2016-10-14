name := """RealTimeTwitterStreaming"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "1.5.2"
libraryDependencies += "org.apache.spark" % "spark-streaming-twitter_2.11" % "1.5.2"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "1.5.2"

libraryDependencies += "org.apache.hadoop" % "hadoop-core" % "1.2.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.0"
libraryDependencies +=  "org.apache.hbase" % "hbase-common" % "0.98.16-hadoop2"
libraryDependencies +=  "org.apache.hbase" % "hbase-client" % "0.98.16-hadoop2"
libraryDependencies +=  "org.apache.hbase" % "hbase-server" % "0.98.16-hadoop2"
libraryDependencies += "org.apache.hbase" % "hbase-hadoop2-compat" % "0.98.16-hadoop2"

libraryDependencies += "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1"
libraryDependencies += "io.spray" % "spray-json_2.10" % "1.3.2"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"


dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true