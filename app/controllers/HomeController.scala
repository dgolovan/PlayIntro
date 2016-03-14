package controllers

import javax.inject._

import play.api.Logger
import play.api.cache.Cached
import play.api.mvc._

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (cached: Cached) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = cached("homePage") {
      Action {
        Logger.info("Evaluating home page")
        Ok(views.html.index("Your new application is ready."))
      }
    val f = Future.apply({ "hello" })
    f
  }

}
