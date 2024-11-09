## 注册中心
### Noraml Image
	$ docker pull registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2
	$ docker pull openjdk:21-slim
	$ docker build -t eureka-server:0.0.1 --build-arg PROFILES_ACTIVE=prod -f eureka/Dockerfile .
	$ docker run -it --rm -p 7101:7101 -p 9101:9101 -e PROFILES_ACTIVE=dev eureka-server:0.0.1
	
	$ curl http://default:123654@localhost:7101/eureka/apps
	$ curl http://default:123654@localhost:7101/eureka/v2/apps
	
### 手动推送镜像
	$ docker login --username=renlm@21cn.com registry.cn-hangzhou.aliyuncs.com
	$ docker tag eureka-server:0.0.1 registry.cn-hangzhou.aliyuncs.com/rlm/eureka-server:0.0.1
	$ docker push registry.cn-hangzhou.aliyuncs.com/rlm/eureka-server:0.0.1
