#!/bin/bash
set -e

INSTANCE_NAME=${POD_IP//./-}.${POD_SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
export INSTANCE_NAME=$INSTANCE_NAME
java -Djava.security.egd=file:/dev/./urandom -jar /app.jar