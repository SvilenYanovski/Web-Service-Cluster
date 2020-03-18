FROM openjdk:8-jre-alpine
COPY ./target/kv-0.0.1-SNAPSHOT.jar /usr/src/reactive-kv/
WORKDIR /usr/src/reactive-kv
CMD ["java", "-jar", "kv-0.0.1-SNAPSHOT.jar"]
