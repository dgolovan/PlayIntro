package controllers

import javax.inject._

import com.typesafe.config.ConfigFactory
import org.joda.time.{DateTime, LocalDate}
import play.api.Logger
import play.api.cache.Cached
import play.api.mvc._

import scala.util.{Random, Try}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CurrencyController @Inject() (cached: Cached) extends Controller with SecurityTrait{
  private lazy val config = ConfigFactory.load()
  private lazy val dateFrom = config.getString("dates.from")
  private lazy val dateTo = config.getString("dates.to")

  def onUnauthorized(request: RequestHeader) = BadRequest("Not Authorized")

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def get(date: String) = cached("getCurrency") {
    IsAuthenticated { apiKey =>
      implicit request =>
        Logger.info("Evaluating get currency page")

        val fromDate = LocalDate.parse(dateFrom)
        val now = LocalDate.parse(dateTo)
        val parsedDate = Try(LocalDate.parse(date)).getOrElse(LocalDate.parse("1900-01-01"))

        if (parsedDate.isAfter(fromDate) && (parsedDate.isBefore(now) || parsedDate.isEqual(now)))
          Ok(views.html.currency.get(parsedDate))
        else
          BadRequest("Either the date format is incorrect or the date is out of range.")
    }
  }

  def random() = Action {
    lazy val from: Long = LocalDate.parse(dateFrom).toDateTimeAtStartOfDay().getMillis()
    lazy val to: Long = LocalDate.parse(dateTo).toDateTimeAtStartOfDay().getMillis()
//    val maxLong: Long = 9223372036854775807L

    lazy val range: Long = to - from + 1
    val random: Long = Random.nextLong() % range

    val newMillis: Long = from + math.abs(random)

    val randomDate: LocalDate = new DateTime(newMillis).toLocalDate

    Redirect(routes.CurrencyController.get(randomDate.toString))
  }



}
