# 环境配置

## Docker
	Ubuntu

### 安装与使用
	$ apt update
	$ apt install -y tree docker.io docker-buildx docker-compose

```
镜像加速
$ cat <<EOF | tee /etc/docker/daemon.json
{
  "registry-mirrors": [ 
    "https://docker-io.renlm.cn"
  ],
  "log-driver": "json-file",
  "log-opts": { "max-size": "500m", "max-file": "3" },
  "features": { "buildkit" : true }
}
EOF
```

```
添加构建日志限制
$ vi /etc/systemd/system/multi-user.target.wants/docker.service
[Service]
Environment="BUILDKIT_STEP_LOG_MAX_SIZE=1073741824"
Environment="BUILDKIT_STEP_LOG_MAX_SPEED=10240000"
```

	启动服务
	$ systemctl daemon-reload
	$ systemctl enable docker
	$ systemctl restart docker
	
	清理缓存
	$ docker system df
	$ docker system prune
	
### 启动服务
	安装 nginx、certbot
	$ apt-get install -y nginx certbot python3-certbot-nginx
	$ systemctl enable nginx
	$ systemctl restart nginx
	$ systemctl status nginx
	
	开发组件
	$ ssh-keygen -m PEM -t rsa -b 4096 -C "renlm@21cn.com"
	$ git clone git@gitee.com:renlm/MicroServer.git
	$ docker network create share
	$ docker network ls
	配置证书
	$ docker login --username=renlm@21cn.com registry.cn-hangzhou.aliyuncs.com
	$ docker-compose -f /root/MicroServer/repo/docker/docker-compose.yml up -d
	$ rm -fr /etc/nginx/conf.d && ln -sf /root/MicroServer/repo/nginx/conf.d /etc/nginx/conf.d
	$ ln -sf /root/MicroServer/repo/nginx/modules-enabled /etc/nginx/modules-enabled/custom
	$ nginx -v
	$ nginx -t
	$ nginx -s reload
	默认已开启定时续期
	$ certbot --nginx
	$ certbot certificates
	$ certbot renew --dry-run
	$ tail -f -n 100 /var/log/letsencrypt/letsencrypt.log
