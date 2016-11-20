package modules

import com.google.inject.AbstractModule
import models.daos._
import net.codingwell.scalaguice.ScalaModule

/**
 * I don't know what the fuck is this
 */
class WhatTheFuckIsThisModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    bind[GoalDAO].to[GoalDAOImpl]
  }

}
