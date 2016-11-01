package utils

import java.util.concurrent.TimeUnit

import TweetUtils.TweetUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import StreamingContext._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter._
import play.api.Play
import org.apache.spark.SparkConf
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object TwitterStreaming {

  val sparkConf = new SparkConf().setAppName("TwitterStreaming").setMaster("local[2]").set("allowMultipleContexts", "true")
  val ssc = new StreamingContext(sparkConf, Seconds(12))

  def TwitterStreamUtil {

    // Twitter Authentication credentials
    val consumerKey = ""
    val consumerSecret = ""
    val accessToken = ""
    val accessTokenSecret = ""

    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    val stream = TwitterUtils.createStream(ssc, None)

    val admin = HBaseUtils.HBaseUtils.getHBaseAdmin
    if (!admin.isTableAvailable("twitteruser")) {
      HBaseUtils.HBaseUtils.createTwitterUser
    }

    if (!admin.isTableAvailable("tweetdata")) {
      HBaseUtils.HBaseUtils.createTweetData
    }

    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))
    val userMentions = stream.flatMap(status => status.getText.split(" ")).filter(_.startsWith("@"))

    val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
      .map { case (topic, count) => (topic, count) }
      .transform(_.sortBy(x => x._2, false, 0))

    topCounts60.foreachRDD(rdd => {
      val topList = rdd.take(10)
      val headers = Array("Hashtag", "Count")
      CSVUtils.CSVUtils.createCSVFromArray(topList, headers, "/home/kedarvdm/Desktop/RealTime/PopularHashTags/PopularHashTags.csv")
      println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
      topList.foreach { case (tag, count) => println("%s (%s tweets)".format(tag, count)) }
    })

    val topMentions60 = userMentions.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
      .map { case (user, count) => (user, count) }
      .transform(_.sortBy(x => x._2, false, 0))

    topMentions60.foreachRDD(rdd => {
      val topUserList = rdd.take(10)
      val headers = Array("User", "Count")
      CSVUtils.CSVUtils.createCSVFromArray(topUserList, headers, "/home/kedarvdm/Desktop/RealTime/UserMentions/PopularUsers.csv")
      println("\nPopular User Mentions in last 60 seconds (%s total):".format(rdd.count()))
      topUserList.foreach { case (user, count) => println("%s (%s tweets)".format(user, count)) }
    }
    )

    stream.foreachRDD(rdd => {
      rdd.foreach(
        x => {
          val s = Future(TweetUtils.dumpUserDatatoHbase(x, consumerKey, consumerSecret, accessToken, accessTokenSecret))
          s.onComplete({ case Success(x) => println("user success"); case Failure(x) => println("user failed +" + x) })
          val t = Future(TweetUtils.dumpTweetDatatoHbase(x, consumerKey, consumerSecret, accessToken, accessTokenSecret))
          t.onComplete({ case Success(x) => println("tweet success"); case Failure(x) => println("tweet failed") })
        }
      )
    }

    )

    ssc.start()
    ssc.awaitTermination()
  }

  def stopStreaming: Unit = {
    ssc.stop()
    println("Streaming stopped")
  }
}