package filters

import javax.inject.Inject

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.collection.mutable.Map
import scala.concurrent.{ExecutionContext, Future}

class ThrottleFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  private lazy val maxRequests = ConfigFactory.load().getInt("maxRequestsPerMin")
  private val requestsPerMin: Map[String, (String, Int)] = Map("lastRequest" -> ("", 0))

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {


    if (!checkRequestLimit) {
      Logger.info("Limiting request!!")
      nextFilter(requestHeader).map { _ =>
        Results.Forbidden("Limit Reached")
      }
    } else {
      Logger.info("Limit not yet reached")
      nextFilter(requestHeader).map { result =>
        result
      }
    }
  }

  private def checkRequestLimit(): Boolean = {
    val upToCurrentMin = DateTime.now().toString("y-M-d H:m")
    Logger.info("Current Time: " + upToCurrentMin)

    if (requestsPerMin("lastRequest")._1 == upToCurrentMin)
      requestsPerMin("lastRequest") = (upToCurrentMin, requestsPerMin("lastRequest")._2 + 1)
    else
      requestsPerMin("lastRequest") = (upToCurrentMin, 0)


    requestsPerMin("lastRequest")._2 < maxRequests
  }
}
