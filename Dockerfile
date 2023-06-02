FROM bellsoft/liberica-openjdk-alpine:17
ADD target/cat-photo-server.jar cat-photo-server.jar
ADD data data
EXPOSE 8080
ENTRYPOINT [ "java", "-jar",  "cat-photo-server.jar"]