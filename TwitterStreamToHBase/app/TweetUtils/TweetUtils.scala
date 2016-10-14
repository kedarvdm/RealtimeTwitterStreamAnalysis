package TweetUtils

import scala.xml.parsing.NoBindingFactoryAdapter
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl

import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.util.Bytes._
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

/**
  * Created by kedarvdm on 11/18/15.
  */

object TweetUtils {

  def getConfigurationBuilder(consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String): ConfigurationBuilder = {
    val cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)
      .setUseSSL(true)
    cb
  }

  def getTwitter(cb: ConfigurationBuilder): Twitter = {
    val tf = new TwitterFactory(cb.build())
    tf.getInstance()
  }

  def retrieveFollowersList(consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String, twitterHandle: String): PagableResponseList[User] = {

    val twitter = getTwitter(getConfigurationBuilder(consumerKey, consumerSecret, accessToken, accessTokenSecret))
    val followers = twitter.getFollowersList(twitterHandle, -1)
    followers
  }

  def retrieveFriendsList(consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String, twitterHandle: String): PagableResponseList[User] = {

    val twitter = getTwitter(getConfigurationBuilder(consumerKey, consumerSecret, accessToken, accessTokenSecret))
    val friends = twitter.getFriendsList(twitterHandle, -1)
    friends
  }

  def dumpUserDatatoHbase(x: Status, consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String): Unit = {
    val htable = HBaseUtils.HBaseUtils.getHBaseTable("twitteruser")
    val p = new Put(toBytes(x.getUser.getId.toString))
    Option(x.getUser.getName) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserName, toBytes(x.getUser.getName))
      case None => "nothing"
    }

    //if (!x.getName.equals(""))
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserName, toBytes(x.getName))

    Option(x.getUser.getScreenName) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.ScreenName, toBytes(x.getUser.getScreenName))
      case None => "nothing"
    }

    //if (!x.getScreenName.equals(""))
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.ScreenName, toBytes(x.getScreenName))

    Option(x.getUser.getDescription) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Description, toBytes(x.getUser.getDescription))
      case None => "nothing"
    }

    //if (x.getDescription != null && !x.getDescription.equals(""))
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Description, toBytes(x.getDescription))

    Option(x.getUser.getLang) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.LanguageCode, toBytes(x.getUser.getLang))
      case None => "nothing"
    }

    //if (x.getLang != null && !x.getLang.equals(""))
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.LanguageCode, toBytes(x.getLang))

    Option(x.getUser.getURL) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserUrl, toBytes(x.getUser.getURL))
      case None => "nothing"
    }

    //if (x.getURL != null && !x.getURL.equals(""))
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserUrl, toBytes(x.getURL))

    Option(x.getUser.isVerified) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Verified, toBytes(x.getUser.isVerified.toString))
      case None => "nothing"
    }

    //if (x.isVerified != null)
    //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Verified, toBytes(x.isVerified.toString))

    Option(x.getUser.getFollowersCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FollowersCount, toBytes(x.getUser.getFollowersCount.toString))
      case None => "nothing"
    }

    //if (x.getFollowersCount != null && !x.getFollowersCount.toString.equals(""))
    //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FollowersCount, toBytes(x.getFollowersCount.toString))

    Option(x.getUser.getFriendsCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FriendsCount, toBytes(x.getUser.getFriendsCount.toString))
      case None => "nothing"
    }

    //if (x.getFriendsCount != null && !x.getFriendsCount.toString.equals(""))
    //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FriendsCount, toBytes(x.getFriendsCount.toString))

    Option(x.getUser.getListedCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getUser.getListedCount.toString))
      case None => "nothing"
    }

    //if (x.getListedCount != null && !x.getListedCount.toString.equals(""))
    //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getListedCount.toString))

    Option(x.getUser.getLocation) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.UserDefinedLocation, toBytes(x.getUser.getLocation))
      case None => "nothing"
    }

    //if (x.getLocation != null && !x.getLocation.equals(""))
    //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getLocation))

    Option(x.getUser.isGeoEnabled) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.GeoEnabled, toBytes(x.getUser.isGeoEnabled.toString))
      case None => "nothing"
    }

    //if (x.isGeoEnabled != null)
    //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.GeoEnabled, toBytes(x.isGeoEnabled.toString))

    Option(x.getUser.getTimeZone) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.TimeZone, toBytes(x.getUser.getTimeZone))
      case None => "nothing"
    }

    Option(x.getPlace) match {
      case Some(v) => {
        Option(x.getPlace.getCountry) match {
          case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.PlaceCountry, toBytes(x.getPlace.getCountry))
          case None => "nothing"
        }
      }
      case None => "nothing"
    }


    //if (x.getTimeZone != null && !x.getTimeZone.equals(""))
    //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.TimeZone, toBytes(x.getTimeZone))

    Option(x.getText) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Tweet, toBytes(x.getId.toString), toBytes(x.getText))
      case None => "nothing"
    }

    //if (x.getText != null && !x.getText.equals(""))
      //p.add(TwitterUserColumnFamily.Tweet, toBytes(x.getId.toString), toBytes(x.getText))

    p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.DumpedAsFriend, toBytes("false"))

    htable.put(p)
    //println("adding followers")
    val r = Future(addFollowerstoHBase(x, consumerKey, consumerSecret, accessToken, accessTokenSecret))
    r.onComplete({ case Success(x) => println("success adding followers"); case Failure(x) => println("failed adding followers") })

    //println("adding followers")
    val s = Future(addFriendstoHBase(x, consumerKey, consumerSecret, accessToken, accessTokenSecret))
    s.onComplete({ case Success(x) => println("success adding friends"); case Failure(x) => println("failed adding friends") })
  }

  def addFollowerstoHBase(x: Status, consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String): Unit = {
    val htable = HBaseUtils.HBaseUtils.getHBaseTable("twitteruser")
    val followers = TweetUtils.retrieveFollowersList(consumerKey, consumerSecret, accessToken, accessTokenSecret, x.getUser.getScreenName)
    val p = new Put(toBytes(x.getUser.getId.toString))
    val i = followers.iterator()
    while (i.hasNext) {
      val m = i.next()
      p.add(TwitterUserColumnFamily.Followers, toBytes(m.getId.toString), toBytes(m.getScreenName))
      dumpSingleUserDatatoHbase(x.getUser)
      //println("ScreenName:" + m.getScreenName + " UserId: " + m.getId)
    }
    htable.put(p)
  }

  def addFriendstoHBase(x: Status, consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String): Unit = {
    val htable = HBaseUtils.HBaseUtils.getHBaseTable("twitteruser")
    val followers = TweetUtils.retrieveFriendsList(consumerKey, consumerSecret, accessToken, accessTokenSecret, x.getUser.getScreenName)
    val p = new Put(toBytes(x.getUser.getId.toString))
    val i = followers.iterator()
    while (i.hasNext) {
      val m = i.next()
      p.add(TwitterUserColumnFamily.Friends, toBytes(m.getId.toString), toBytes(m.getScreenName))
      dumpSingleUserDatatoHbase(x.getUser)
      //println("ScreenName:" + m.getScreenName + " UserId: " + m.getId)
    }
    htable.put(p)
  }

  def dumpSingleUserDatatoHbase(x: User): Unit = {
    val htable = HBaseUtils.HBaseUtils.getHBaseTable("twitteruser")
    val p = new Put(toBytes(x.getId.toString))

    Option(x.getName) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserName, toBytes(x.getName))
      case None => "nothing"
    }

    //if (!x.getName.equals(""))
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserName, toBytes(x.getName))

    Option(x.getScreenName) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.ScreenName, toBytes(x.getScreenName))
      case None => "nothing"
    }

    //if (!x.getScreenName.equals(""))
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.ScreenName, toBytes(x.getScreenName))

    Option(x.getDescription) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Description, toBytes(x.getDescription))
      case None => "nothing"
    }

    //if (x.getDescription != null && !x.getDescription.equals(""))
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Description, toBytes(x.getDescription))

    Option(x.getLang) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.LanguageCode, toBytes(x.getLang))
      case None => "nothing"
    }

    //if (x.getLang != null && !x.getLang.equals(""))
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.LanguageCode, toBytes(x.getLang))

    Option(x.getURL) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserUrl, toBytes(x.getURL))
      case None => "nothing"
    }

    //if (x.getURL != null && !x.getURL.equals(""))
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.UserUrl, toBytes(x.getURL))

    Option(x.isVerified) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Verified, toBytes(x.isVerified.toString))
      case None => "nothing"
    }

    //if (x.isVerified != null)
      //p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.Verified, toBytes(x.isVerified.toString))

    Option(x.getFollowersCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FollowersCount, toBytes(x.getFollowersCount.toString))
      case None => "nothing"
    }

    //if (x.getFollowersCount != null && !x.getFollowersCount.toString.equals(""))
      //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FollowersCount, toBytes(x.getFollowersCount.toString))

    Option(x.getFriendsCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FriendsCount, toBytes(x.getFriendsCount.toString))
      case None => "nothing"
    }

    //if (x.getFriendsCount != null && !x.getFriendsCount.toString.equals(""))
      //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.FriendsCount, toBytes(x.getFriendsCount.toString))

    Option(x.getListedCount) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getListedCount.toString))
      case None => "nothing"
    }

    //if (x.getListedCount != null && !x.getListedCount.toString.equals(""))
      //p.add(TwitterUserColumnFamily.Stat, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getListedCount.toString))

    Option(x.getLocation) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getLocation))
      case None => "nothing"
    }

    //if (x.getLocation != null && !x.getLocation.equals(""))
      //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.PostsTillDate, toBytes(x.getLocation))

    Option(x.isGeoEnabled) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.GeoEnabled, toBytes(x.isGeoEnabled.toString))
      case None => "nothing"
    }

    //if (x.isGeoEnabled != null)
      //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.GeoEnabled, toBytes(x.isGeoEnabled.toString))

    Option(x.getTimeZone) match {
      case Some(v) => p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.TimeZone, toBytes(x.getTimeZone))
      case None => "nothing"
    }

    //if (x.getTimeZone != null && !x.getTimeZone.equals(""))
      //p.add(TwitterUserColumnFamily.Geo, TwitterUserColumnQualifier.TimeZone, toBytes(x.getTimeZone))

    p.add(TwitterUserColumnFamily.Info, TwitterUserColumnQualifier.DumpedAsFriend, toBytes("true"))


    htable.put(p)
  }

  def dumpTweetDatatoHbase(x: Status, consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String): Unit = {
    val htable = HBaseUtils.HBaseUtils.getHBaseTable("tweetdata")

    val p = new Put(toBytes(x.getId.toString))

    Option(x.getCreatedAt) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.CreatedAt, toBytes(v.toString))
      case None => "nothing"
    }

    Option(x.getUser.getId) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.UserId, toBytes(v))
      case None => "nothing"
    }

    Option(x.getText) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.TweetText, toBytes(v))
      case None => "nothing"
    }

    Option(x.getUser.isVerified) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.VerifiedAccount, toBytes(v))
      case None => "nothing"
    }

    Option(x.getSource) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.TweetSource, toBytes(v))
      case None => "nothing"
    }

    Option(x.isRetweet) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.IsRetweeted, toBytes(v.toString))
      case None => "nothing"
    }

    Option(x.isRetweetedByMe) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.IsRetweetedByMe, toBytes(v.toString))
      case None => "nothing"
    }

    Option(x.isRetweetedByMe) match {
      case Some(v) => p.add(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.IsRetweetedByMe, toBytes(v.toString))
      case None => "nothing"
    }

    //Inserting Tweet sentiment
    val sentiment = HBaseUtils.SentimentAnalysis.getSentiment(x.getText)
    Option(sentiment) match{
      case Some(v) => p.add(TwitterDataColumnFamilies.Tweet, TwitterDataColumnQualifiers.Sentiment, toBytes(v.toString))
      case None => "nothing"
    }

    Option(x.getPlace) match {
      case Some(v) => {
        p.add(TwitterDataColumnFamilies.Info, TwitterDataColumnQualifiers.PlacePresent, toBytes("true"))
        Option(x.getPlace.getCountry) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceCountry, toBytes(v))
          case None => "nothing"
        }

        Option(x.getPlace.getCountryCode) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceCountryCode, toBytes(v))
          case None => "nothing"
        }

        Option(x.getPlace.getFullName) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceFullName, toBytes(v))
          case None => "nothing"
        }

        Option(x.getPlace.getPlaceType) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceType, toBytes(v))
          case None => "nothing"
        }

      }
      case None => "nothing"
    }

    Option(x.getGeoLocation) match {
      case Some(v) => {
        Option(x.getGeoLocation.getLatitude) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceLatitude, toBytes(v))
          case None => "nothing"
        }

        Option(x.getGeoLocation.getLongitude) match {
          case Some(v) => p.add(TwitterDataColumnFamilies.Place, TwitterDataColumnQualifiers.PlaceLongitudes, toBytes(v))
          case None => "nothing"
        }
      }
      case None => "nothing"
    }

    htable.put(p)
  }

  def getText(html: Array[Byte]): String = {
    lazy val adapter = new NoBindingFactoryAdapter()
    lazy val parser = (new SAXFactoryImpl).newSAXParser
    val stream = new ByteArrayInputStream(html)
    val source = new InputSource(stream)
    return adapter.loadXML(source, parser).text
  }
}

object TwitterDataColumnFamilies extends Enumeration {
  val Info = Bytes.toBytes("info")
  val Tweet = Bytes.toBytes("tweet")
  val Place = Bytes.toBytes("place")
  val GeoLocation = Bytes.toBytes("GeoLocation")
}

object TwitterDataColumnQualifiers extends Enumeration {
  /*Info*/
  val TweetId = Bytes.toBytes("tweetid")
  val CreatedAt = Bytes.toBytes("createdat")
  val UserId = Bytes.toBytes("userid")
  val PlacePresent = Bytes.toBytes("placepresent")
  val VerifiedAccount = Bytes.toBytes("verifiedaccount")
  /*Tweet*/
  val TweetText = Bytes.toBytes("tweettext")
  val TweetSource = Bytes.toBytes("tweetsource")
  val IsRetweeted = Bytes.toBytes("isretweeted")
  val IsRetweetedByMe = Bytes.toBytes("isretweetedbyme")
  /*Place*/
  val PlaceCountry = Bytes.toBytes("placecountry")
  val PlaceCountryCode = Bytes.toBytes("placecountrycode")
  val PlaceFullName = Bytes.toBytes("placefullname")
  val PlaceType = Bytes.toBytes("placetype")

  /*GeoLocation*/
  val PlaceLatitude = Bytes.toBytes("placelatitudes")
  val PlaceLongitudes = Bytes.toBytes("placelongitudes")

  //Sentiment
  val Sentiment = Bytes.toBytes("sentiment")
}

object TwitterUserColumnFamily extends Enumeration {
  val Info = Bytes.toBytes("info")
  val Stat = Bytes.toBytes("stat")
  val Geo = Bytes.toBytes("geo")
  val Tweet = Bytes.toBytes("tweet")
  val Friends = Bytes.toBytes("friends")
  val Followers = Bytes.toBytes("followers")
}

object TwitterUserColumnQualifier extends Enumeration {
  val UserId = Bytes.toBytes("userid")
  val Description = Bytes.toBytes("description")
  val FollowersCount = Bytes.toBytes("followerscount")
  val FriendsCount = Bytes.toBytes("friendscount")
  val LanguageCode = Bytes.toBytes("languagecode")
  val UserDefinedLocation = Bytes.toBytes("userdefinedlocation")
  val GeoEnabled = Bytes.toBytes("geoenabled")
  val PlaceCountry = Bytes.toBytes("placecountry")
  val UserName = Bytes.toBytes("username")
  val ScreenName = Bytes.toBytes("screenname")
  val PostsTillDate = Bytes.toBytes("poststilldate")
  val TimeZone = Bytes.toBytes("timezone")
  val UserUrl = Bytes.toBytes("userurl")
  val Verified = Bytes.toBytes("verified")
  val DumpedAsFriend = Bytes.toBytes("dumpedasfriend")
}

