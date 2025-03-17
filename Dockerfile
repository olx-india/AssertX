FROM docker:24.0.2-dind-alpine3.18

# Install dependencies
RUN apk add --no-cache \
        bash \
        maven \
        openjdk17 \
        git \
        docker-compose \ 
        ca-certificates \
        wget \
        python3 \
        py3-pip \
    && pip3 install --upgrade --no-cache-dir pip \
    && pip3 install --no-cache-dir \
        awscli \
    && rm -rf /var/cache/apk/*

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
