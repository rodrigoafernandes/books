FROM quay.io/quarkus/ubi-quarkus-native-image:21.0.0-java11 as build
WORKDIR /usr/src/app
COPY . /usr/src/app/
USER root
RUN ./gradlew clean
RUN chown -R quarkus /usr/src/app
USER quarkus
RUN ./gradlew clean build -Dquarkus.package.type=native

FROM quay.io/quarkus/quarkus-distroless-image:1.0

COPY --from=build /usr/src/app/build/*-runner /books

USER nonroot

CMD ./books -Dquarkus.http.host=0.0.0.0