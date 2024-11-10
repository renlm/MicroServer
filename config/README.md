# 配置中心
## keyStore.jks
	keypass 与 storepass 要相同
	Warning:  Different store and key passwords not supported for PKCS12 KeyStores.
	$ keytool -genkeypair -alias alias -keyalg RSA -validity 365 -dname "C=CN" -keypass letmein -keystore keyStore.jks -storepass letmein
	
## 请求配置
	$ curl http://default:123654@localhost:7001/master/rabbitmq-dev.yaml
	$ curl http://default:123654@localhost:7001/master/rabbitmq-prod.yaml
	
	$ curl -X POST http://default:123654@localhost:7001/encrypt -s -d {明文}
	$ curl -X POST http://default:123654@localhost:7001/decrypt -s -d {密文}

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yaml
/{label}/{application}-{profile}.yaml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
