# K3S（Ubuntu）

## 修改 hostname
	立即生效
	$ hostname -F /etc/hostname
	$ hostname

## 检查 DNS 配置
	# Nameserver limits were exceeded
	# Too many DNS servers configured, the following entries may be ignored.
	$ cat /run/systemd/resolve/resolv.conf
	
	注释掉多余的DNS servers
	$ vi /etc/systemd/resolved.conf
	
	重启服务
	$ systemctl restart systemd-resolved
	
## 系统参数
	# failed to create fsnotify watcher: too many open files
	$ sysctl -n fs.inotify.max_user_instances
	$ echo fs.inotify.max_user_instances = 1024 | tee -a /etc/sysctl.conf && sysctl -p
	
```
Enabling CPU, CPUSET, and I/O delegation(only cgroup v2)
By default, a non-root user can only get memory controller and pids controller to be delegated.
https://rootlesscontaine.rs/getting-started/common/cgroup2/	
https://github.com/opencontainers/runc/blob/main/docs/cgroup-v2.md

对于 cgroup v1，输出为 tmpfs
对于 cgroup v2，输出为 cgroup2fs
$ stat -fc %T /sys/fs/cgroup

$ cat /sys/fs/cgroup/user.slice/user-$(id -u).slice/user@$(id -u).service/cgroup.controllers
memory pids

To allow delegation of other controllers such as cpu, cpuset, and io, run the following commands:
$ mkdir -p /etc/systemd/system/user@.service.d
$ cat <<EOF | tee /etc/systemd/system/user@.service.d/delegate.conf
[Service]
Delegate=cpu cpuset io memory pids
EOF
$ systemctl daemon-reload
```

## 配置镜像代理
	设置主节点host
	安装的每个节点机器都执行
	$ sed -i '$a 192.168.16.3 k3s.master' /etc/hosts
	
```
https://docs.k3s.io/zh/installation/private-registry
$ mkdir -p /etc/rancher/k3s \
&& cat <<-'EOF' | tee /etc/rancher/k3s/registries.yaml
mirrors:
  docker.io:
    endpoint:
    - https://docker.1ms.run
    - https://docker.renlm.cn
  gcr.io:
    endpoint:
    - https://gcr.io.renlm.site
  ghcr.io:
    endpoint:
    - https://ghcr.io.renlm.site
  quay.io:
    endpoint:
    - https://quay.io.renlm.site
  registry.k8s.io:
    endpoint:
    - https://registry.k8s.io.renlm.site
EOF
```
	
## 安装 helm
	Helm版本支持策略
	https://helm.sh/docs/topics/version_skew/
	https://github.com/helm/helm/releases/
	
	master节点即可
	$ wget https://github.renlm.cn/download/helm-v3.16.4-linux-amd64.tar.gz \
        && tar -zxvf helm-v3.16.4-linux-amd64.tar.gz -C /usr/local/ --transform="s/linux-amd64/helm-v3.16.4/g" \
        && ln -sf /usr/local/helm-v3.16.4 /usr/local/helm \
        && sed -i '$a export PATH=/usr/local/helm:$PATH' ~/.bashrc \
        && source ~/.bashrc \
        && helm version
	
## 安装 k3s
	https://www.suse.com/suse-rancher/support-matrix/all-supported-versions/rancher-v2-10-1/
	https://docs.rancher.cn/docs/k3s/installation/ha/_index/
	https://github.com/k3s-io/k3s/releases/
	
	restorecon
	$ apt-get update
	$ apt-get install -y policycoreutils
		
```	
# master主节点
# 禁用traefik，安装istio替代
$ curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | \
    INSTALL_K3S_MIRROR=cn \
    INSTALL_K3S_VERSION=v1.31.4+k3s1 \
    K3S_TOKEN=SECRET \
    sh -s - server \
    --disable=traefik \
    --tls-san k3s.master \
    --tls-san kubernetes.renlm.cn \
    --cluster-init
```

```	
# master从节点
$ curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | \
    INSTALL_K3S_MIRROR=cn \
    INSTALL_K3S_VERSION=v1.31.4+k3s1 \
    K3S_TOKEN=SECRET \
    sh -s - server \
    --disable=traefik \
    --server https://k3s.master:6443
```

```	
# agent节点
$ curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | \
    INSTALL_K3S_MIRROR=cn \
    INSTALL_K3S_VERSION=v1.31.4+k3s1 \
    K3S_TOKEN=SECRET \
    sh -s - agent \
    --server https://k3s.master:6443
```

## 验证 k3s
	master主节点
	$ cp /etc/rancher/k3s/k3s.yaml /etc/rancher/k3s/KUBECONFIG.yaml \
        && sed -i '$a export KUBECONFIG=/etc/rancher/k3s/KUBECONFIG.yaml' ~/.bashrc \
        && sed -i '$a alias kubectl="k3s kubectl"' ~/.bashrc \
        && sed -i '$a alias ctr="k3s ctr"' ~/.bashrc \
        && sed -i '$a alias crictl="k3s crictl"' ~/.bashrc \
        && source ~/.bashrc \
        && kubectl get nodes \
        && ctr -n k8s.io c ls \
        && kubectl version --output=json

## 安装 cert-manager
	https://cert-manager.io/docs/installation/helm/
	$ wget https://github.renlm.cn/cert-manager/cert-manager/releases/download/v1.16.2/cert-manager.yaml \
        && kubectl apply -f cert-manager.yaml \
        && kubectl get pods --namespace cert-manager
	
## 安装 istio
	https://istio.io/latest/docs/setup/additional-setup/download-istio-release/
	https://github.com/istio/istio/releases
	
	master节点即可
	$ wget https://github.renlm.cn/download/istio-1.24.2-linux-amd64.tar.gz \
        && tar -zxvf istio-1.24.2-linux-amd64.tar.gz -C /usr/local/ \
        && ln -sf /usr/local/istio-1.24.2 /usr/local/istio \
        && sed -i '$a export ISTIO_PATH=/usr/local/istio' ~/.bashrc \
        && sed -i '$a export PATH=$ISTIO_PATH/bin:$PATH' ~/.bashrc \
        && source ~/.bashrc \
        && istioctl version
	
	安装istio组件
	$ istioctl install -y --set profile=minimal \
        && wget https://github.renlm.cn/helm/istio.install.yaml \
        && istioctl install -y -f istio.install.yaml
	
	可视化
	https://istio.io/latest/zh/docs/setup/additional-setup/sidecar-injection/#manual-sidecar-injection
	$ kubectl apply -f $ISTIO_PATH/samples/addons/prometheus.yaml
	$ kubectl apply -f $ISTIO_PATH/samples/addons/loki.yaml
	$ kubectl apply -f $ISTIO_PATH/samples/addons/kiali.yaml
	
	https://opentelemetry.io/docs/kubernetes/operator/
	$ kubectl create namespace observability \
        && kubectl label namespace observability istio-injection=enabled \
        && wget https://github.renlm.cn/open-telemetry/opentelemetry-operator/releases/latest/download/opentelemetry-operator.yaml \
        && sed -i '/- --enable-nginx-instrumentation=true/a\        - --enable-multi-instrumentation=true' opentelemetry-operator.yaml \
        && sed -i '/- --enable-nginx-instrumentation=true/a\        - --enable-go-instrumentation=true' opentelemetry-operator.yaml \
        && kubectl apply -f opentelemetry-operator.yaml
	
## 安装 rancher
	配置网关（外部 Nginx 负载均衡）
	修改域名为自己的
	$ wget https://github.renlm.cn/helm/istio.rancher.yaml
	$ kubectl apply -f istio.rancher.yaml
	$ kubectl describe certificate -n istio-system
	
	证书申请失败，删除certificate手动重试
	$ kubectl describe challenges --all-namespaces
	$ kubectl delete certificate istio-gateway -n istio-system
	$ kubectl apply -f istio.rancher.yaml

	添加 Helm Chart 仓库
	https://ranchermanager.docs.rancher.com/zh/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster
	$ helm repo add rancher-stable https://releases.rancher.com/server-charts/stable
	$ helm search repo rancher
	
	安装 rancher-stable/rancher v2.10.1
	禁用ingress，使用istio网关进行代理和加密
	https://ranchermanager.docs.rancher.com/zh/getting-started/installation-and-upgrade/installation-references/helm-chart-options
	$ kubectl create namespace cattle-system
	$ helm fetch rancher-stable/rancher --version=v2.10.1
	$ helm install rancher ./rancher-2.10.1.tgz \
        --namespace cattle-system \
        --set hostname=rancher.renlm.cn \
        --set ingress.enabled=false \
        --set replicas=1
	
	在外部的 L7 负载均衡器上终止 Rancher 的 SSL/TLS
	Istio numTrustedProxies 大于0时，Envoy 将开启 X-Forwarded-Proto、X-Forwarded-Port、X-Forwarded-For
	同时，istio-ingressgateway 中 k8s.service.externalTrafficPolicy 设置为 Local，可保留客户端访问来源IP
	Rancher 服务的 80 端口默认会进行 302 重定向，当 X-Forwarded-Proto 为 https 时停止
	https://istio.io/latest/zh/docs/reference/config/istio.mesh.v1alpha1/#Topology
	https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/headers#config-http-conn-man-headers-x-forwarded-for
	$ curl -i -HHost:rancher.renlm.cn -HX-Forwarded-Proto:https "http://{CLUSTER-IP}/dashboard/"
	
	查看安装情况，完成后根据输出提示获取随机登录密码（admin）
	$ kubectl -n cattle-system get deploy rancher
	$ kubectl -n cattle-system rollout status deploy/rancher
	
	查看资源占用率
	$ kubectl top nodes
	$ kubectl top pods -A
	
	重置密码（admin）
	$ kubectl get pods -n cattle-system -o wide
	$ kubectl -n cattle-system exec -it [POD_NAME] -- reset-password
	
	卸载
	$ helm ls -A
	$ helm uninstall rancher -n cattle-system
	
## 演示案例
	创建namespace并开启istio自动注入，安装fleet仓库micro
	https://gitee.com/renlm/MicroServer.git
	$ kubectl create namespace mylb
    $ kubectl label namespace mylb istio-injection=enabled
    
    编辑kiali的configmap
    找到external_services添加Grafana地址
    保存后重启kiali
    grafana:
        url: http://grafana.istio-system:3000
    
    
	主页链接添加代理访问地址
	Grafana（admin：M1h62Gj3Uy54r）：https://grafana.renlm.cn
	Prometheus：https://rancher.renlm.cn/api/v1/namespaces/istio-system/services/http:prometheus:9090/proxy
	灰度演示（default：123654）：https://istio.renlm.cn
	注册中心（default：M1h62Gj3Uy54r）：https://istio.renlm.cn/registrar
	Jenkins（admin：M1h62Gj3Uy54r）：https://jenkins.renlm.cn
	Mygraph（S-linghc：^M1h62Gj3Uy54r）：https://mygraph.renlm.cn

## MTU 设置（可选）
	使用 rancher 创建的集群
	为保障通信，集群节点规格不一致时，需要统一MTU
	以值最小的那个节点为基准
	https://projectcalico.docs.tigera.io/networking/mtu
	https://docs.rke2.io/install/network_options

	查看网卡MTU
	$ ip a | grep eth0
	
	MTU检测
	$ ping -s 1451 -M do {目标IP或域名}
	以MTU=1450为例，选用Calico MTU with VXLAN (IPv4)，集群MTU应设置为：1400（差值50 = 1450 - 1400）
	如果使用WireGuard进行跨云集群节点的网络穿透，1400 - 80(40[IPv6] + 32[WireGuard] + 8[ICMP]) = 1320
	运营商可能对UDP数据包有限制，如果使用wstunnel对WireGuard流量进行TCP包装时，MTU = 1320 - 60(40[IPv6] + 20[TCP]) = 1260
	
```
# 协议消耗
IPv4 – 20 Bytes
IPv6 – 40 Bytes
UDP – 8 Bytes
TCP – 20 Bytes
WireGuard – 32 Bytes
ICMP – 8 Bytes
PPPoE – 8 Bytes
```

```
# 修改方式一：
# 创建集群时，[附加配置] 添加参数
installation:
  calicoNetwork:
    mtu: 1260
```
	
```
# 修改方式二：
# 命令修改
kubectl patch installation.operator.tigera.io default --type merge -p '{"spec":{"calicoNetwork":{"mtu":1260}}}'
```

