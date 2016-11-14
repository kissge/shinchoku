package models

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param screenName The screen name of the authenticated provider.
 * @param apiToken The token used for invoking API calls.
 */
case class User(
  userID: Int,
  loginInfo: LoginInfo,
  screenName: String,
  apiToken: String) extends Identity {
}
