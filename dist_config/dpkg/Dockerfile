FROM ubuntu:17.10

# Setup Java JDK9
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y openjdk-9-jdk
RUN apt-get install -y git
RUN apt-get install -y build-essential

# Additional Installs
RUN apt-get install -y wget


# Setup Leiningen
WORKDIR /usr/bin
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein .
RUN chmod 775 lein
RUN chown root:root lein


# Setup User and do first time leiningen self-install
RUN useradd --create-home \
            --shell /bin/bash \
            builderbob
USER builderbob
WORKDIR /home/builderbob
RUN lein

# Setup GraalVM in the builderbob home folder
add https://github.com/oracle/graal/releases/download/vm-1.0.0-rc2/graalvm-ce-1.0.0-rc2-linux-amd64.tar.gz .
ENV GRAAL_HOME=/home/builderbob/graalvm-ce-1.0.0-rc2

# Setup the project
RUN git clone -b master http://github.com/benzap/fif
WORKDIR ./fif
ENTRYPOINT make dpkg
