FROM docker:25.0.0-dind-alpine3.19

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
        ffmpeg

RUN pip3 install --upgrade --no-cache-dir pip --break-system-packages \
 && pip3 install --no-cache-dir awscli --break-system-packages \
 && rm -rf /var/cache/apk/*

# Configure JDK17
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="$JAVA_HOME/bin:${PATH}"

# Copy assertx scripts to containers
COPY ./pipeline-scripts/execute-it.sh app/
COPY ./pipeline-scripts/execute-it-gradle.sh app/
COPY ./pipeline-scripts/upload-report.sh app/

WORKDIR /app

RUN chmod +x *.sh
RUN /bin/bash
