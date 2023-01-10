FROM openjdk:17-jdk-slim
RUN apt-get update && \
    apt-get install -y chromium --no-install-recommends && \
    apt-get remove --purge --auto-remove -y curl && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /var/cache/apt/*
