package controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Real-Time Tweet Analysis"))
  }

  def popularHashtags = Action{
    SparkMapReduce.SparkReduce.popularHashTags
    Ok(views.html.popularHashtags())
  }

  def numberOfDevices = Action{
    SparkMapReduce.SparkReduce.deviceCountPerCountry
    Ok(views.html.numberOfDevices())
  }

  def verifiedAccounts = Action{
    SparkMapReduce.SparkReduce.numberOfVerifiedAccountsPerCountry
    Ok(views.html.index("Please check your desktop for verified accounts"))
  }

  def activeTimeZones = Action{
    SparkMapReduce.SparkReduce.tweetsPerTimeZone
    Ok(views.html.activeTimeZones())
  }

  def topUserMentions = Action{
    SparkMapReduce.SparkReduce.topUserMentions
    Ok(views.html.index("Please check your desktop for Top User Mentions"))
  }

  def sentimentAnalysis = Action{
    SparkMapReduce.SparkReduce.sentimentAnalysis
    Ok(views.html.sentimentVisualization())
  }

  def languageCount = Action{
    SparkMapReduce.SparkReduce.languageCount
    Ok(views.html.languageCount())
  }

}
