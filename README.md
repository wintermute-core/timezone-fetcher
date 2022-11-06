# timezone-fetcher

Web application for fetching cities time zones.

## Tech stack

* Java 17
* Gradle 7
* Ratpack
* Lombok
* Mockito
* Junit5

## Application structure

Application entrypoint is `com.timezone.fetcher.Main`.

Important application packages:
* `com.timezone.fetcher.client` - implementation of Timezone DB client
* `com.timezone.fetcher.handler` - API request handlers

## Useful commands

Run application:
```
./gradlew run
```
Access application over http://localhost:5050/

Run tests:
```
./gradlew test
```

Run end to end integration tests:
```
./gradlew itest
```
Since will be performed real calls to TimezoneDB, `itest` take some time to run.

## API Calls

Example API calls:

```
curl "http://localhost:5050/time?country=RO&city=Bucharest"
{
  "status": "OK",
  "error": null,
  "payload": [
    {
      "zoneName": "Europe/Bucharest",
      "time": "2022-11-06 22:05:43"
    },
    {
      "zoneName": "Europe/Bucharest",
      "time": "2022-11-06 22:05:43"
    }
  ]
}

curl "http://localhost:5050/time?country=US&city=*Athens*" | jq

{
  "status": "OK",
  "error": null,
  "payload": [
    {
      "zoneName": "America/Chicago",
      "time": "2022-11-06 14:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/Chicago",
      "time": "2022-11-06 14:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/Detroit",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/New_York",
      "time": "2022-11-06 15:06:50"
    },
    {
      "zoneName": "America/Chicago",
      "time": "2022-11-06 14:06:50"
    }
  ]
}

```

## Configurations

Application can be configured by setting environment variables:
* `TIMEZONE_DB_ENDPOINT` - Timezone DB API endpoint 
* `TIMEZONE_DB_KEY` - API key used in Timezone DB requests

## Future work

* CICD integration
* Packaging application as Docker container
* Exposing Prometheus metrics
* Rate limiter
* Better integration tests

