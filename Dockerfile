FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build -x test



FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
# 스크립트를 이미지에 복사
COPY entrypoint.sh /app/entrypoint.sh


EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=deploy



# 실행 권한 부여
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]