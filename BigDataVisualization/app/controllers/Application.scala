package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def popularHashtags = Action{
    Ok(views.html.popularHashtags())
  }

  def numberOfDevices = Action{
    Ok(views.html.numberOfDevices())
  }

  def userPerTimeZone = Action{
    Ok(views.html.usersPerTimeZone())
  }

  def topUserMentions = Action{
    Ok(views.html.topUserMentions())
  }

  def sentimentAnalysis = Action{
    Ok(views.html.sentimentVisualization())
  }

  def languageCount = Action{
    Ok(views.html.languageCount())
  }

  def tweetsPerState = Action{
    Ok(views.html.tweetsPerState())
  }

  def hashtagExplorer = Action {
    Ok(views.html.hashtagExplorer())
  }

  def textExplorer = Action {
    Ok(views.html.textExplorer())
  }

  def sentimentsPerTimezone = Action {
    Ok(views.html.sentimentsPerTimezone())
  }

  def genderPerTimezone =Action {
    Ok(views.html.genderPerTimezone())
  }

}
