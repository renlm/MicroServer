## 配置中心（Graalvm）
### 输出编译配置
	$ docker pull alpine
	$ docker pull registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2
	$ docker tag registry.cn-hangzhou.aliyuncs.com/rlm/graalvm-ce:21.0.2 graalvm-ce:21.0.2
	$ docker run -it --rm -p 7001:7001 -v /root/.m2:/root/.m2 -v /root/GrmServer:/build graalvm-ce:21.0.2 bash
	bash-5.1# cd /build/config
	
	静态编译
	bash-5.1# mvn clean -Pnative -P dev native:compile
	bash-5.1# ldd target/config
        not a dynamic executable
	
	测试覆盖
	bash-5.1# mvn clean package -P dev
	bash-5.1# java -agentlib:native-image-agent=config-output-dir=./output -jar target/config.jar
	
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
	$ docker build -t config-native-server:0.0.1 --build-arg PROFILES_ACTIVE=prod -f config/Dockerfile .
	$ docker run -it --rm -p 7001:7001 -p 9001:9001 -e PROFILES_ACTIVE=dev config-native-server:0.0.1
	
### 手动推送镜像
	$ docker login --username=renlm@21cn.com registry.cn-hangzhou.aliyuncs.com
	$ docker tag config-native-server:0.0.1 registry.cn-hangzhou.aliyuncs.com/rlm/config-native-server:0.0.1
	$ docker push registry.cn-hangzhou.aliyuncs.com/rlm/config-native-server:0.0.1
	
### keyStore.jks
	keypass 与 storepass 要相同
	Warning:  Different store and key passwords not supported for PKCS12 KeyStores.
	$ keytool -genkeypair -alias alias -keyalg RSA -validity 365 -dname "C=CN" -keypass letmein -keystore keyStore.jks -storepass letmein
	
### 获取配置
	$ curl http://default:123654@localhost:7001/master/rabbitmq-dev.yaml
	$ curl http://default:123654@localhost:7001/master/rabbitmq-prod.yaml
	
	$ curl -X POST http://default:123654@localhost:7001/encrypt -s -d {明文}
	$ curl -X POST http://default:123654@localhost:7001/decrypt -s -d {密文}
	
### 请求规则

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yaml
/{label}/{application}-{profile}.yaml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
