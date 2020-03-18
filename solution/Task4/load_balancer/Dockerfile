FROM openjdk:8-jre-alpine
COPY ./target/load_balancer-0.0.1-SNAPSHOT.jar /usr/src/reactive-kv/
WORKDIR /usr/src/reactive-kv
CMD ["java", "-jar", "load_balancer-0.0.1-SNAPSHOT.jar"]
