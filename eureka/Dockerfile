FROM registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2
ADD . /build
WORKDIR /build
RUN --mount=type=cache,target=/root/.m2 cd eureka && mvn clean package

FROM openjdk:21-slim
COPY --from=0 "/build/eureka/target/eureka.jar" app.jar
RUN \
  # https://mirrors.tuna.tsinghua.edu.cn/help/debian/
  sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
  && apt update \
  && apt install -y curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && touch /app.jar
EXPOSE 7001
EXPOSE 9001
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=${PROFILES_ACTIVE}"]