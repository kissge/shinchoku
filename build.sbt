import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences._

name := """scala-shinchoku"""

version := "4.0.0"

scalaVersion := "2.11.8"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.iheart" %% "ficus" % "1.2.6",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
  specs2 % Test,
  cache,
  filters,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "mysql" % "mysql-connector-java" % "5.1.40",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

routesImport += "utils.route.Binders._"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

//********************************************************
// Scalariform settings
//********************************************************

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(DanglingCloseParenthesis, Preserve)

//********************************************************
// slick-codegen settings
//********************************************************

slickCodegenSettings
sourceGenerators in Compile <+= slickCodegen
// TODO: Inject parameters
slickCodegenDatabaseUrl := ""
slickCodegenDatabaseUser := ""
slickCodegenDatabasePassword := ""
slickCodegenDriver := slick.driver.MySQLDriver
slickCodegenJdbcDriver := "com.mysql.jdbc.Driver"
slickCodegenOutputPackage := "models"
slickCodegenExcludedTables in Compile := Seq("schema_version")
slickCodegenOutputDir := (sourceManaged in Compile).value
slickCodegenCodeGenerator := { (model:  slick.model.Model) =>
  new slick.codegen.SourceCodeGenerator(model) {
        override def code =
          "import com.github.tototoshi.slick.H2JodaSupport._\n" + "import org.joda.time.DateTime\n" + super.code
    override def Table = new Table(_) {
      override def Column = new Column(_) {
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "DateTime" // kill j.s.Timestamp
          case _ =>
            super.rawType
        }
      }
    }
  }
}
