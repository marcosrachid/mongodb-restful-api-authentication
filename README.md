# mongodb-restful-api-authentication
Mongodb restful api with oauth2 authentication

## Tecnologias

| Technology | Version|
| ------ | ----------- |
|**`Java`**|8.\*|
|**`Spring Boot`**|2.0.5.RELEASE|
|**`Apache Maven`**|3.3.9|
|**`MongoDB`**|v3.6.3-rc1|

## Premises

MongoDB must be running on for `development` and `test` environment.

## Builds

run in `modules`:
```bash
 mvn clean install
``` 
or
```
  mvn clean install -Pdev
```

## Run Application

run in `modules/rest`:
```
  mvn spring-boot:run
```

## Run Tests

run in `modules`:
```
  mvn clean install -Pfunctest
```

## Swagger

[http://{domain}:{port}/swagger-ui.html]

## Oauth2 Default Requests

To get admin token:
```
curl -X GET 'http://localhost:8080/oauth/token?grant_type=password&username=admin&password=admin'
```
Obs: password from admin is registered on first startup and can be modified on properties file.

To refresh token:
```
curl -X POST \
  http://localhost:8080/oauth/token \
  -H 'authorization: Basic Y2FtcHNpdGU6Y2FtcHNpdGU=' \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'grant_type=refresh_token&refresh_token=b229ad65-ab37-4b12-8229-e8487d935f0d'
```
Obs: Authentication that generates the "authorization: Basic" by default is `campsite:campsite`

Example of request:
```
curl -X DELETE http://localhost:8080/api/v1/users/marcosrachid -H 'authorization: Bearer 3d04aad7-c6b7-4d28-8e3b-600f92a6d108'
```
