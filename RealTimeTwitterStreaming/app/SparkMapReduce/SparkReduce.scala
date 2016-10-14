package SparkMapReduce

import TweetUtils.{TwitterUserColumnQualifier, TwitterUserColumnFamily, TwitterDataColumnQualifiers, TwitterDataColumnFamilies}
import akka.actor.Status.Success
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by kedarvdm on 11/23/15.
  */
object SparkReduce {

  var sparkConf = new SparkConf().setAppName("PopularTags").setMaster("local[1]").set("allowMultipleContexts", "true")
  var sparkContext = new SparkContext(sparkConf)

  def popularHashTags: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTweetDataRDD(sparkContext)

    val hashtags = hBaseRDD.map(tuple => tuple._2)
      .map(result => result.getValue(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.TweetText))
      .flatMap(tweet => TweetUtils.TweetUtils.getText(tweet).split(" ").filter(_.startsWith("#")))

    val topCounts = hashtags.map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._2, false, 0)
      .map { case (topic, count) => (topic, count) }
      .take(10)
    val headers = Array("Hashtag", "Count")
    CSVUtils.CSVUtils.createCSVFromArray(topCounts, headers, "/home/kedarvdm/Desktop/PopularHashTags/PopularHashTags.csv")

    println("Popular Hashtags")
    topCounts.foreach(rdd => {
      println(rdd)
    })
  }

  def deviceCountPerCountry: Unit = {
    val androidList = List("Twitter for Android", "Twitter for Android Tablets")
    val iosList = List("Twitter for iPhone", "Twitter for iPad")
    val otherList = List("Twitter for BlackBerry", "Twitter for BlackBerryÂ®", "Twitter for Windows", "Twitter for Windows Phone")
    val hBaseRDD = HBaseUtils.HBaseUtils.getTweetDataRDD(sparkContext)

    val countryAndSource = hBaseRDD.map(tuple => tuple._2)
      .map(result => {
        val country = result.getValue(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceCountry)
        val deviceType = TweetUtils.TweetUtils.getText(result.getValue(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.TweetSource)).toString
        var deviceSource: String = "Web"

        if (androidList.contains(deviceType))
          deviceSource = "Android"
        else if (iosList.contains(deviceType))
          deviceSource = "ioS"
        else if (otherList.contains(deviceType))
          deviceSource = "Others"
        else
          deviceSource = "Web"

        Option(country) match {
          case Some(x) => {
            TweetUtils.TweetUtils.getText(x) + "-" + deviceSource
          }
          case None => {
            "Unspecified Country - " + deviceSource
          }
        }
      })

    val numberOfDevices = countryAndSource.map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._1, true, 0)
      .map { case (countryAndType, count) => (countryAndType, count) }
      .collect()
    val headers = Array("Country", "Android", "ioS", "Web", "Other")
    CSVUtils.CSVUtils.createCSVForDeviceCount(numberOfDevices, headers, "/home/kedarvdm/Desktop/DevicePerCountry/DevicePerCountry.csv")

    println("Device Count")
    numberOfDevices.foreach(rdd => {
      println(rdd)
    })
  }


  def numberOfVerifiedAccountsPerCountry: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTwitterUserRDD(sparkContext)

    val hashtags = hBaseRDD.map(tuple => tuple._2)
      .map(result => {
        var country = result.getValue(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.PlaceCountry)
        Option(country) match {
          case Some(x) => x
          case None => {
            country = Bytes.toBytes("Unspecified Country - ")
          }
        }

        var isVerified = false
        Option(result.getValue(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.VerifiedAccount)) match {
          case Some(x) => isVerified = Bytes.toBoolean(x)
          case None => isVerified = false
        }
        if (isVerified) {
          TweetUtils.TweetUtils.getText(country) + " - Verified"
        }
        else {
          TweetUtils.TweetUtils.getText(country) + " - Not Verified"
        }
      })

    val verifiedUsers = hashtags.map((_, 1))
      .reduceByKey(_ + _)
      .map { case (topic, count) => (topic, count) }
      .sortBy(x => x._1, true, 0)
      .collect()
    val headers = Array("Country", "Users")
    CSVUtils.CSVUtils.createCSVFromArray(verifiedUsers, headers, "/home/kedarvdm/Desktop/VerifiedAccounts/VerifiedAccounts.csv")

    println("Verified Accounts")
    verifiedUsers.foreach(rdd => {
      println(rdd)
    })

  }

  def tweetsPerTimeZone: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTwitterUserRDD(sparkContext)
    val tweetsPerTimeZone = hBaseRDD.map(tuple => tuple._2)
      .map(result => Bytes.toStringBinary(result.getValue(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.TimeZone)))
      .filter(!_.equals("null"))
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._2, false, 1)
      .take(10)

    val headers = Array("TimeZone", "Tweets")
    CSVUtils.CSVUtils.createCSVFromArray(tweetsPerTimeZone, headers, "/home/kedarvdm/Desktop/ActiveTimeZones/ActiveTimeZones.csv")

    println("Tweets Perc Timezone")
    tweetsPerTimeZone.foreach(rdd => {
      println(rdd)
    })
  }

  def topUserMentions: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTweetDataRDD(sparkContext)
    val topUserMentions = hBaseRDD.map(tuple => tuple._2)
      .map(status => status.getValue(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.TweetText))
      .flatMap(TweetUtils.TweetUtils.getText(_).split(" ")).filter(_.startsWith("@"))
      .filter(x=>(!(x.equals("@") || x.equals("@null")))) //Removing only @ and @null mentions
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._2, false, 1)
      .take(20)
    val headers = Array("TimeZone", "Tweets")
    CSVUtils.CSVUtils.createCSVFromArray(topUserMentions, headers, "/home/kedarvdm/Desktop/TopUserMentions/TopUserMentions.csv")

    println("Top User Mentions")
    topUserMentions.foreach(rdd => {
      println(rdd)
    })
  }

  //Sentiment MapReduce
  def sentimentAnalysis: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTweetDataRDD(sparkContext)

    val sentiments = hBaseRDD.map(tuple => tuple._2)
      .map(result => TweetUtils.TweetUtils.getText(result.getValue(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.Sentiment)))
      .map((_, 1))
      .reduceByKey(_ + _)
      .collect()

    val headers = Array("label", "value", "color")
    CSVUtils.CSVUtils.createCSVForSentiment(sentiments, headers, "/home/kedarvdm/Desktop/SentimentAnalysis/SentimentAnalysis.csv")

    println("Sentiment Analysis")
    sentiments.foreach(rdd => {
      println(rdd)
    })
  }

  //Language Count
  def languageCount: Unit = {
    val hBaseRDD = HBaseUtils.HBaseUtils.getTwitterUserRDD(sparkContext)
    val languageCount = hBaseRDD.map(tuple => tuple._2)
      .map(result => Bytes.toStringBinary(result.getValue(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.LanguageCode)))
      .filter(!_.equals("null"))
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._2, false, 1)
      .take(20)
    val headers = Array("label", "order", "score", "weight" ,"color", "count")
    CSVUtils.CSVUtils.createCSVLanguageAsterPlot(languageCount, headers, "/home/kedarvdm/Desktop/LanguageCount/LanguageCount.csv")

    println("Language Count")
    languageCount.foreach(rdd => {
      println(rdd)
    })
  }
}