package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.{WS, Response}

import scala.concurrent._
import scala.util.{Success, Failure}
import ExecutionContext.Implicits.global

import models.CityWeather

object Application extends Controller {
  
  private def getCityUrl(cityId: String): String = s"http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D$cityId&format=json&diagnostics=true"
  
  def index = Action {
    Async {
      val CAIRO = "1521894"
      val myUrl = getCityUrl(CAIRO)
      val resp: Future[Response] = WS.url(myUrl).get()
      val weather: Future[CityWeather] = resp.map(CityWeather.fromResponse)
      val result = weather map { w =>
        Ok(views.html.city(w))
      }
      println("Application.index finished")
      result
    }
  }

  
  
}
