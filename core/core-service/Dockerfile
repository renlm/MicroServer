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
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y tini curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && touch /app.jar
EXPOSE 7001
EXPOSE 9001
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/usr/bin/tini","--","java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]