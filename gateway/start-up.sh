#!/bin/bash
set -e

HOSTNAME=${POD_IP//./-}.${RELEASE_NAME}.${POD_NAMESPACE}.svc.cluster.local
export HOSTNAME=$HOSTNAME
java -Djava.security.egd=file:/dev/./urandom -jar /app.jar