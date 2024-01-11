package com.github.yoshiyoshifujii.removeMember.adapters.http

import io.circe
import io.circe.generic.auto._
import sttp.client4._
import sttp.client4.circe.asJson
import sttp.client4.okhttp.OkHttpSyncBackend

case class Rooms(chatworkBaseUri: String, chatworkToken: String) {
  import Rooms._

  lazy val gets: Response[Either[ResponseException[String, circe.Error], Vector[Room]]] = {
    val request = basicRequest
      .get(uri"$chatworkBaseUri/rooms")
      .header("accept", "application/json")
      .header("x-chatworktoken", chatworkToken)
      .response(asJson[Vector[Room]])

    val backend = OkHttpSyncBackend()
    request.send(backend)
  }

}

object Rooms {

  case class Room(
      room_id: Long,
      name: String,
      `type`: String,
      role: String
  )

}
