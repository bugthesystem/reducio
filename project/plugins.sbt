resolvers += Classpaths.sbtPluginReleases
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.2")

addSbtPlugin("io.spray" %% "sbt-revolver" % "0.9.1")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.7.5")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")
addSbtPlugin("io.gatling" % "gatling-sbt" % "3.2.1")