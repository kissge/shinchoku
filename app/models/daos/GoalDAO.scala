package models.daos

import models.Goal

import scala.concurrent.Future

import models.Tables._

/**
 * Give access to the goal object.
 */
trait GoalDAO {

  /**
   * List the goals.
   */
  def list(): Future[Seq[Goal]]

  /**
   *  Find a goal.
   */
  def find(id: Int): Future[Option[Goal]]

  /**
   * Create a goal.
   */
  def create(goal: Goal): Future[Goal]
}
