### STAGE 1: Setup ###
FROM maven as builder

WORKDIR /app
COPY src ./src
COPY pom.xml .

RUN mvn package -DskipTests

### STAGE 2: Setup ###
FROM openjdk:11-jre-slim

USER root

RUN apt update && apt install -y curl

ENV JAEGER_SERVICE_NAME=service-b\
  JAEGER_ENDPOINT=http://jaeger-collector.istio-system.svc.cluster.local:9411\
  JAEGER_PROPAGATION=b3\
  JAEGER_SAMPLER_TYPE=const\
  JAEGER_SAMPLER_PARAM=1
  
EXPOSE 8080
  
COPY --from=builder /app/target/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
