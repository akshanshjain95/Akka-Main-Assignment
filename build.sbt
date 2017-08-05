name := "Accounting-System"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.3"

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.3" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % "test"

coverageEnabled:=true
