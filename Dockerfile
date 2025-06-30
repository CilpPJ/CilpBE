FROM openjdk:17-jdk

COPY ./build/libs/*SNAPSHOP.jar project.jar

ENTRYPOINT ["java", "-jar", "/project.jar"]