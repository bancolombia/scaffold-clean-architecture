FROM eclipse-temurin:17-jdk-alpine
COPY *.jar r2dbc-example.jar
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "/bin/sh", "-c", "java $JAVA_OPTS -jar r2dbc-example.jar" ]
