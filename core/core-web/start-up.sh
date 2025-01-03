#!/bin/bash
set -e

POD_FULLNAME=${POD_IP//./-}.${POD_SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
sed -i '$a'"export POD_FULLNAME=$POD_FULLNAME"'' ~/.bashrc
sed -i '$a'"$POD_IP" "$POD_FULLNAME"'' /etc/hosts
source ~/.bashrc

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar