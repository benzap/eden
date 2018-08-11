FROM ubuntu:18.04

RUN apt-get update \
  && apt-get install --yes wget \
  && rm -rf /var/lib/apt/lists/*
RUN wget https://github.com/benzap/eden/releases/download/0.8.0/eden-0.8.0-amd64.deb
RUN dpkg --install eden-0.8.0-amd64.deb

ENTRYPOINT ["/usr/bin/eden"]
