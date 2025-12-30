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
        py3-pip

RUN wget https://www.johnvansickle.com/ffmpeg/old-releases/ffmpeg-6.0.1-amd64-static.tar.xz \
    && tar -xf ffmpeg-6.0.1-amd64-static.tar.xz \
    && mv ffmpeg-6.0.1-amd64-static/ffmpeg /usr/local/bin/ \
    && mv ffmpeg-6.0.1-amd64-static/ffprobe /usr/local/bin/ \
    && chmod +x /usr/local/bin/ffmpeg /usr/local/bin/ffprobe \
    && rm -rf ffmpeg-6.0.1-amd64-static*

RUN pip3 install --upgrade --no-cache-dir pip --break-system-packages \
 && pip3 install --no-cache-dir awscli --break-system-packages \
 && rm -rf /var/cache/apk/* \

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
