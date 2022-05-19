FROM adoptopenjdk/openjdk8-openj9@sha256:75225e1550aab2a1c57c806d18ed301419cc6dcadb521effe7dc0652a160a3a1
VOLUME /tmp
COPY *.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar /app.jar" ]
