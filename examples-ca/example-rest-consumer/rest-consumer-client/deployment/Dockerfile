FROM adoptopenjdk/openjdk11-openj9@sha256:15eb4c70a455781ecc93fcfad548502f78a9c0fb3fe727fd3199ff38d7b09279
VOLUME /tmp
COPY *.jar rest-consumer-client.jar
RUN sh -c 'touch /rest-consumer-client.jar'
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "/bin/sh", "-c", "java $JAVA_OPTS  -jar rest-consumer-client.jar" ]