#!/bin/bash
set -e

HOSTNAME=${POD_IP//./-}.${POD_SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
export HOSTNAME=$HOSTNAME
java -Djava.security.egd=file:/dev/./urandom -jar /app.jar