package CSVUtils

import java.io.{PrintWriter, File}
import java.nio.file.{Paths, Path, Files}

import org.apache.hadoop.fs.FileUtil

/**
  * Created by kedarvdm on 11/29/15.
  */
object CSVUtils {

  def createCSVFromArray(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Unit = {

    val output = new File(path)
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    val writer = new PrintWriter(output)
    //write Headers
    writer.println(headers.mkString(","))
    dataRDD.foreach(x => {
      val str = x._1 + "," + x._2
      writer.write(str)
      writer.println()
    })
    writer.close()
  }

  def createCSVLanguageAsterPlot(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Unit = {

    val output = new File(path)
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    var total=0;
    dataRDD.foreach(x => {
      total= total+ x._2
    })

    val maxCount= dataRDD.head._2

    val writer = new PrintWriter(output)
    var order=1.0
    var count=1

    //write Headers
    writer.println(headers.mkString(","))
    dataRDD.foreach(x => {
      val weight = x._2 / total.asInstanceOf[Double]
      val score = (x._2/maxCount.asInstanceOf[Double])*100
      //"label", "order", "score", "weight" ,"color", "count"
      val str = x._1.toUpperCase +","+ order.toString + "," + score +"," + weight.toString +"," + get20Colors(count) + ","+ x._2
      order= order+1
      count=count+1
      writer.write(str)
      writer.println()
    })
    writer.close()
  }

  def createTSVFromArray(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Unit = {

    val output = new File(path)
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    val writer = new PrintWriter(output)
    //write Headers
    writer.println(headers.mkString("\t"))
    dataRDD.foreach(x => {
      val str = x._1 + "," + x._2
      writer.write(str)
      writer.println()
    })
    writer.close()
  }

  def createCSVForSentiment(dataRDD: Array[(String, Int)],headers: Array[String], path: String): Unit = {
    val output = new File(path)
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    val writer = new PrintWriter(output)
    writer.println(headers.mkString(","))
    dataRDD.foreach (data => {
        val jsonText = data._1.toString + "," + data._2 + "," + geSentimenttColor(data._1.toString)
        writer.write(jsonText)
        writer.println()
      })
    writer.close()
  }

  def createCSVForDeviceCount(dataRDD: Array[(String, Int)], headers: Array[String], path: String): Unit = {
    val output = new File(path)
    if (output.exists()) {
      FileUtil.fullyDelete(output)
    }
    output.getParentFile.mkdirs()
    output.createNewFile()

    val writer = new PrintWriter(output)
    //write Headers
    writer.println(headers.mkString(","))
    var temp = "A"
    var deviceCount : DeviceCount = new DeviceCount()
    dataRDD.foreach(data => {
      println(data._1)
      val country = data._1.split("-")(0)
      val deviceType = data._1.split("-")(1)
      if (!country.equals(temp)) {
        temp = country
        if(!deviceCount.country.equals(""))
        deviceCount.writeCSV(writer)
        deviceCount = new DeviceCount()
        deviceCount.country = country

        if (deviceType.equals("Android"))
        deviceCount.android = data._2
        else if (deviceType.equals("ioS"))
        deviceCount.ios = data._2
        else if (deviceType.equals("Web"))
        deviceCount.web = data._2
        else if (deviceType.equals("Others"))
        deviceCount.others = data._2
      } else {
        if (deviceType.equals("Android"))
          deviceCount.android = data._2
        else if (deviceType.equals("ioS"))
          deviceCount.ios = data._2
        else if (deviceType.equals("Web"))
          deviceCount.web = data._2
        else if (deviceType.equals("Others"))
          deviceCount.others = data._2
      }
    })
    writer.close()
  }

  def geSentimenttColor(sentiment: String): String ={
    sentiment match {
      case "NEGATIVE" => return "#FF0000"
      case "POSITIVE" => return "#008000"
      case "NEUTRAL" => return "#6495ED"
      case "NOT_UNDERSTOOD" => return "#FAFAD2"
    }
  }

  def get20Colors(id:Int):String= {
    id match {
      case 1 => return "#FF6347"
      case 2 => return "#F5DEB3"
      case 3 => return "#008080"
      case 4 => return "#D8BFD8"
      case 5 => return "#D2B48C"
      case 6 => return "#4682B4"
      case 7 => return "#00FF7F"
      case 8 => return "#708090"
      case 9 => return "#6A5ACD"
      case 10 => return "#A0522D"
      case 11 => return "#FA8072"
      case 12 => return "#F4A460"
      case 13 => return "#2E8B57"
      case 14 => return "#4169E1"
      case 15 => return "#663399"
      case 16 => return "#800080"
      case 17 => return "#DB7093"
      case 18 => return "#FFA500"
      case 19 => return "#7B68EE"
      case 20 => return "#20B2AA"
    }
  }
}
