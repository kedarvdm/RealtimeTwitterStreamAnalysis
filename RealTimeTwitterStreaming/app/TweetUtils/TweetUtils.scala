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

