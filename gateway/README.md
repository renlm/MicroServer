## 服务网关（Graalvm）
### 输出编译配置
	$ docker pull alpine
	$ docker pull registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2
	$ docker tag registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2 graalvm-ce:21.0.2
	$ docker run -it --rm -v /root/.m2:/root/.m2 -v /root/GrmServer:/build graalvm-ce:21.0.2 bash
	bash-5.1# cd /build/gateway
	
	静态编译
	bash-5.1# mvn clean -Pnative native:compile
	bash-5.1# ldd target/gateway
        not a dynamic executable
	
	测试覆盖
	bash-5.1# mvn clean package
	bash-5.1# java -agentlib:native-image-agent=config-output-dir=./output -jar target/gateway.jar
	
```	
将输出拷贝到resources/META-INF/native-image
新建native-image.properties，设置构建参数
自动加载
.
|-- META-INF
|   `-- native-image
|       |-- jni-config.json
|       |-- predefined-classes-config.json
|       |-- proxy-config.json
|       |-- reflect-config.json
|       |-- resource-config.json
|       `-- serialization-config.json
```

### Native Image
	$ docker build -t gateway-native-server:0.0.1 --build-arg PROFILES_ACTIVE=prod -f gateway/Dockerfile .
	$ docker run -it --rm -p 7002:7002 -p 9002:9002 -e PROFILES_ACTIVE=dev gateway-native-server:0.0.1
	
### 手动推送镜像
	$ docker login --username=renlm@21cn.com registry.cn-hangzhou.aliyuncs.com
	$ docker tag gateway-native-server:0.0.1 registry.cn-hangzhou.aliyuncs.com/rlm/gateway-native-server:0.0.1
	$ docker push registry.cn-hangzhou.aliyuncs.com/rlm/gateway-native-server:0.0.1
