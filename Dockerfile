FROM java:8
MAINTAINER soriole
VOLUME /tmp
ADD build/libs/kademlia-1.0.4.jar /app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8080/tcp  10210/udp
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

