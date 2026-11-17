FROM eclipse-temurin:25-jdk-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY *.jar exampleDynamoDB.jar
RUN chown -R appuser:appgroup /app
ENV JAVA_OPTS="-XX:+UseCompactObjectHeaders -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "/bin/sh", "-c", "/opt/java/openjdk/bin/java $JAVA_OPTS  -jar /app/exampleDynamoDB.jar" ]
