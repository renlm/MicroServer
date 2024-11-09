FROM openjdk:21-slim
ADD . /build
WORKDIR /build
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
RUN --mount=type=cache,target=/root/.m2 \
  # https://mirrors.tuna.tsinghua.edu.cn/help/debian/
  cp -a /etc/apt/sources.list /etc/apt/sources.list.bak \
  && sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && sed -i 's/security.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && apt update \
  && apt install -y maven \
  && mvn clean package -P ${PROFILES_ACTIVE}

FROM openjdk:21-slim
COPY --from=0 "/build/config/target/config" app.jar
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add tzdata curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && touch /app.jar
EXPOSE 7080
EXPOSE 7090
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/sbin/tini","--","java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]