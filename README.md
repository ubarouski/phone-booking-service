# Phone booking service
## Overview

Phone booking service allows a phone to be booked / returned. 

## Run on local environment
Requires Java 21 and maven

mvn spring-boot:run

## Usage

Swagger documentation can be found [here](http://localhost:8080/webjars/swagger-ui/index.html).

## Scenario

Find phone infornation with booking status
```
curl -X 'GET' \
  'http://localhost:8080/v1/phones?brand=Nokia&model=3310' \
  -H 'accept: */*'
```

Book the phone by IMEI

```
curl -X 'POST' \
  'http://localhost:8080/v1/phones/355623112522310' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "userName": "Vova",
  "action": "BOOK"
}'
```

Return the phone
```
curl -X 'POST' \
  'http://localhost:8080/v1/phones/355623112522310' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "action": "RETURN"
}'
```