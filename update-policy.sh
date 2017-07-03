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
STACK_NAME="${USER}-policy"

aws cloudformation deploy --template-file 'policy.json' --stack-name ${STACK_NAME} --capabilities 'CAPABILITY_NAMED_IAM' \
  --parameter-overrides UserName=${USER} DeploymentBucket=${S3_BUCKET} --no-execute-changeset
