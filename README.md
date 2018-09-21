# ❱ reduc.io ❰

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

## Alternative solutions

We could use following short code gen also:
```
substr(base62(md5(url)), 6) = 62 ^ 6 //unique short urls
```

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

Simulation users count can be set in `application.conf` in test resources.

```sh
# terminal 1
sbt run

# Run simulation in terminal 2
sbt gatling:test gatling:latestReport
```

### Coverage with Report

```sh
sbt clean coverage test coverageReport
```

## Improvements
- Seperate read and write API's
- Move metrics to different data store and API
- Add DB support by having write master/replicaass and have read replicas
- Move hit counts to MapReduce job and generate them from the Web Server logs
- Implement Authentication using JWT authentication protocol with OAuth2 authentication framework
- API Documentation using swagger or similar
- Add Host blackListing
- Add Retry policies for `Redis` calls using [Retry](https://github.com/softprops/retry) or similar one
- Add Throttling using [akka-http-contrib](https://github.com/adhoclabs/akka-http-contrib)
- Add Metrics support using [akka-http-metrics](https://github.com/Backline/akka-http-metrics)


z i λ a s a l.
