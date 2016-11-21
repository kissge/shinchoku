package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import utils.auth.DefaultEnv
import models._
import models.daos._
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  implicit val webJarAssets: WebJarAssets,
  goalDAO: GoalDAO)
  extends Controller with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = silhouette.SecuredAction.async { implicit request =>
    for {
      goals <- goalDAO.list
    } yield Ok(views.html.home(request.identity, goals))
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  def newGoal = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.newGoal(request.identity, goalForm)))
  }

  def newGoalExecute = silhouette.SecuredAction.async { implicit request =>
    goalForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(views.html.newGoal(request.identity, errors))),
      {
        case (name, description, timeLimit, maxProgress) =>
          goalDAO.create(new Goal(0, name, description, timeLimit, maxProgress, request.identity, DateTime.now))
          Future.successful(Redirect(routes.ApplicationController.index))
      }
    )
  }

  val goalForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "timeLimit" -> jodaDate(pattern = "yyyy-MM-dd HH:mm:ss").verifying("error.future", _.isAfterNow),
      "maxProgress" -> number(min = 1)
    )
  )
}
