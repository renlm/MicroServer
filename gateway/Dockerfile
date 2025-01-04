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
COPY --from=0 "/build/gateway/target/gateway.jar" app.jar
ADD gateway/start-up.sh /start-up.sh
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y tini curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && chmod +x /start-up.sh \
  && touch /app.jar
EXPOSE 7002
EXPOSE 9002
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/usr/bin/tini","--","/bin/bash","/start-up.sh"]