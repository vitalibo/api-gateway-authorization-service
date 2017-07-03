#!/usr/bin/env bash

if [ $# -ne 1 ]; then
  echo "Usage: $0 [user-name]"
  echo ''
  echo 'Options:'
  echo '  user-name  User name'
  exit 1
fi

USER=$1
STACK_NAME="${USER}-sample"

aws cloudformation deploy --template-file 'sample.json' --stack-name ${STACK_NAME} \
  --parameter-overrides UserName=${USER}  --capabilities 'CAPABILITY_NAMED_IAM' --no-execute-changeset
