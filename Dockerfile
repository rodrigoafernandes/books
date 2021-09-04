FROM quay.io/quarkus/ubi-quarkus-native-image:21.1-java11 as build
WORKDIR /usr/src/app
COPY . /usr/src/app/
USER root
RUN ./gradlew clean
RUN chown -R quarkus /usr/src/app
USER quarkus
RUN ./gradlew clean build -Dquarkus.package.type=native -x test

FROM quay.io/quarkus/quarkus-distroless-image:1.0

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/build/*-runner /usr/src/app/books

USER nonroot

CMD ["./books", "-Dquarkus.http.host=0.0.0.0"]