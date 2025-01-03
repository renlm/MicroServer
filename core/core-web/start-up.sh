#!/bin/bash
set -e

INSTANCE_HOSTNAME=${POD_IP//./-}.${SERVICE_NAME}.${POD_NAMESPACE}.svc.cluster.local
export INSTANCE_HOSTNAME=$INSTANCE_HOSTNAME

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar