package controllers

import play.api.mvc._
import utils.TwitterStreaming
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Application extends Controller {

  def index = Action {
    Future{TwitterStreaming.TwitterStreamUtil}
    Ok(views.html.index("Streaming has been started!!!"))
  }

  def stopStreaming = Action {
    TwitterStreaming.stopStreaming
    Ok(views.html.index("Streaming Stopped, please restart the application to stream again"))
  }
}
