FROM openjdk:21-slim
ADD . /build
WORKDIR /build
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
RUN --mount=type=cache,target=/root/.m2 \
  # https://developer.aliyun.com/mirror/ubuntu
  sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y maven \
  && mvn clean package -P ${PROFILES_ACTIVE}

FROM openjdk:21-slim
COPY --from=0 "/build/core/core-service/target/core-service.jar" app.jar
ADD core/core-service/start-up.sh /start-up.sh
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y tini curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && chmod +x /start-up.sh \
  && touch /app.jar
EXPOSE 8001
EXPOSE 8091
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/usr/bin/tini","--","/bin/bash","/start-up.sh"]