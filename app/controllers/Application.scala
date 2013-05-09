package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.{WS, Response}

import scala.concurrent._
import ExecutionContext.Implicits.global

import models.CityWeather

object Application extends Controller {
  
  private def getCityUrl(cityId: String): String = s"http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D$cityId&format=json&diagnostics=true"
  
  def index(city: String) = Action {
    Async {
      val myUrl = getCityUrl(city)
      val resp: Future[Response] = WS.url(myUrl).get()
      val weather: Future[CityWeather] = resp.map(CityWeather.fromResponse)
      val result = weather map { w =>
        Ok(views.html.city(w))
      }
      println("Application.index finished")
      result
    }
  }
  
  def hottest = Action {
    val cityIds: Seq[String] = "1521894 1521643 1522006 1522328 2347289 44418 12589610".split(" ")
    val listOfFutures: Seq[Future[CityWeather]] = cityIds.map { cityId => 
      WS.url(getCityUrl(cityId)).get.map(CityWeather.fromResponse)
    }
    val futureList: Future[Seq[CityWeather]] = Future.sequence(listOfFutures)
    val hottest: Future[CityWeather] = futureList map { weathers => 
      weathers.maxBy(_.temp)
    }
    Async {
      hottest map { cw => Ok(views.html.city(cw)) }
    }
  }
  
  

  
  
}
