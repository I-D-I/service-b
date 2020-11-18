### STAGE 1: Setup ###
FROM maven as builder

WORKDIR /app
COPY src ./src
COPY pom.xml .

RUN mvn package -DskipTests

### STAGE 2: Setup ###
FROM openjdk:11-jre-slim

ENV JAEGER_SERVICE_NAME=service-four\
  JAEGER_ENDPOINT=http://jaeger-collector.istio-system.svc:9411\
  JAEGER_PROPAGATION=b3\
  JAEGER_SAMPLER_TYPE=const\
  JAEGER_SAMPLER_PARAM=1
  
COPY --from=builder /app/target/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
