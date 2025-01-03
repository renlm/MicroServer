#!/bin/bash
set -e

INSTANCE_NAME=${INSTANCE_IP//./-}.${POD_NAME}.${POD_NAMESPACE}.svc.cluster.local
export INSTANCE_NAME=$INSTANCE_NAME

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar