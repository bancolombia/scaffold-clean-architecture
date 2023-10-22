FROM amazoncorretto:17-alpine
COPY *.jar channel-operations.jar
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "/bin/sh", "-c", "java $JAVA_OPTS -jar channel-operations.jar" ]