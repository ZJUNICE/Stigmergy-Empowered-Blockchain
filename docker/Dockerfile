FROM maven:3.8.6-jdk-8
MAINTAINER LI DX

RUN mkdir -p /workspace
WORKDIR /workspace
COPY . /workspace
RUN mvn install
RUN rm -rf src

CMD ["/bin/bash"]