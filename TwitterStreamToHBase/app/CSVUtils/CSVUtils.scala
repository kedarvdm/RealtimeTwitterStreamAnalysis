package CSVUtils

import java.io.{PrintWriter, File}
import java.nio.file.{Paths, Path, Files}

import org.apache.hadoop.fs.FileUtil
import org.apache.spark.rdd.RDD

/**
  * Created by kedarvdm on 11/29/15.
  */
object CSVUtils {

  def createCSVFromArray(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Boolean = {

    val output = new File(path)
    var success = true
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }

    try {
      output.getParentFile.mkdirs()
      output.createNewFile()
    }
    catch {
      case e: Exception => success = false
    }

    val writer = new PrintWriter(output)
    //write Headers
    writer.println(headers.mkString(","))
    dataRDD.foreach(x => {
      val str = x._1 + "," + x._2
      writer.write(str)
      writer.println()
    })
    writer.close()
    success
  }

  def createTSVFromArray(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Boolean = {

    val output = new File(path)
    var success = true
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    try {
      output.getParentFile.mkdirs()
      output.createNewFile()
    }
    catch {
      case e: Exception => success = false
    }

    val writer = new PrintWriter(output)
    //write Headers
    writer.println(headers.mkString("\t"))
    dataRDD.foreach(x => {
      val str = x._1 + "," + x._2
      writer.write(str)
      writer.println()
    })
    writer.close()
    success
  }

  def createCSVForSentiment(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Boolean = {
    val output = new File(path)
    var success = true
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    try {
      output.getParentFile.mkdirs()
      output.createNewFile()
    } catch {
      case e: Exception => success = false
    }

    val writer = new PrintWriter(output)
    writer.println(headers.mkString(","))
    dataRDD.foreach(data => {
      val jsonText = data._1.toString + "," + data._2 + "," + getColor(data._1.toString)
      writer.write(jsonText)
      writer.println()
    })
    writer.close()
    success
  }

  def getColor(sentiment: String): String = {
    sentiment match {
      case "NEGATIVE" => return "#FF0000"
      case "POSITIVE" => return "#008000"
      case "NEUTRAL" => return "#6495ED"
      case "NOT_UNDERSTOOD" => return "#FAFAD2"
    }
  }
}
