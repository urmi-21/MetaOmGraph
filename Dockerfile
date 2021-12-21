# syntax=docker/dockerfile:1

## Init
## --------------------- ##
FROM openjdk:17-bullseye as init
WORKDIR /opt/mog
## --------------------- ##

RUN apt update && apt install maven file unzip python3-pip -y

## Build
## --------------------- ##
FROM init as setup
WORKDIR /opt/mog
## --------------------- ##

COPY ./scripts ./scripts
COPY ./pom.xml ./pom.xml
COPY ./src ./src

ENV BUILD_MODE 2
RUN bash ./scripts/bootstrap.sh

## Build
## --------------------- ##
FROM setup as build
WORKDIR /opt/mog
## --------------------- ##

COPY ./scripts ./scripts
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Build the project!
ENV BUILD_MODE 2
RUN bash ./scripts/build.sh
RUN cp ./target/metaomgraph4*.jar mog.jar



## Live Image
## --------------------- ##
FROM build as live
## --------------------- ##

# Graphics stuff
RUN apt update && apt install -y libxext6 libxrender1 libxtst6

COPY --from=build ./mog.jar ./mog.jar

# Launch!
ENTRYPOINT ["java"]
CMD ["-jar", "./mog.jar"]
