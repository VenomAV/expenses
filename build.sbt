name := "Expenses"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-feature",
  "-Ypartial-unification",
  "-language:higherKinds"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
libraryDependencies += "org.typelevel"  %% "squants"  % "1.3.0"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"

libraryDependencies ++= Seq(

  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "0.5.3",

  // And add any of these as needed
//  "org.tpolecat" %% "doobie-h2"        % "0.5.3", // H2 driver 1.4.197 + type mappings.
//  "org.tpolecat" %% "doobie-hikari"    % "0.5.3", // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "0.5.3"  // Postgres driver 42.2.2 + type mappings.
//  "org.tpolecat" %% "doobie-specs2"    % "0.5.3", // Specs2 support for typechecking statements.
//  "org.tpolecat" %% "doobie-scalatest" % "0.5.3"  // ScalaTest support for typechecking statements.
)

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)