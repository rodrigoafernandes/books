FROM openjdk:11.0.8-jdk-slim as build
WORKDIR /usr/src/app
COPY . /usr/src/app/
RUN ./gradlew clean quarkusBuild -Dquarkus.package.type=uber-jar -x test

FROM openjdk:11-jre-slim

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/build/books-*-runner.jar /usr/src/app/books.jar

ENTRYPOINT java ${ADDITIONAL_OPTS} -jar books.jar