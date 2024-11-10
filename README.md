# 微服务模板
## Git
	$ apt update
	$ apt install -y git
	$ git --version
	$ git config --global user.name "renlm"
	$ git config --global user.email "renlm@21cn.com"
	$ ssh-keygen -m PEM -t rsa -b 2048 -C "renlm@21cn.com"
	$ cat ~/.ssh/id_rsa.pub
	$ git clone -b master --single-branch git@gitee.com:renlm/MicroServer.git
