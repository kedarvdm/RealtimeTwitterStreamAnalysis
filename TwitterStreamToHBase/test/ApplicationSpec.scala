import org.scalatestplus.play.PlaySpec
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
//@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends PlaySpec {

  //  "Application" should {
  //
  //    "send 404 on a bad request" in new WithApplication{
  //      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
  //    }
  //
  //    "render the index page" in new WithApplication{
  //      val home = route(FakeRequest(GET, "/")).get
  //
  //      status(home) must equalTo(OK)
  //      contentType(home) must beSome.which(_ == "text/html")
  //      contentAsString(home) must contain ("Your new application is ready.")
  //    }
  //  }

  "createCSVFromArray" should {
    "generate a CSV file on local machine" in {
      val fakeHeaderArray = Array("Hashtag", "count")
      val fakePath = "/home/kedarvdm/Desktop/CSVScalaTest"
      val fakeData: Array[(String, Int)] = Array(("Data 1", 1), ("Data2", 2), ("Data3", 3))
      val success = CSVUtils.CSVUtils.createCSVFromArray(fakeData, fakeHeaderArray, fakePath)
      assert(success == true)
    }
  }

  "createTSVFromArray" should {
    "generate a TSV file on local machine" in {
      val fakeHeaderArray = Array("Hashtag", "count")
      val fakePath = "/home/kedarvdm/Desktop/TSVScalaTest"
      val fakeData: Array[(String, Int)] = Array(("Data 1", 1), ("Data2", 2), ("Data3", 3))
      val success = CSVUtils.CSVUtils.createCSVFromArray(fakeData, fakeHeaderArray, fakePath)
      assert(success == true)
    }
  }

  "createCSVForSentiment" should {
    "generate a CSV file with color of the sentiment" in {
      val fakeHeaderArray = Array("Sentiment", "count")
      val fakePath = "/home/kedarvdm/Desktop/SentimentScalaTest"
      val fakeData: Array[(String, Int)] = Array(("POSITIVE", 12), ("NEGATIVE", 6), ("NEUTRAL", 3))
      val success = CSVUtils.CSVUtils.createCSVForSentiment(fakeData, fakeHeaderArray, fakePath)
      assert(success == true)
    }
  }
}
