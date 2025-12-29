FROM docker:24.0.2-dind-alpine3.18

# Install dependencies
RUN apk add --no-cache \
        bash \
        maven \
        openjdk17 \
        git \
        ca-certificates \
        wget \
        python3 \
        py3-pip \
    && pip3 install --upgrade --no-cache-dir pip \
    && pip3 install --no-cache-dir \
        awscli \
    && rm -rf /var/cache/apk/*

RUN mkdir -p /usr/local/lib/docker/cli-plugins \
 && wget -q https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-linux-x86_64 \
    -O /usr/local/lib/docker/cli-plugins/docker-compose \
 && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose \
 && ln -sf /usr/local/lib/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose

# Configure JDK11
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="$JAVA_HOME/bin:${PATH}"

# Copy assertx scripts to containers
COPY ./pipeline-scripts/execute-it.sh app/
COPY ./pipeline-scripts/execute-it-gradle.sh app/
COPY ./pipeline-scripts/upload-report.sh app/

WORKDIR /app

RUN chmod +x *.sh
RUN /bin/bash
