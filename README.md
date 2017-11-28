Reduc.io
========

> URL Shortener service

[![Build Status](https://travis-ci.org/ziyasal/Reducio.svg?branch=master)](https://travis-ci.org/ziyasal/Reducio)

## Tech stack
 - [Scala](https://www.scala-lang.org/)
 - [Akka Http](https://github.com/akka/akka-http)
 - [Circe](https://github.com/circe/circe)
 - [Redis](https://github.com/antirez/redis)
 - [ScalaTest](http://www.scalatest.org/)
 - [Specs2](https://github.com/etorreborre/specs2)
 - [Mockito](https://github.com/mockito/mockito)
 - [Gatling](https://gatling.io/)

## Commands
### Run
:warning: _If you want to use **`docker-compose`**, you can skip manual steps._

### Docker

Run `docker-compose`, it will start `api`, `redis` and will expose api port to host.
```sh
docker-compose up
```

### Create executable
```sh
sbt packageBin
```

### Test
```sh
sbt test
```
#### Gatling Simulation
```sh
sbt

# Run simulation
sbt> gatling:test

# To see report
sbt> gatling:latestReport
```

### Coverage
```sh
sbt clean coverage test
```

**To create coverage report**
```sh
sbt coverageReport
```

## Improvements / TODO
 - AUTH using JWT authentication protocol with OAuth2 authentication framework
 - API Documentation using swagger or similar tool/lib
 - Integration Testing
   2. Redis integration tests using [`embedded redis`](https://github.com/kstyrc/embedded-redis) or similar tool/lib

ziλasal.
