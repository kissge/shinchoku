package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

import javax.inject.Inject
import play.api.db.slick.{ HasDatabaseConfigProvider, DatabaseConfigProvider }
import slick.driver.JdbcProfile
import models.Tables._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) =
    db.run(Users.filter(_.socialId === loginInfo.providerKey.toLong).result)
      .map(_.headOption)
      .flatMap { row =>
        Future.successful(row.map { row2 =>
          User(row2.id, LoginInfo("twitter", row2.socialId.toString), row2.screenName, row2.avatarUrl, row2.apiToken)
        })
      }
  // なんかもっと良い書き方ありそうだなあ

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Int) =
    db.run(Users.filter(_.id === userID).result)
      .map(_.headOption)
      .flatMap { row =>
        Future.successful(row.map { row2 =>
          User(row2.id, LoginInfo("twitter", row2.socialId.toString), row2.screenName, row2.avatarUrl, row2.apiToken)
        })
      }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    db.run(Users.insertOrUpdate(UsersRow(user.userID, user.loginInfo.providerKey.toLong, user.screenName, user.avatarURL, user.apiToken)))
    // implicitConversionで書き直したい
    Future.successful(user)
  }
}
