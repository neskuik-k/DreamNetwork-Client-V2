FROM openjdk:8
COPY . /tmp
WORKDIR /tmp
CMD ["java","-jar","DreamNetworkV2-1.0-SNAPSHOT-shaded.jar"]