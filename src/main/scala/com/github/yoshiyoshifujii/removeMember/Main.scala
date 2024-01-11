package com.github.yoshiyoshifujii.removeMember

import com.github.yoshiyoshifujii.removeMember.adapters.http.RoomMembers.RoomMember
import com.github.yoshiyoshifujii.removeMember.adapters.http.{ RoomMembers, Rooms }

import scala.io.StdIn
import scala.util.Try

object Main extends App {
  import Rooms._

  private val ChatworkBaseUri = "https://api.chatwork.com/v2"
  private val ChatworkToken   = sys.env("CHATWORK_TOKEN")
  private val RemoveAccountId = sys.env("REMOVE_ACCOUNT_ID")

  private val roomsService: Rooms             = Rooms(ChatworkBaseUri, ChatworkToken)
  private val roomMembersService: RoomMembers = RoomMembers(ChatworkBaseUri, ChatworkToken)

  private def removeAccount(room: Room, roomMembers: Vector[RoomMember]): Unit = {
    println(s"Room Name: ${room.name}")
    println(s"Before  RoomMembers: $roomMembers")
    val filteredRoomMembers = roomMembers.filterNot(_.account_id.toString == RemoveAccountId)
    println(s"After   RoomMembers: $filteredRoomMembers")

    val filteredRoomMemberMap = filteredRoomMembers
      .map { roomMember =>
        roomMember.role -> roomMember.account_id
      }.foldLeft(Map.empty[String, Vector[Long]]) { case (acc, (role, account_id)) =>
        val accountIds = acc.getOrElse(role, Vector.empty[Long])
        acc + (role -> accountIds.:+(account_id))
      }

    StdIn.readLine("Remove Room Member ok?") match {
      case "yes" =>
        roomMembersService
          .put(
            room.room_id,
            filteredRoomMemberMap.get("admin"),
            filteredRoomMemberMap.get("member"),
            filteredRoomMemberMap.get("readonly")
          ).body match {
          case Left(except) => throw new RuntimeException(except)
          case Right(ok)    => println(s"Removed RoomMembers: $ok")
        }
      case _ =>
        println("Skip")
    }
  }

  private def removeAccountFrom(room: Room): Unit = {
    Thread.sleep(1000)
    roomMembersService.gets(room.room_id).body match {
      case Left(except) => throw new RuntimeException(except)
      case Right(roomMembers) if roomMembers.exists(_.account_id.toString == RemoveAccountId) =>
        removeAccount(room, roomMembers)
      case _ => ()
    }
  }

  private def removeAccountFrom(rooms: Vector[Room]): Either[Throwable, Unit] =
    Try(rooms.foreach(removeAccountFrom)).toEither

  private def execute(): Unit = {
    for {
      rooms <- roomsService.gets.body
      filteredRooms = rooms.filter(_.`type` == "group").filter(_.role == "admin")
      _ <- removeAccountFrom(filteredRooms)
    } yield ()
  }

  execute()
}
