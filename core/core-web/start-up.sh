#!/bin/bash
set -e

POD_FULLNAME=${POD_IP//./-}.${POD_SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
echo "$(sed '$a'"$POD_IP $POD_FULLNAME"'' /etc/hosts)" > /etc/hosts

export POD_FULLNAME=$POD_FULLNAME
java -Djava.security.egd=file:/dev/./urandom -jar /app.jar