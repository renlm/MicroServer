FROM registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2
ADD . /build
WORKDIR /build
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
RUN --mount=type=cache,target=/root/.m2 cd config && mvn clean -Pnative -P ${PROFILES_ACTIVE} native:compile

FROM alpine
COPY --from=0 "/build/config/target/config" server
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk upgrade \
  && apk --no-cache add tzdata curl \
  && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && touch /server
EXPOSE 7001
EXPOSE 9001
ARG PROFILES_ACTIVE
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENTRYPOINT ["sh","-c","./server"]