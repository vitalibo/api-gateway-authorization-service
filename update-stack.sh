#!/usr/bin/env bash

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

check_result() {
  if [ $? -ne 0 ]; then
     echo "error: deploy failed"
     exit 1
  fi
}

function copy() {
  SERVICE_NAME=$1
  aws s3 cp "api-gateway-${SERVICE_NAME}/target/api-gateway-${SERVICE_NAME}-1.0-SNAPSHOT.jar" "s3://${S3_BUCKET}/${USER}/"
  check_result
}

function update_lambda() {
  aws lambda update-function-code --function-name "$1" \
    --s3-bucket "${S3_BUCKET}" --s3-key "${USER}/api-gateway-$2-1.0-SNAPSHOT.jar" 2>&1 >/dev/null
  check_result
}

copy 'authorization-server'
copy 'basic-authenticator'
copy 'jwt-authorizer'

aws cloudformation deploy --template-file 'stack.json' --stack-name ${STACK_NAME} \
  --parameter-overrides UserName=${USER} DeploymentBucket=${S3_BUCKET}

update_lambda "${USER}-server" 'authorization-server'
update_lambda "${USER}-basic-authenticator" 'basic-authenticator'
update_lambda "${USER}-jwt-authorizer" 'jwt-authorizer'
