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
  aws s3 cp "authorization-service-${SERVICE_NAME}/target/authorization-service-${SERVICE_NAME}-1.0-SNAPSHOT.jar" "s3://${S3_BUCKET}/${USER}/" --profile my
  check_result
}

function update_lambda() {
  aws lambda update-function-code --function-name "${USER}-$1" \
    --s3-bucket "${S3_BUCKET}" --s3-key "${USER}/authorization-service-$1-1.0-SNAPSHOT.jar" --profile my 2>&1 >/dev/null
  check_result
}

copy 'server'
copy 'basic-authenticator'
copy 'jwt-authorizer'

aws cloudformation deploy --template-file 'stack.json' --stack-name ${STACK_NAME} \
  --parameter-overrides UserName=${USER} DeploymentBucket=${S3_BUCKET} --profile my

update_lambda 'server'
update_lambda 'basic-authenticator'
update_lambda 'jwt-authorizer'
