package models.daos

import models._
import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future

import javax.inject.Inject
import play.api.db.slick.{ HasDatabaseConfigProvider, DatabaseConfigProvider }
import slick.driver.JdbcProfile
import models.Tables._
import org.joda.time.DateTime
import com.github.tototoshi.slick.H2JodaSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Give access to the goal object.
 */
class GoalDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends GoalDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def list() =
    db.run((Goals join Users).sortBy(_._1.timeLimit.desc).result).flatMap { row =>
      Future.successful(row.map {
        case (rGoal, rUser) =>
          Goal(rGoal.id, rGoal.name, rGoal.description, new DateTime(rGoal.timeLimit), rGoal.maxProgress,
            User(rUser.id, LoginInfo("twitter", rUser.socialId.toString), rUser.screenName, rUser.apiToken),
            new DateTime(rGoal.createdAt))
      })
    }

  def find(id: Int) =
    for {
      row <- db.run((Goals join Users).filter(_._1.id === id).result.headOption)
    } yield row match {
      case Some((rGoal, rUser)) =>
        Some(Goal(rGoal.id, rGoal.name, rGoal.description, new DateTime(rGoal.timeLimit), rGoal.maxProgress,
          User(rUser.id, LoginInfo("twitter", rUser.socialId.toString), rUser.screenName, rUser.apiToken),
          new DateTime(rGoal.createdAt)))
      case None => None
    }

  def create(goal: Goal) =
    db.run((Goals returning Goals.map(_.id)) += GoalsRow(goal.goalID, goal.name, goal.description, goal.timeLimit, goal.maxProgress, goal.createdBy.userID, goal.createdAt))
      .flatMap { id =>
        Future.successful(goal.copy(goalID = id))
      }

}
