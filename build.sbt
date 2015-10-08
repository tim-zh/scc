name := "scc"

version := "1.0"

scalaVersion := "2.11.7"

unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
    Seq( base / "src/main/assets" )
}

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % Test,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % Test,
    "org.scalatest"       %%  "scalatest"     % "2.2.4" % Test
  )
}

Revolver.settings

lazy val scc = (project in file(".")).enablePlugins(SbtTwirl)