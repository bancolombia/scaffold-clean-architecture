FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY *.jar ExampleRedis.jar
RUN sh -c 'touch /ExampleRedis.jar'
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "/bin/sh", "-c", "java $JAVA_OPTS  -jar ExampleRedis.jar" ]