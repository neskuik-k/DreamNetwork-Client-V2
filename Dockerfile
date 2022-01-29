FROM openjdk:8
COPY . /target
WORKDIR /target
CMD ["/bin/sh", "-c", "ls"]
ENTRYPOINT ["java","-jar","target/DreamNetworkV2-1.9.0-SNAPSHOT-prod.jar"]