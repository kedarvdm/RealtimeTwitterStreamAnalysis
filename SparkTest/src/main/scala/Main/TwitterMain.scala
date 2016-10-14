package Main

import TweetUtils.{TweetUtils, TwitterUserColumnQualifier, TwitterUserColumnFamily}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter._
import org.apache.spark.SparkConf
import java.util.concurrent.TimeUnit

import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

object TwitterMain {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: TwitterPopularTags <consumer key> <consumer secret> " +
        "<access token> <access token secret> [<filters>]")
      System.exit(1)
    }


    val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    val filters = args.takeRight(args.length - 4)

    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    val sparkConf = new SparkConf().setAppName("TwitterPopularTags").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(12))
    val stream = TwitterUtils.createStream(ssc, None, filters)    //.filter(_.getUser.getLang.equals("en"))

    stream.foreachRDD( rdd=>
    {

      rdd.foreach(
        x=>
          {
            TweetUtils.dumpUserDatatoHbase(x,consumerKey,consumerSecret,accessToken,accessTokenSecret)
          }
      )
    }

    )

    ssc.start()
    TimeUnit.SECONDS.sleep(60)
    ssc.stop(true)
    //ssc.awaitTermination()
  }
}