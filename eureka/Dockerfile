FROM openjdk:21-slim
ADD . /build
WORKDIR /build
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
RUN --mount=type=cache,target=/root/.m2 \
  # https://mirrors.tuna.tsinghua.edu.cn/help/debian/
  sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y maven \
  && mvn clean package -P ${PROFILES_ACTIVE}

FROM openjdk:21-slim
COPY --from=0 "/build/eureka/target/eureka.jar" app.jar
ADD eureka/start-up.sh /start-up.sh
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y tini curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && chmod +x /start-up.sh \
  && touch /app.jar
EXPOSE 7101
EXPOSE 9101
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["/usr/bin/tini","--","/bin/bash","/start-up.sh"]