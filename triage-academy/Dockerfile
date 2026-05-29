FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY triage-academy/gradle ./gradle
COPY triage-academy/gradlew .
COPY triage-academy/build.gradle.kts .
COPY triage-academy/settings.gradle.kts .
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY triage-academy/src ./src
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S triage && adduser -S triage -G triage

WORKDIR /app

COPY --from=build /app/build/libs/*.jar triage-backend.jar

RUN chown triage:triage triage-backend.jar

USER triage

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "triage-backend.jar"]