#!/bin/bash
set -e

INSTANCE_NAME=${POD_IP//./-}.${SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
export HOSTNAME=$INSTANCE_NAME

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar