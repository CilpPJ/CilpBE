FROM openjdk:17-jdk

# 빌드된 JAR 복사
COPY ./build/libs/*SNAPSHOT.jar project.jar

ENTRYPOINT ["java", "-jar", "/project.jar"]
