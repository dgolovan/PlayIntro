package controllers

import play.api.Logger
import play.api.mvc._

/**
  * Created by denisg on 2016-03-11.
  */
trait SecurityTrait {
  self: Controller =>
  val logger: Logger = Logger(this.getClass())

  def apiKey(request: RequestHeader): Option[String] = {
    val apiKey = request.headers.get("api-key").getOrElse("")
    Logger.debug("API-KEY: "+ apiKey)
    val Pattern = "^([a-zA-Z0-9]{32})$".r
    apiKey match {
      case Pattern(c) => Some(c)
      case _ => None
    }
  }

  /**
    * Redirect to login if the use in not authorized.
    */
  def onUnauthorized(request: RequestHeader): Result

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(apiKey, onUnauthorized) { apiKey =>
      Action(request => f(apiKey)(request))
    }
}