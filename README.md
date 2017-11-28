❱ reduc.io ❰
========

> URL shortener service `http://reduc.io/wuXaq`

[![Build Status](https://travis-ci.org/ziyasal/reducio.svg?branch=master)](https://travis-ci.org/ziyasal/reducio) [![Coverage Status](https://coveralls.io/repos/github/ziyasal/reducio/badge.svg?branch=master)](https://coveralls.io/github/ziyasal/reducio?branch=master)

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

Run `docker-compose`, it will start `api`, `redis` and will expose api port to host.
```sh
docker-compose up
```
## Sample usage

```sh
# Shorten
curl -i http://localhost:9001 -F "url=https://www.amazon.com/Star-Wars-Battlefront-II-Digital/dp/B072JZZ4XD"

# Call shortened url
for ((i=1;i<=100;i++)); do curl -i "http://localhost:9001/SEwuXHhBQw"; done

# Get Stats
curl -i "http://localhost:9001/stats/?url=https://www.amazon.com/Star-Wars-Battlefront-II-Digital/dp/B072JZZ4XD"

# returns: {"callCount":100}
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

## Improvements
 - Implement Authentication using JWT authentication protocol with OAuth2 authentication framework
 - API Documentation using swagger or similar
 - Add Host blackListing
 - Add Retry using [Retry](https://github.com/softprops/retry)
 - Add Throttling using [akka-http-contrib](https://github.com/adhoclabs/akka-http-contrib)
 - Add Metrics support using [akka-http-metrics](https://github.com/Backline/akka-http-metrics)

ziλasal.
