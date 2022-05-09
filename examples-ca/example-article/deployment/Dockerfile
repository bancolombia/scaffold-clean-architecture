FROM adoptopenjdk/openjdk11-openj9:alpine-slim
VOLUME /tmp
COPY *.jar crudTest.jar
RUN sh -c 'touch /crudTest.jar'
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar crudTest.jar" ]