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
COPY --from=0 "/build/core/core-web/target/core-web.jar" app.jar
ADD core/core-web/start-up.sh /start-up.sh
ADD repo/fonts/msyh.ttf /usr/share/fonts/myfonts
ADD repo/fonts/msyhbd.ttf /usr/share/fonts/myfonts
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y tini curl fontconfig \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && chmod +x /start-up.sh \
  && touch /app.jar
EXPOSE 7001
EXPOSE 9001
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/usr/bin/tini","--","/bin/bash","/start-up.sh"]