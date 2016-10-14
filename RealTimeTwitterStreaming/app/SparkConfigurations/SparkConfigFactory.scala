package SparkConfigurations

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}

import scala.util.Random

/**
  * Created by kedarvdm on 11/23/15.
  */
object SparkConfigFactory {
  val rand= new Random()
  def getSparkConf: SparkConf ={
    new SparkConf().setAppName("DefaultSparkConf").setMaster("local["+rand.nextInt(500)+"]").set("allowMultipleContexts","true")
  }

  def getSparkConf(appName:String): SparkConf ={
    new SparkConf().setAppName(appName).setMaster("local[2]").set("allowMultipleContexts","true")
  }

  def getSparkContext :SparkContext ={
    new SparkContext(getSparkConf)
  }

  def getSparkContext(sparkConf: SparkConf) :SparkContext ={
    new SparkContext(sparkConf)
  }

  def getSparkContext(appName:String) :SparkContext ={
    new SparkContext(getSparkConf(appName))
  }

  def getStreamingContext(): StreamingContext ={
    //default
    new StreamingContext(getSparkConf,Seconds(10))
  }

  def getStreamingContext(appName:String): StreamingContext ={
    new StreamingContext(getSparkConf(appName),Seconds(10))
  }

  def getStreamingContext(duration:Int): StreamingContext ={
    new StreamingContext(getSparkConf,Seconds(duration))
  }

  def getStreamingContext(appName:String,duration:Int): StreamingContext ={
    new StreamingContext(getSparkConf,Seconds(duration))
  }

  def getStreamingContext(sparkConf: SparkConf): StreamingContext ={
    new StreamingContext(sparkConf,Seconds(12))
  }

  def getStreamingContext(sparkConf: SparkConf, duration:Int): StreamingContext ={
    new StreamingContext(sparkConf,Seconds(duration))
  }

}
