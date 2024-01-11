package com.github.yoshiyoshifujii.removeMember.adapters.http

import io.circe
import io.circe.generic.auto._
import sttp.client4._
import sttp.client4.circe.asJson
import sttp.client4.okhttp.OkHttpSyncBackend

case class RoomMembers(chatworkBaseUri: String, chatworkToken: String) {
  import RoomMembers._

  def gets(room_id: Long): Response[Either[ResponseException[String, circe.Error], Vector[RoomMember]]] = {
    val request = basicRequest
      .get(uri"$chatworkBaseUri/rooms/${room_id.toString}/members")
      .header("accept", "application/json")
      .header("x-chatworktoken", chatworkToken)
      .response(asJson[Vector[RoomMember]])

    val backend = OkHttpSyncBackend()
    request.send(backend)
  }

  def put(
      room_id: Long,
      members_admin_ids: Option[Vector[Long]],
      members_member_ids: Option[Vector[Long]],
      members_readonly_ids: Option[Vector[Long]]
  ): Response[Either[ResponseException[String, circe.Error], PutOk]] = {
    val bodyMap = Vector(
      members_admin_ids.map(v => "members_admin_ids" -> v.mkString(",")),
      members_member_ids.map(v => "members_member_ids" -> v.mkString(",")),
      members_readonly_ids.map(v => "members_readonly_ids" -> v.mkString(","))
    ).flatten.toMap

    val request = basicRequest
      .put(uri"$chatworkBaseUri/rooms/${room_id.toString}/members")
      .body(bodyMap)
      .header("accept", "application/json")
      .header("content-type", "application/x-www-form-urlencoded")
      .header("x-chatworktoken", chatworkToken)
      .response(asJson[PutOk])

    val backend = OkHttpSyncBackend()
    request.send(backend)
  }

}

object RoomMembers {

  case class RoomMember(
      account_id: Long,
      role: String,
      name: String
  )

  case class PutOk(
      admin: Vector[Long],
      member: Vector[Long],
      readonly: Vector[Long]
  )

}
