package models

import play.api.libs.ws.Response
import play.api.libs.json._


case class CityWeather(city: String, temp: String)

object CityWeather {
  def fromResponse(resp: Response): CityWeather = {
    val json: JsValue = Json.parse(resp.body)
    val data = json \ "query" \ "results" \ "channel"
    val cw = CityWeather(
      (data \ "location" \ "city").as[String],
      (data \ "item" \ "condition" \ "temp").as[String]
    )
    println("CityWeather.fromResponse finished")
    cw
  }
}
