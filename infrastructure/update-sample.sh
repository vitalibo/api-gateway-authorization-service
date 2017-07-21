#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
  echo "Usage: $0 [user-name]"
  echo ''
  echo 'Options:'
  echo '  user-name  User name'
  exit 1
fi

USER=$1
STACK_NAME="${USER}-mock-api"

aws cloudformation deploy --template-file 'sample.json' --stack-name ${STACK_NAME} \
  --parameter-overrides UserName=${USER}  --capabilities 'CAPABILITY_NAMED_IAM'

echo 'Customizing stack configuration'
MOCK_REST_API=$( aws cloudformation describe-stacks --stack-name ${STACK_NAME} \
  --query 'Stacks[0].Outputs[?OutputKey==`MockRestApi`].OutputValue' --output text )

for NAME in 'AUTHORIZER_CONFIGURATION_ERROR' ; do
  aws apigateway update-gateway-response --rest-api-id ${MOCK_REST_API} \
    --cli-input-json file://configuration/response/${NAME}.json 2>&1 >/dev/null
done

echo 'Done'
