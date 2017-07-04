#!/usr/bin/env bash

set -e

if [ $# -ne 2 ]; then
  echo "Usage: $0 [user-name] [deployment-bucket]"
  echo ''
  echo 'Options:'
  echo '  user-name          User name'
  echo '  deployment-bucket  S3 bucket name where contains compiled lambdas'
  exit 1
fi

USER=$1
S3_BUCKET=$2
STACK_NAME="${USER}-stack"
BUILD=`date -u +%Y-%m-%dT%H:%M:%SZ`

echo 'Create/Update stack initialized'
for MODULE in 'server' 'basic-authenticator' 'jwt-authorizer'; do
  aws s3 cp "authorization-service-${MODULE}/target/authorization-service-${MODULE}-1.0-SNAPSHOT.jar" \
    "s3://${S3_BUCKET}/${USER}/${BUILD}/"
done

aws cloudformation deploy --template-file 'stack.json' --stack-name ${STACK_NAME} \
  --parameter-overrides UserName=${USER} DeploymentBucket=${S3_BUCKET} Build=${BUILD}
