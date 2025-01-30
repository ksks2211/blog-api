#!/bin/sh

# 지연 시간 설정
# echo "Waiting for 50 seconds..."
# sleep 50

# 애플리케이션 실행
exec java -Duser.timezone=UTC -jar -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE}" app.jar