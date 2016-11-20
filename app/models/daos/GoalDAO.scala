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

}
