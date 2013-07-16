import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ctr"
  val appVersion      = "0.0.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.specs2" %% "specs2" % "2.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
