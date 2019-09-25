The steps I have performed is to create an external network called `localstack` `docker network create localstack` then in the folder `working/`:

`docker-compose up -d`

`./gradlew clean build`

`npm install`

`sls deploy --stage local`

