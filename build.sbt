name := "fyber-tech-assignment"

version := "1.0"

scalaVersion := "2.12.2"

organization := "com.evans"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

mainClass in Compile := Some("com.evans.Main")

val scalaTestV = "3.0.1"

libraryDependencies ++= {
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
  )
}
