package models

import org.joda.time.DateTime

/**
 * The goal object.
 */
case class Goal(
  goalID: Int,
  name: String,
  description: String,
  timeLimit: DateTime,
  maxProgress: Int,
  createdBy: User,
  createdAt: DateTime
)
