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

function describe_stacks() {
  aws cloudformation describe-stacks --stack-name ${STACK_NAME} \
    --query 'Stacks[0].Outputs[?OutputKey==`'${1}'`].OutputValue' --output text
}

MOCK_REST_API=`describe_stacks 'MockRestApi'`
STAGE_NAME=`describe_stacks 'StageName'`

for NAME in 'ACCESS_DENIED' 'AUTHORIZER_CONFIGURATION_ERROR' 'UNAUTHORIZED' ; do
  aws apigateway update-gateway-response --rest-api-id ${MOCK_REST_API} \
    --cli-input-json file://configuration/response/${NAME}.json 2>&1 >/dev/null
done

aws apigateway create-deployment --rest-api-id ${MOCK_REST_API} --stage-name ${STAGE_NAME} \
  --description 'Customize stack configuration' 2>&1 >/dev/null
echo 'Done'
